package com.chx.saleswithagent.VO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductsVO {
    private Integer id;
    private String name;
    private List<String> specs;
    private String category;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer sales;
    private Integer stock;
    private Integer hue;
    private BigDecimal flashPrice;
    private Integer flashPercent;
    private LocalDateTime flashStartAt;
    private LocalDateTime flashEndAt;
    private Integer flashStock;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
