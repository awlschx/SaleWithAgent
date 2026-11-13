package com.chx.saleswithagent.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddressDTO {
    private Long id;
    private Long userId;
    private String name;
    private String phone;
    private String region;
    private String detail;
    private Boolean isDefault;
    private LocalDateTime createTime;
}
