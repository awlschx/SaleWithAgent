package com.chx.saleswithagent.VO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductsVO {
    private Integer id;
    private String name;
    private String category;
    private float price;
    private float originalPrice;
    private Integer sales;
    private Integer stock;
    private Integer hue;
    private float flashPrice;
    private Integer flashPercent;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
