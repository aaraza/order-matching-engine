package gg.aliraza.ome.models.order;

/**
 * Represents the type of order placed by a customer.
 */
public enum OrderType {

    /**
     * Customers bid place a bid on assets they don't own.
     */
    BID,

    /**
     * Customers ask for a price to sell assets they own.
     */
    ASK
}
