package com.chx.saleswithagent.Entity;

import com.chx.saleswithagent.VO.AddressVO;
import com.chx.saleswithagent.VO.OrderItemsVO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdersEntity {
    private Long id;
    private Long userId;
    private String orderNo;
    private String status;
    private BigDecimal itemAmount;
    private BigDecimal shipFee;
    private BigDecimal discount;
    private BigDecimal payAmount;
    private AddressVO addressSnapshot;
    private Long userCouponId;
    private LocalDateTime createdAt;
    private LocalDateTime shipTime;
    private LocalDateTime receiveTime;
}
