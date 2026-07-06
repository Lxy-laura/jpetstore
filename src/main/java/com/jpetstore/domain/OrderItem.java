package com.jpetstore.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderid;
    private Integer linenum;
    private String itemid;

    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;

    @NotNull(message = "单价不能为空")
    private BigDecimal unitprice;

    private Item item;

    public BigDecimal getTotalPrice() {
        if (unitprice != null && quantity != null) {
            return unitprice.multiply(new BigDecimal(quantity));
        }
        return BigDecimal.ZERO;
    }
}