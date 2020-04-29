package pt.pmribeiro.webstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@code PurchaseDetail} is the purchase detail dto info representation
 * @todo: use validators annotations
 *
 * Created by pribeiro on 27/11/2016.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseDetail {

    private Long id;
    private String description;
    private Integer quantity;
    private Double value;

}
