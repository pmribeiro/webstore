package pt.pmribeiro.webstore.service;

import pt.pmribeiro.webstore.dto.Purchase;
import pt.pmribeiro.webstore.exceptions.DataIntegrityViolationException;
import pt.pmribeiro.webstore.exceptions.NotFoundException;
import pt.pmribeiro.webstore.exceptions.ValidationException;

import java.util.List;

/**
 * {@code IPurchaseService} provide the required interfaces to
 * handles the business rules implementation for the purchase service
 *
 * Created by pribeiro on 26/11/2016.
 */
public interface IPurchaseService {

    /**
     * Gets a list of valid purchases
     * Definition: A valid purchase has the expiration date bigger or equals to the current system date
     *
     * @return list of valid purchases ids
     */
    List<Purchase> getValidPurchases();

    /**
     * Gets the purchase information for a given purchase id
     *
     * @param id the id of the purchase to retrive
     * @return purchase
     * @throws NotFoundException if purchase id not found
     */
    Purchase findPurchaseById(Long id) throws NotFoundException;

    /**
     * Creates a new purchasefindPurchaseById
     *
     * @param purchase the purchase to save
     * @return the purchase saved
     * @throws DataIntegrityViolationException if data integrity was violated
     * @throws ValidationException if any of the information in the purchase don't match the requirements
     */
    Purchase createPurchase(Purchase purchase) throws DataIntegrityViolationException, ValidationException;

    /**
     * Updates a existing purchase
     *
     * @param purchase the purchase to update
     * @return the purchase updated
     * @throws DataIntegrityViolationException if data integrity was violated
     * @throws ValidationException if any of the information in the purchase don't match the requirements
     */
    Purchase updatePurchase(Purchase purchase) throws DataIntegrityViolationException, ValidationException;;

    /**
     * Checks if a purchase with the given id exists in database
     *
     * @param id the purchase id
     * @return if the purchase exists in the database
     */
    boolean purchaseExists(Long id);

}
