package com.chx.saleswithagent.Entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponsTemplateEntity {
    private Long id;
    private String title;
    private BigDecimal minAmount;//最小达到可用金额
    private BigDecimal offAmount;
    private String category;
    private Long validDays;
    private Long totalStock;
    private Long claimedCount;//已发放数量
    private int status;
    private LocalDateTime createdAt;
}
