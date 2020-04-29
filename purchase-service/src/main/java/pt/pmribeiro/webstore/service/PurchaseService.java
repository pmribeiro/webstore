package pt.pmribeiro.webstore.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.pmribeiro.webstore.dao.IPurchaseDAO;
import pt.pmribeiro.webstore.dto.Purchase;
import pt.pmribeiro.webstore.exceptions.DataIntegrityViolationException;
import pt.pmribeiro.webstore.exceptions.NotFoundException;
import pt.pmribeiro.webstore.exceptions.ValidationException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * {@code PurchaseService} handles the business rules implementation class for the purchase service
 *
 * Created by pribeiro on 26/11/2016.
 */
@Service
public class PurchaseService implements IPurchaseService {

    private IPurchaseDAO purchaseRepository;

    @Autowired
    public PurchaseService(IPurchaseDAO purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    /**
     * Gets a list of valid purchases id
     * Definition: A valid purchase has the expiration date bigger or equals to the current system date
     *
     * @param purchasesList list of purchases to find valid purchases
     * @return list of valid purchases ids
     */
    @HystrixCommand
    protected Long[] getValidPurchasesIds(List<Purchase> purchasesList) {
        long now = System.currentTimeMillis();
        return purchasesList.parallelStream() // @todo: needs more info to decide to use parallelStream or stream -> for now consider its a big stream
                .filter(purchase -> purchase.getExpires().getTime() >= now) // apply filter to the collection to return valid purchases
                .map(purchase -> purchase.getId()) // extract only the id
                .toArray(size -> new Long[size]); // convert to Ling array
    }

    /**
     * @see IPurchaseService
     */
    @Override
    @HystrixCommand
    public List<Purchase> getValidPurchases() {
        List<Purchase> purchasesList = purchaseRepository.getAll(); // get all purchases from database
        Long[] validPurchasesIds = getValidPurchasesIds(purchasesList); // get only valid ids
        return purchaseRepository.getPurchasesDetailByPurchasesIds(validPurchasesIds); // return purchases with detail
    }

    /**
     * @see IPurchaseService
     */
    @Override
    @HystrixCommand
    public Purchase findPurchaseById(Long id) throws NotFoundException {
        return Optional.ofNullable(purchaseRepository.findById(id))
                .map(result -> result)
                .orElseThrow(() -> new NotFoundException("Purchase " + id + " not found"));
    }

    /**
     * Gets a new expiration date for the purchase
     * The expiration date is calculated based on the current time plus one hour
     *
     * @return expiration date of the purchase
     */
    protected Date getNewExpirationDate() {
        Calendar expirationDate = Calendar.getInstance();
        expirationDate.add(Calendar.HOUR, 1); // adds one hour to the current time
        return expirationDate.getTime();
    }

    /**
     * @see IPurchaseService
     */
    @Override
    @HystrixCommand
    public Purchase createPurchase(Purchase purchase) throws DataIntegrityViolationException, ValidationException {
        if (purchaseRepository.exists(purchase.getId())) { // validates if purchase id already exists in database
            throw new DataIntegrityViolationException("Purchase id already in use"); // thows DataIntegrityViolationException if exits
        }

        purchase.setExpires(getNewExpirationDate()); // set expiration date

        return purchaseRepository.save(purchase); // save to repository and returns final value
    }

    /**
     * @see IPurchaseService
     */
    @Override
    @HystrixCommand
    public Purchase updatePurchase(Purchase purchase) throws ValidationException {
        purchase.setExpires(getNewExpirationDate()); // set expiration date
        return purchaseRepository.update(purchase); // save to repository and returns final value
    }

    /**
     * @see IPurchaseService
     */
    @Override
    @HystrixCommand
    public boolean purchaseExists(Long id) {
        return purchaseRepository.exists(id);
    }

}
