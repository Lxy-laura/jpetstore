package com.jpetstore.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "订单ID不能为空")
    private Integer orderid;

    @NotNull(message = "行号不能为空")
    private Integer linenum;

    @NotNull(message = "时间戳不能为空")
    private Date timestamp;

    private String status;
}