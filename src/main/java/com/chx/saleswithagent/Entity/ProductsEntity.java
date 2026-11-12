package com.chx.saleswithagent.Entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductsEntity {
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
