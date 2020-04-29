package pt.pmribeiro.webstore.dao;

import org.springframework.stereotype.Repository;
import pt.pmribeiro.webstore.dto.Purchase;
import pt.pmribeiro.webstore.exceptions.DataIntegrityViolationException;
import pt.pmribeiro.webstore.exceptions.NotFoundException;
import pt.pmribeiro.webstore.exceptions.ValidationException;

import java.util.List;

/**
 * Interface specification for PurchaseDAO bean
 * @todo: change methods signature to return Future to monitor the quality of service with timeout. SLA <= 2s
 *
 * Created by pribeiro on 26/11/2016.
 */
@Repository
public interface IPurchaseDAO {

    /**
     * Gets all purchases from a database
     *
     * @return list of all purchases in the database
     */
    List<Purchase> getAll();

    /**
     * Gets purchases from a database with the given ids
     *
     * @param ids list of the purchase ids to get from the database with the detail
     * @return list of purchases in the database
     */
    List<Purchase> getPurchasesDetailByPurchasesIds(Long... ids);

    /**
     * Get the purchase for the given id
     *
     * @param id the id of the purchase to get from the database
     * @return purchase in the database
     * @throws NotFoundException if purchase not found in database
     */
    Purchase findById(Long id) throws NotFoundException;

    /**
     * Save a given purchase in the database
     *
     * @param purchase the purchase model to save
     * @return purchase saved in database
     * @throws DataIntegrityViolationException if violates the integrity of the data in the database
     * @throws ValidationException if any of the field don't match the requirements
     */
    Purchase save(Purchase purchase) throws DataIntegrityViolationException, ValidationException;

    /**
     * Updates a given purchase in the database
     *
     * @param purchase the purchase model to update
     * @return purchase saved in database
     * @throws DataIntegrityViolationException if violates the integrity of the data in the database
     * @throws ValidationException if any of the field don't match the requirements
     */
    Purchase update(Purchase purchase) throws DataIntegrityViolationException, ValidationException;

    /**
     * Checks if a purchase with the given id exists in database
     *
     * @param id the purchase id
     * @return if the purchase exists in the database
     */
    boolean exists(Long id);

}
