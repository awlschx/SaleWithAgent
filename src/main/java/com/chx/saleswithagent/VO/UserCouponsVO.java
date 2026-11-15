package com.chx.saleswithagent.VO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserCouponsVO {
    private Long id;           // user_coupons.id，前端映射成 uid
    private Long templateId;   // 前端映射成 cid
    private String title;
    private BigDecimal minAmount;
    private BigDecimal offAmount;
    private String category;
    private Boolean used;
    private LocalDateTime expireAt;   // 前端会转成毫秒时间戳
}
