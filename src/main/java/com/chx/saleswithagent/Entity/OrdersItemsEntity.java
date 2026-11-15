package com.chx.saleswithagent.Entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrdersItemsEntity {
    private Long orderId;
    private String productName;
    private Long productId;
    private String spec;
    private Integer quantity;
    private BigDecimal price;
}
