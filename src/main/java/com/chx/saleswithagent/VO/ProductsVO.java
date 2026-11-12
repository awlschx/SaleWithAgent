package com.chx.saleswithagent.VO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductsVO {
    private Integer id;
    private String productName;
    private String category;
    private float price;
    private float originalPrice;
    private Integer sales;
    private Integer stock;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
