package pt.pmribeiro.webstore.config;

import com.google.common.collect.Lists;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pt.pmribeiro.webstore.controller.PurchaseController;
import pt.pmribeiro.webstore.dao.IPurchaseDAO;
import pt.pmribeiro.webstore.dto.Purchase;
import pt.pmribeiro.webstore.dto.PurchaseDetail;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * {@code DevelopmentConfiguration} contains the spring configuration
 * for development profile with mock of the IPurchaseDAO
 *
 * Created by pribeiro on 26/11/2016.
 */
@Configuration
@Profile({"dev", "docker"})
public class DevelopmentConfiguration extends CommonComfiguration {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PurchaseController.class);

    /**
     * Create a mock instance of IPurchaseDAO interface
     *
     * @return IPurchaseDAO mock
     */
    @Bean
    public IPurchaseDAO purchaseRepository() {
        // get valid in invalid dates to set in expires field of Purchase
        Calendar validDate = Calendar.getInstance();
        validDate.add(Calendar.DAY_OF_MONTH, 10);
        Calendar invalidDate = Calendar.getInstance();
        invalidDate.add(Calendar.DAY_OF_MONTH, -10);

        // build purchases list
        List<Purchase> purchaseList = Lists.newArrayList();

        purchaseList.add(Purchase.builder().id(1L).productType("Bakery").expires(validDate.getTime()).purchaseDetails(
                Lists.newArrayList(
                        PurchaseDetail.builder().id(1L).description("XXL wedding cake").quantity(1).value(500.00).build()
                )
        ).build());

        purchaseList.add(Purchase.builder().id(2L).productType("Gadget").expires(validDate.getTime()).purchaseDetails(
                Lists.newArrayList(
                        PurchaseDetail.builder().id(1L).description("One plus 3").quantity(2).value(798.00).build()
                )
        ).build());

        purchaseList.add(Purchase.builder().id(3L).productType("Bakery").expires(invalidDate.getTime()).purchaseDetails(
                Lists.newArrayList(
                        PurchaseDetail.builder().id(1L).description("Mickey mouse cake").quantity(1).value(70.00).build()
                )
        ).build());

        purchaseList.add(Purchase.builder().id(4L).productType("Gadget").expires(invalidDate.getTime()).purchaseDetails(
                Lists.newArrayList(
                        PurchaseDetail.builder().id(1L).description("IPhone 7 64Gb").quantity(1).value(700.00).build()
                )
        ).build());

        // create IPurchaseDAO mock
        IPurchaseDAO purchaseRepository = spy(IPurchaseDAO.class);

        // set methods call expected result
        // getAll function
        doReturn(purchaseList).when(purchaseRepository).getAll();
        // getPurchasesDetailByPurchasesIds function
        doAnswer(new Answer<List<Purchase>>() {
            @Override
            public List<Purchase> answer(InvocationOnMock invocation) throws Throwable {
                List<Long> idsList = Lists.newArrayList();
                List<Object> args = Arrays.asList(invocation.getArguments());
                args.forEach(arg -> idsList.add((Long)arg));
                logger.debug("idsList", idsList);
                return purchaseList.parallelStream()
                        .filter(p -> idsList.contains(p.getId()))
                        .collect(Collectors.toList());
            }
        }).when(purchaseRepository).getPurchasesDetailByPurchasesIds(any());
        // findById function
        doAnswer(new Answer<Purchase>() {
            @Override
            public Purchase answer(InvocationOnMock invocation) throws Throwable {
                Long id = invocation.getArgument(0);
                List<Purchase> purchases = purchaseList.parallelStream()
                        .filter(p -> id == p.getId())
                        .collect(Collectors.toList());
                return purchases.isEmpty() ? null : purchases.get(0);
            }
        }).when(purchaseRepository).findById(anyLong());
        // save function
        doAnswer(new Answer<Purchase>() {
            @Override
            public Purchase answer(InvocationOnMock invocation) throws Throwable {
                purchaseList.add(invocation.getArgument(0));
                return (Purchase)invocation.getArgument(0);
            }
        }).when(purchaseRepository).save(any(Purchase.class));
        // update function
        doAnswer(new Answer<Purchase>() {
            @Override
            public Purchase answer(InvocationOnMock invocation) throws Throwable {
                List<Long> ids = purchaseList.parallelStream()
                        .map(p -> p.getId())
                        .collect(Collectors.toList());
                Purchase purchase = invocation.getArgument(0);
                purchaseList.set(ids.indexOf(purchase.getId()), invocation.getArgument(0));
                return (Purchase)invocation.getArgument(0);
            }
        }).when(purchaseRepository).update(any(Purchase.class));
        // exists function
        doAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                List<Long> ids = purchaseList.parallelStream()
                        .map(p -> p.getId())
                        .collect(Collectors.toList());
                return ids.contains(invocation.getArgument(0));
            }
        }).when(purchaseRepository).exists(any(Long.class));

        return purchaseRepository;
    }

}
