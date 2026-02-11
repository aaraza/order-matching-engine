package gg.aliraza.ome.models.order;

public enum OrderStatus {

    /**
     * The order has been placed.
     */
    OPEN,

    /**
     * The order has been partially matched. Additional trades need to be executed before the order is filled.
     */
    PARTIALLY_FILLED,

    /**
     * The order has been filled.
     */
    FILLED,

    /**
     * Assets have been transferred and the trade is complete.
     */
    SETTLED

}
