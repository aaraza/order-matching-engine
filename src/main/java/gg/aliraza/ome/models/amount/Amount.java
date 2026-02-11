package gg.aliraza.ome.models.amount;

import lombok.Data;

@Data
public class Amount {

    private AmountType amountType;
    private Double quantity;

    public Amount(AmountType amountType, Double quantity) {
        this.amountType = amountType;
        if (quantity <= 0) {
            throw new IllegalArgumentException("Amount quantity needs to be greater than 0");
        }
        this.quantity = quantity;
    }

}
