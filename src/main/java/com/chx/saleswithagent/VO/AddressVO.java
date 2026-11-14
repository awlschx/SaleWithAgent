package com.chx.saleswithagent.VO;

import lombok.Data;

@Data
public class AddressVO {
    private Long id;
    private String name;
    private String phone;
    private String region;
    private String detail;
    private Boolean isDefault;
}
