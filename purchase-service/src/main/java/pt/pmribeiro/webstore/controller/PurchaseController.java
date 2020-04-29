package pt.pmribeiro.webstore.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import pt.pmribeiro.webstore.dto.Purchase;
import pt.pmribeiro.webstore.exceptions.DataIntegrityViolationException;
import pt.pmribeiro.webstore.exceptions.NotFoundException;
import pt.pmribeiro.webstore.exceptions.ValidationException;
import pt.pmribeiro.webstore.service.IPurchaseService;
import pt.pmribeiro.webstore.validators.PurchaseValidator;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * {@code PurchaseController} implements the endpoints of the Purchase Service API
 * @todo: check if request mapping with version is necessary
 *
 * For more detailed information about the api endpoints and
 * functionalities start the application and check the following
 * url: http://localhost:9000/docs/index.html
 *
 * Created by pribeiro on 27/11/2016.
 */
@RestController
@RequestMapping(path = "/api")
public class PurchaseController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PurchaseController.class);

    private IPurchaseService purchaseService;

    @Autowired
    private PurchaseValidator purchaseValidator;

    @Autowired
    public PurchaseController(IPurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    /**
     * Adds validators to binder
     * @param binder
     */
    @InitBinder
    public void dataBinding(WebDataBinder binder) {
        binder.addValidators(purchaseValidator); // add purchaseValidator to binders
    }

    /**
     * Gets a list of valid purchases
     *
     * @return list of valid purchases
     */
    @RequestMapping(path = "/purchases", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, name = "getValidPurchases")
    @ResponseBody
    @HystrixCommand
    public ResponseEntity<List<Purchase>> getValidPurchases() {
        logger.info("Fetching all valid purchases");
        return Optional.ofNullable(purchaseService.getValidPurchases())
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    /**
     * Saves a given purchase
     *
     * @param purchase
     * @param bindingResult
     * @return saved purchase
     */
    @RequestMapping(path = "/purchases", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, name = "savePurchase")
    @ResponseBody
    @HystrixCommand
    public ResponseEntity savePurchase(@RequestBody @Valid Purchase purchase, BindingResult bindingResult) throws Exception {
        logger.info("Saving purchase");

        // check if purchase information is valid
        if (bindingResult.hasErrors()) {
            throw new ValidationException("Invalid purchase", bindingResult);
        }

        return Optional.ofNullable(purchaseService.createPurchase(purchase))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new Exception("Unable to create purchase"));
    }

    /**
     * Updates a existing purchase
     *
     * @param purchaseId
     * @param purchase
     * @param bindingResult
     * @return updated purchase
     */
    @RequestMapping(path = "/purchases/{purchaseId}", method = {RequestMethod.PUT, RequestMethod.PATCH}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, name = "saveOrUpdatePurchase")
    @ResponseBody
    //@HystrixCommand
    public ResponseEntity updatePurchase(@PathVariable("purchaseId") Long purchaseId, @RequestBody @Valid Purchase purchase, BindingResult bindingResult) throws Exception {
        logger.info("Updating purchase");

        // fetch existing purchase
        Purchase currentPurchase = purchaseService.findPurchaseById(purchaseId);
        // if purchase not found throws exception
        if (currentPurchase == null) {
            throw new NotFoundException("Purchase " + purchaseId + " not found");
        }

        // check if purchase information is valid
        if (bindingResult.hasErrors()) {
            throw new ValidationException("Invalid purchase", bindingResult);
        }

        // copy properties in purchase to old purchase
        currentPurchase.setProductType(purchase.getProductType());
        currentPurchase.setPurchaseDetails(purchase.getPurchaseDetails());

        // updates and returns saved values
        return Optional.ofNullable(purchaseService.updatePurchase(currentPurchase))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new Exception("Unable to create purchase"));
    }

    /**
     * Handles the validation exception  throned by the api
     * and build the http response
     *
     * @param exception
     * @return validation errors list
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    @HystrixCommand
    public @ResponseBody Map<String, Object> handleInvalidRequestException(ValidationException exception) {
        List<Map<String, Object>> fieldErrorResources = Lists.newArrayList();

        // build list with errors founded
        List<FieldError> fieldErrors = exception.getErrors().getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            Map<String, Object> fieldErrorResource = Maps.newHashMap();

            fieldErrorResource.put("resource", fieldError.getObjectName());
            fieldErrorResource.put("field", fieldError.getField());
            fieldErrorResource.put("code", fieldError.getCode());
            fieldErrorResource.put("message", fieldError.getDefaultMessage());
            fieldErrorResources.add(fieldErrorResource);
        }

        // set information to return
        Map<String, Object> error = Maps.newHashMap();
        error.put("code", "InvalidPurchase");
        error.put("message", exception.getMessage());
        error.put("fieldErrors", fieldErrorResources);

        return error;
    }

    /**
     * Handles the data integrity violation exception throned by the api
     * and build the http response
     *
     * @param exception
     * @return error information
     */
    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public @ResponseBody
    @HystrixCommand
    Map<String, Object> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        Map<String, Object> error = Maps.newHashMap();
        error.put("code", "DataIntegrityViolation");
        error.put("message", exception.getMessage());

        return error;
    }

    /**
     * Handles the not found exception throned by the api
     * and build the http response
     *
     * @param exception
     * @return error information
     */
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    @HystrixCommand
    public @ResponseBody Map<String, Object> handleNotFoundException(NotFoundException exception) {
        Map<String, Object> error = Maps.newHashMap();
        error.put("code", "NotFound");
        error.put("message", exception.getMessage());

        return error;
    }

}
