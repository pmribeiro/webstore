package pt.pmribeiro.webstore.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import pt.pmribeiro.webstore.dto.Purchase;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@code PurchaseValidator} is the class responsible for the validation
 * of  the information of a purchase
 * NOTE: some validation requires more work
 *
 * Created by pribeiro on 26/11/2016.
 */
@Component
public class PurchaseValidator implements Validator {

    /**
     * Checks if clazz is instance of Purchase object
     *
     * @param clazz class
     * @see Validator
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return true;
        //return Purchase.class.isAssignableFrom(clazz);
    }

    /**
     * Validates information of the Purchase
     * and puts error messages in the errors parameter
     *
     * @param target purchase object
     * @param errors list of errors
     *
     * @see Validator
     */
    @Override
    public void validate(Object target, Errors errors) {
        Purchase purchase = (Purchase)target;

        if (purchase.getId() == null) {
            errors.rejectValue("id","", "Id is null");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "productType", "","ProductType is empty");

        if (purchase.getPurchaseDetails() == null || purchase.getPurchaseDetails().isEmpty()) {
            errors.rejectValue("purchaseDetails","", "PurchaseDetails is empty");
        } else {
            final AtomicInteger index = new AtomicInteger(0);
            purchase.getPurchaseDetails().stream().forEach(purchaseDetail -> {
                if (purchaseDetail.getId() == null) {
                    errors.rejectValue("purchaseDetails[" + index.get() + "].id", "", "PurchaseDetail id is null");
                }

                if (purchaseDetail.getDescription() == null || purchaseDetail.getDescription().isEmpty()) {
                    errors.rejectValue("purchaseDetails[" + index.get() + "].description", "", "PurchaseDetail description is null or empty");
                }

                if (purchaseDetail.getQuantity() == null || purchaseDetail.getQuantity() == 0) {
                    errors.rejectValue("purchaseDetails[" + index.get() + "].quantity", "", "PurchaseDetail quantity is null or equals to 0");
                }

                if (purchaseDetail.getValue() == null) {
                    errors.rejectValue("purchaseDetails[" + index.getAndIncrement() + "].value", "", "PurchaseDetail value is null");
                }
            });
        }
    }

}
