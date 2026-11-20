package com.chx.saleswithagent.DTO;

import lombok.Data;

@Data
public class LoginDTO {
    private String token;
    private String phone;
    private String password;
}
