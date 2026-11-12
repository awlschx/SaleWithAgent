package com.chx.saleswithagent.DTO;

import lombok.Data;

/*
*商品的固有属性
*
*/
@Data
public class CartItemDTO {
    //商品id
    private Long productId;
    //商品规格
    private String spec;
    //商品数量
    private Integer quantity;
    //是否选择
    private Boolean selected;
}
