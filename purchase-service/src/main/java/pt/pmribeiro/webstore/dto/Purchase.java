package pt.pmribeiro.webstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * {@code Purchase} is the purchase dto info representation
 * @todo: use validators annotations
 *
 * Created by pribeiro on 27/11/2016.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Purchase {

    private Long id;
    private String productType;
    private Date expires;
    private List<PurchaseDetail> purchaseDetails;

}
