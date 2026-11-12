package com.chx.saleswithagent.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AllSelectedDTO {
    @NotNull(message = "勾选状态不能为空")
    private Boolean selected;
}

