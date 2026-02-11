package gg.aliraza.ome.models.order;

import gg.aliraza.ome.models.amount.Amount;
import lombok.Data;

@Data
public class Order {

    private Integer orderId;
    private String symbol;
    private OrderType orderType;
    private OrderMechanism orderMechanism;
    private Amount amount;


}