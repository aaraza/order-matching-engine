package gg.aliraza.ome.models.symbols;

import lombok.Data;

@Data
public class Symbol {

    private String ticker;
    private String name;
    private int lotSize;
    private double tickSize;
    private TradingStatus tradingStatus;

}
