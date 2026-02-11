package gg.aliraza.ome.services.orders;

import gg.aliraza.ome.models.order.OrderType;

/**
 * Service for creating orders.
 */
public interface CreateOrderService {

    /**
     * Allows clients to create a market order where they specify the number of shares they want to trade
     * @param symbol The symbol to trade
     * @param orderType Whether it's a buy or sell order
     * @param numberOfShares Number of shares to trade
     * @return Order ID
     */
     int createMarketOrderByShares(String symbol, OrderType orderType, double numberOfShares);

    /**
     * Allows clients to create a market order where they specify the dollar value they want to trade.
     * @param symbol The symbol to trade
     * @param orderType Whether it's a buv or sell order
     * @param dollars The dollar value to trade
     * @return Order ID
     */
     int createMarketOrderByPrice(String symbol, OrderType orderType, double dollars);

    /**
     * Allows clients to create a limit order where they specify the number of shares they want to trade.
     * @param symbol The symbol to trade
     * @param orderType Whether it's a buy or sell order
     * @param numberOfShares The number of shares to trade
     * @param limit The limit value beyond which point the order shall not execute
     * @return Order ID
     */
     int createLimitOrderByShares(String symbol, OrderType orderType, double numberOfShares, double limit);

    /**
     * Allows clients to create a limit order where they specify the dollar value they want to trade.
     * @param symbol The symbol to trade
     * @param orderType Whether it's a buy or sell order
     * @param dollars The dollar value to trade
     * @param limit The limit value beyond which point the order shall not execute
     * @return Order ID
     */
     int createLimitOrderByPrice(String symbol, OrderType orderType, double dollars, double limit);

}
