package com.chx.saleswithagent.VO;


import lombok.Data;

/*
*前端请求登录，注册返回的VO
*
 */
@Data
public class UserVO {
    String token;
    LoginVO login;

}
