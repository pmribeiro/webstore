package pt.pmribeiro.webstore.service;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import pt.pmribeiro.webstore.dao.IPurchaseDAO;
import pt.pmribeiro.webstore.dto.Purchase;
import pt.pmribeiro.webstore.exceptions.DataIntegrityViolationException;
import pt.pmribeiro.webstore.exceptions.ValidationException;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;

/**
 * Created by pribeiro on 26/11/2016.
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@WebMvcTest(UserVehicleController.class)
public class PurchaseServiceTest {

    @Mock
    private IPurchaseDAO purchaseRepository;

    @InjectMocks
    @Autowired
    private PurchaseService purchaseService;

    private List<Purchase> validPurchaseList;
    private List<Purchase> invalidPurchaseList;
    private List<Purchase> completePurchaseList;
    private Long[] validPurchasesIds;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        assertNotNull(purchaseRepository);
        assertNotNull(purchaseService);

        initTestData();
        assertNotNull(validPurchaseList);
        assertNotNull(invalidPurchaseList);
        assertNotNull(completePurchaseList);
        assertNotNull(validPurchasesIds);
    }

    private void initTestData() {
        Calendar validDate = Calendar.getInstance();
        validDate.add(Calendar.DAY_OF_MONTH, 10);

        Calendar invalidDate = Calendar.getInstance();
        invalidDate.add(Calendar.DAY_OF_MONTH, -10);

        validPurchaseList = Lists.newArrayList(
                new Purchase(1L, "Cake", validDate.getTime(), Lists.newArrayList()));
        invalidPurchaseList = Lists.newArrayList(
                new Purchase(2L, "Cake", invalidDate.getTime(), Lists.newArrayList()));

        completePurchaseList = Lists.newArrayList(validPurchaseList);
        completePurchaseList.addAll(invalidPurchaseList);

        validPurchasesIds = validPurchaseList.stream().map(p -> p.getId()).toArray(size -> new Long[size]);
    }

    @Test
    public void testGetValidPurchasesIds() {
        Assert.assertArrayEquals(validPurchasesIds, purchaseService.getValidPurchasesIds(completePurchaseList));
    }

    @Test
    public void testGetValidPurchases(){
        given(purchaseRepository.getAll()).willReturn(completePurchaseList);
        Assert.assertEquals(completePurchaseList, purchaseRepository.getAll());

        given(purchaseRepository.getPurchasesDetailByPurchasesIds(validPurchasesIds)).willReturn(validPurchaseList);
        Assert.assertEquals(validPurchaseList, purchaseRepository.getPurchasesDetailByPurchasesIds(validPurchasesIds));

        List validPurchases = purchaseService.getValidPurchases();
        assertNotNull(validPurchases);
        Assert.assertEquals(1, validPurchases.size());
        Assert.assertEquals(validPurchaseList, validPurchases);
    }

    @Test
    public void testFindPurchaseById() {
        given(purchaseRepository.findById(1L)).willReturn(validPurchaseList.get(0));
        Assert.assertEquals(validPurchaseList.get(0), purchaseService.findPurchaseById(1L));
    }

    @Test
    public void testCreatePurchase() {
        given(purchaseRepository.exists(1L)).willReturn(false);
        given(purchaseRepository.save(validPurchaseList.get(0))).willReturn(validPurchaseList.get(0));
        Assert.assertEquals(validPurchaseList.get(0), purchaseService.createPurchase(validPurchaseList.get(0)));
    }

    @Test(expected=DataIntegrityViolationException.class)
    public void testCreatePurchaseDataIntegrityViolationException() {
        given(purchaseRepository.exists(1L)).willReturn(true);
        purchaseService.createPurchase(validPurchaseList.get(0));
    }

    @Test(expected=ValidationException.class)
    public void testCreatePurchaseValidationException() {
        given(purchaseRepository.exists(1L)).willReturn(false);
        given(purchaseRepository.save(validPurchaseList.get(0))).willThrow(new ValidationException("", null));
        purchaseService.createPurchase(validPurchaseList.get(0));
    }

    @Test
    public void testUpdatePurchase() {
        given(purchaseRepository.update(validPurchaseList.get(0))).willReturn(validPurchaseList.get(0));
        Assert.assertEquals(validPurchaseList.get(0), purchaseService.updatePurchase(validPurchaseList.get(0)));
    }

    @Test(expected=ValidationException.class)
    public void testUpdatePurchaseValidationException() {
        given(purchaseRepository.update(validPurchaseList.get(0))).willThrow(new ValidationException("", null));
        Assert.assertEquals(validPurchaseList.get(0), purchaseService.updatePurchase(validPurchaseList.get(0)));
    }

    @Test
    public void testPurchaseExists() {
        given(purchaseRepository.exists(1L)).willReturn(false);
        Assert.assertEquals(false, purchaseService.purchaseExists(1L));

        given(purchaseRepository.exists(2L)).willReturn(true);
        Assert.assertEquals(true, purchaseService.purchaseExists(2L));
    }

}
