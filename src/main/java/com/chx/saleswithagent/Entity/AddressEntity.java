package com.chx.saleswithagent.Entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddressEntity {
    private Long id;
    private Long userId;
    private String name;
    private String phone;
    private String region;
    private String detail;
    private Boolean isDefault;
    private LocalDateTime createTime;
}
