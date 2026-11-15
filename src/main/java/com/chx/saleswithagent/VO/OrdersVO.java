package com.chx.saleswithagent.VO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdersVO {
    private Long id;
    private String orderNo;
    private String status;
    private BigDecimal itemAmount;
    private BigDecimal shipFee;
    private BigDecimal discount;
    private BigDecimal payAmount;
    private AddressVO addressSnapshot;
    private Long userCouponId;
    private LocalDateTime createdAt;
    private List<OrderItemsVO> items;
}
