package com.chx.saleswithagent.Entity;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserCouponsEntity {
    private Long id;
    private Long userId;
    private Long templateId;
    private BigDecimal minAmount;
    private BigDecimal offAmount;
    private int used;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private String orderNo;
}
