package com.chx.saleswithagent.DTO;

import lombok.Data;

import java.util.List;

@Data
public class OrdersDTO {
    private Long addressId;
    private Long couponId;
    private List<OrdersItemsDTO> items;
    private String source;
}
