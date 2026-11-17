package com.chx.saleswithagent.VO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductsVO {
    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer sales;
    private Integer stock;
    private Integer hue;
    private BigDecimal flashPrice;
    private Integer flashPercent;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
