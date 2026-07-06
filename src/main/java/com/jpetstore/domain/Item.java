package com.jpetstore.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "商品项ID不能为空")
    private String itemid;

    @NotBlank(message = "产品ID不能为空")
    private String productid;

    private BigDecimal listprice;

    private BigDecimal unitcost;

    private Integer supplier;

    private String status;

    private String attr1;
    private String attr2;
    private String attr3;
    private String attr4;
    private String attr5;

    @Min(value = 0, message = "库存数量不能为负数")
    private Integer qty;

    private Product product;
}