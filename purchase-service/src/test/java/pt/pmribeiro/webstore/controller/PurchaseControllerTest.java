package pt.pmribeiro.webstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import pt.pmribeiro.webstore.dto.Purchase;
import pt.pmribeiro.webstore.dto.PurchaseDetail;
import pt.pmribeiro.webstore.exceptions.DataIntegrityViolationException;
import pt.pmribeiro.webstore.exceptions.NotFoundException;
import pt.pmribeiro.webstore.exceptions.ValidationException;
import pt.pmribeiro.webstore.service.IPurchaseService;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by pribeiro on 26/11/2016.
 */
@SpringBootTest
public class PurchaseControllerTest {

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("apidocs/springgen"); // used for build documentation

    @Autowired
    private MockMvc mvc;

    @InjectMocks
    @Autowired
    private PurchaseController purchaseController;

    @Mock
    private IPurchaseService purchaseService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        assertNotNull(purchaseService);
        assertNotNull(purchaseController);

        mvc = MockMvcBuilders.standaloneSetup(purchaseController)
                .apply(documentationConfiguration(this.restDocumentation))
                .alwaysDo(document("{method-name}/{step}/"))
                .build();
        assertNotNull(mvc);
    }

    // GET TESTS

    @Test
    public void testGetValidPurchasesWithContent() throws Exception {
        Date nowDate = new Date();

        List<Purchase> validPurchases = Lists.newArrayList();
        validPurchases.add(new Purchase(1L, "Cake", nowDate, Lists.newArrayList()));
        validPurchases.add(new Purchase(2L, "Tools", nowDate, Lists.newArrayList()));

        given(purchaseService.getValidPurchases()).willReturn(validPurchases);

        mvc.perform(get("/api/purchases").accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk()).andExpect(content().json(objectMapper.writeValueAsString(validPurchases)));
    }

    @Test
    public void testGetValidPurchasesWithNoContent() throws Exception {
        given(purchaseService.getValidPurchases()).willReturn(null);

        mvc.perform(get("/api/purchases").accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNoContent());
    }

    // SAVE TESTS

    @Test
    public void testSavePurchase() throws Exception {
        Date nowDate = new Date();

        Purchase purchase = Purchase.builder().id(1L).productType("Cake").expires(nowDate).purchaseDetails(
                Lists.newArrayList(
                        PurchaseDetail.builder().id(1L).description("XXL wedding cake").quantity(1).value(500.00).build()
                )
        ).build();

        given(purchaseService.createPurchase(purchase)).willReturn(purchase);

        mvc.perform(post("/api/purchases").accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(purchase)))
                .andExpect(status().isCreated()).andExpect(content().json(objectMapper.writeValueAsString(purchase)));
    }

    @Test
    public void testSavePurchaseDataIntegrityViolationException() throws Exception {
        Date nowDate = new Date();

        Purchase purchase = Purchase.builder().id(1L).productType("Cake").expires(nowDate).purchaseDetails(
                Lists.newArrayList(
                        PurchaseDetail.builder().id(1L).description("XXL wedding cake").quantity(1).value(500.00).build()
                )
        ).build();

        given(purchaseService.createPurchase(purchase)).willThrow(new DataIntegrityViolationException("Purchase id already in use"));

        Map<String, String> expectedJsonMessage = Maps.newHashMap();
        expectedJsonMessage.put("code", "DataIntegrityViolation");
        expectedJsonMessage.put("message", "Purchase id already in use");

        mvc.perform(post("/api/purchases").accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(purchase)))
                .andExpect(status().isConflict()).andExpect(content().json(objectMapper.writeValueAsString(expectedJsonMessage)));
    }

    @Test
    public void testSavePurchaseValidationException() throws Exception {
        Date nowDate = new Date();

        Purchase purchase = Purchase.builder().id(1L).expires(nowDate).purchaseDetails(
                Lists.newArrayList(
                        PurchaseDetail.builder().id(1L).description("XXL wedding cake").value(500.00).build()
                )
        ).build();

        BindingResult dummyBindingResult = new BeanPropertyBindingResult(purchase, "purchase");
        dummyBindingResult.rejectValue("productType", "","ProductType is empty");
        dummyBindingResult.rejectValue("purchaseDetails[0].quantity", "", "PurchaseDetail quantity is null or equals to 0");
        given(purchaseService.createPurchase(purchase)).willThrow(new ValidationException("Invalid purchase", dummyBindingResult));

        List<Map<String, String>> fieldErrors = Lists.newArrayList();
        Map<String, String> productTypeFieldError = Maps.newHashMap();
        productTypeFieldError.put("code", "");
        productTypeFieldError.put("field", "productType");
        productTypeFieldError.put("resource", "purchase");
        productTypeFieldError.put("message", "ProductType is empty");
        fieldErrors.add(productTypeFieldError);

        Map<String, String> purchaseDetailsQuantityfieldError = Maps.newHashMap();
        purchaseDetailsQuantityfieldError.put("code", "");
        purchaseDetailsQuantityfieldError.put("field", "purchaseDetails[0].quantity");
        purchaseDetailsQuantityfieldError.put("resource", "purchase");
        purchaseDetailsQuantityfieldError.put("message", "PurchaseDetail quantity is null or equals to 0");
        fieldErrors.add(purchaseDetailsQuantityfieldError);

        Map<String, Object> expectedJsonMessage = Maps.newHashMap();
        expectedJsonMessage.put("code", "InvalidPurchase");
        expectedJsonMessage.put("message", "Invalid purchase");
        expectedJsonMessage.put("fieldErrors", fieldErrors);

        mvc.perform(post("/api/purchases").accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(purchase)))
                .andExpect(status().isBadRequest()).andExpect(content().json(objectMapper.writeValueAsString(expectedJsonMessage)));
    }

    // UPDATE TESTS

    @Test
    public void testUpdatePurchase() throws Exception {
        Date nowDate = new Date();

        Purchase purchase = Purchase.builder().id(1L).productType("Cake").expires(nowDate).purchaseDetails(
                Lists.newArrayList(
                        PurchaseDetail.builder().id(1L).description("XXL wedding cake").quantity(1).value(100.00).build()
                )
        ).build();

        given(purchaseService.findPurchaseById(1L)).willReturn(purchase);
        given(purchaseService.updatePurchase(purchase)).willReturn(purchase);

        mvc.perform(put("/api/purchases/1").accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(purchase)))
                .andExpect(status().isOk()).andExpect(content().json(objectMapper.writeValueAsString(purchase)));
    }

    @Test
    public void testUpdatePurchaseDataIntegrityViolationException() throws Exception {
        Date nowDate = new Date();

        Purchase purchase = Purchase.builder().id(1L).productType("Cake").expires(nowDate).purchaseDetails(
                Lists.newArrayList(
                        PurchaseDetail.builder().id(1L).description("XXL wedding cake").quantity(1).value(500.00).build()
                )
        ).build();

        given(purchaseService.findPurchaseById(1L)).willReturn(purchase);
        given(purchaseService.updatePurchase(purchase)).willThrow(new DataIntegrityViolationException("Invalid purchase detail id"));

        Map<String, String> expectedJsonMessage = Maps.newHashMap();
        expectedJsonMessage.put("code", "DataIntegrityViolation");
        expectedJsonMessage.put("message", "Invalid purchase detail id");

        mvc.perform(put("/api/purchases/1").accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(purchase)))
                .andExpect(status().isConflict()).andExpect(content().json(objectMapper.writeValueAsString(expectedJsonMessage)));
    }

    @Test
    public void testUpdatePurchaseValidationException() throws Exception {
        Date nowDate = new Date();

        Purchase purchase = Purchase.builder().id(1L).expires(nowDate).purchaseDetails(
                Lists.newArrayList(
                        PurchaseDetail.builder().id(1L).description("XXL wedding cake").value(500.00).build()
                )
        ).build();

        given(purchaseService.findPurchaseById(1L)).willReturn(purchase);

        BindingResult dummyBindingResult = new BeanPropertyBindingResult(purchase, "purchase");
        dummyBindingResult.rejectValue("productType", "","ProductType is empty");
        dummyBindingResult.rejectValue("purchaseDetails[0].quantity", "", "PurchaseDetail quantity is null or equals to 0");
        given(purchaseService.updatePurchase(purchase)).willThrow(new ValidationException("Invalid purchase", dummyBindingResult));

        List<Map<String, String>> fieldErrors = Lists.newArrayList();
        Map<String, String> productTypeFieldError = Maps.newHashMap();
        productTypeFieldError.put("code", "");
        productTypeFieldError.put("field", "productType");
        productTypeFieldError.put("resource", "purchase");
        productTypeFieldError.put("message", "ProductType is empty");
        fieldErrors.add(productTypeFieldError);

        Map<String, String> purchaseDetailsQuantityfieldError = Maps.newHashMap();
        purchaseDetailsQuantityfieldError.put("code", "");
        purchaseDetailsQuantityfieldError.put("field", "purchaseDetails[0].quantity");
        purchaseDetailsQuantityfieldError.put("resource", "purchase");
        purchaseDetailsQuantityfieldError.put("message", "PurchaseDetail quantity is null or equals to 0");
        fieldErrors.add(purchaseDetailsQuantityfieldError);

        Map<String, Object> expectedJsonMessage = Maps.newHashMap();
        expectedJsonMessage.put("code", "InvalidPurchase");
        expectedJsonMessage.put("message", "Invalid purchase");
        expectedJsonMessage.put("fieldErrors", fieldErrors);

        mvc.perform(put("/api/purchases/1").accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(purchase)))
                .andExpect(status().isBadRequest()).andExpect(content().json(objectMapper.writeValueAsString(expectedJsonMessage)));
    }

    @Test
    public void testUpdatePurchaseNotFoundException() throws Exception {
        Date nowDate = new Date();

        Purchase purchase = Purchase.builder().id(10L).expires(nowDate).purchaseDetails(
                Lists.newArrayList(
                        PurchaseDetail.builder().id(1L).description("XXL wedding cake").value(500.00).build()
                )
        ).build();

        given(purchaseService.findPurchaseById(10L)).willThrow(new NotFoundException("Purchase 10 not found"));

        Map<String, Object> expectedJsonMessage = Maps.newHashMap();
        expectedJsonMessage.put("code", "NotFound");
        expectedJsonMessage.put("message", "Purchase 10 not found");

        mvc.perform(put("/api/purchases/10").accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(purchase)))
                .andExpect(status().isNotFound()).andExpect(content().json(objectMapper.writeValueAsString(expectedJsonMessage)));
    }

}