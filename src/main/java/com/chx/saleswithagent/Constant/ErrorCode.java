package com.chx.saleswithagent.Constant;

public enum ErrorCode {
    COMMON_ERROR(400,"错误"),
    PHONE_EXIST(40900,"该手机号已注册"),
    USER_EXIST(400,"该用户已登录");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
