package com.chx.saleswithagent.VO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Setter
@Getter
public class CartItemVO {
    private Long id;
    private Long productId;
    private String name;
    private String spec;
    private BigDecimal price;
    private Integer quantity;
    private boolean selected;
    private BigDecimal subtotal;
}
