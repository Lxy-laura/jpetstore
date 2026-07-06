package com.jpetstore.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Item item;
    private int quantity;
    private boolean isInStock;

    public CartItem(Item item) {
        this.item = item;
        this.quantity = 1;
        this.isInStock = true;
    }

    public void incrementQuantity() {
        quantity++;
    }

    public BigDecimal getTotalPrice() {
        if (item != null && item.getListprice() != null) {
            return item.getListprice().multiply(new BigDecimal(quantity));
        }
        return BigDecimal.ZERO;
    }
}