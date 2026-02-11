package gg.aliraza.ome.models.symbols;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Symbol {

    @JsonProperty("displaySymbol")
    @EqualsAndHashCode.Include
    private String ticker;

    @JsonProperty("description")
    private String name;

    private String mic;
    private String type;
    private String currency;

    private int lotSize;
    private double tickSize;
    private TradingStatus tradingStatus;

}
