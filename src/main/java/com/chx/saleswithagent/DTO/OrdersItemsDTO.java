package com.chx.saleswithagent.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrdersItemsDTO {
    private Long productId;
    private String spec;
    private Integer quantity;
}
