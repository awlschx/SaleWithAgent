package com.chx.saleswithagent.VO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CouponsTemplateVO {
    private Long id;
    private String title;
    private BigDecimal minAmount;
    private BigDecimal offAmount;
    private String category;   // null 或空 = 全场通用
}
