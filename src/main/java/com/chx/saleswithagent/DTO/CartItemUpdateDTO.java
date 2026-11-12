package com.chx.saleswithagent.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemUpdateDTO
{
    @NotNull(message = "数量不能为空")
    private Integer quantity;
    @NotNull(message = "勾选状态不能为空")
    private Boolean selected;
}
