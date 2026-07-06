package com.jpetstore.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderid;

    @NotBlank(message = "用户ID不能为空")
    private String userid;

    @NotNull(message = "订单日期不能为空")
    private Date orderdate;

    @NotBlank(message = "收货地址不能为空")
    private String shipaddr1;
    private String shipaddr2;
    @NotBlank(message = "收货城市不能为空")
    private String shipcity;
    @NotBlank(message = "收货州/省不能为空")
    private String shipstate;
    @NotBlank(message = "收货邮编不能为空")
    private String shipzip;
    @NotBlank(message = "收货国家不能为空")
    private String shipcountry;

    @NotBlank(message = "账单地址不能为空")
    private String billaddr1;
    private String billaddr2;
    @NotBlank(message = "账单城市不能为空")
    private String billcity;
    @NotBlank(message = "账单州/省不能为空")
    private String billstate;
    @NotBlank(message = "账单邮编不能为空")
    private String billzip;
    @NotBlank(message = "账单国家不能为空")
    private String billcountry;

    @NotBlank(message = "快递公司不能为空")
    private String courier;

    @NotNull(message = "总价不能为空")
    private BigDecimal totalprice;

    @NotBlank(message = "账单名不能为空")
    private String billtofirstname;
    @NotBlank(message = "账单姓不能为空")
    private String billtolastname;

    @NotBlank(message = "收货人名不能为空")
    private String shiptofirstname;
    @NotBlank(message = "收货人姓不能为空")
    private String shiptolastname;

    @NotBlank(message = "信用卡号不能为空")
    private String creditcard;
    @NotBlank(message = "信用卡有效期不能为空")
    private String exprdate;
    @NotBlank(message = "信用卡类型不能为空")
    private String cardtype;
    @NotBlank(message = "语言不能为空")
    private String locale;

    private String status;

    private List<OrderItem> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
    }
}