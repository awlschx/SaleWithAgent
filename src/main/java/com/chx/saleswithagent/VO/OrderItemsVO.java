package com.chx.saleswithagent.VO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemsVO {
    private Long productId;
    private String spec;
    private Integer quantity;
    private BigDecimal price;
    private Boolean reviewed;   //是否已评价，订单列表据此显示「评价 / 已评价」
}
