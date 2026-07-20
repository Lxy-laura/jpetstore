package com.jpetstore.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "产品ID不能为空")
    private String productid;

    @NotBlank(message = "分类ID不能为空")
    private String category;

    @NotBlank(message = "产品名称不能为空")
   private String name;

    private String status = "ON_SALE";

   private String description;

    private String image;

    private BigDecimal price;

    private Category categoryObj;
    private List<Item> items;

}
