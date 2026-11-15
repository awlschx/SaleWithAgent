package com.chx.saleswithagent.VO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemsVO {
    private Long productId;
    private String spec;
    private Integer quantity;
    private BigDecimal price;
}
