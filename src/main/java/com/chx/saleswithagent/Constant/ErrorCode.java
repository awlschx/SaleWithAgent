package com.chx.saleswithagent.Constant;

public enum ErrorCode {
    COMMON_ERROR(400,"错误"),
    PHONE_EXIST(40900,"该手机号已注册"),
    USER_EXIST(400,"该用户已登录"),
    ORDER_ITEMS_EMPTY(40004,"结算商品为空"),
    COUPON_INVALID(40005,"优惠券不存在或已被使用"),
    REVIEW_NOT_FOUND(40009,"评价不存在或已下架"),
    ORDER_NOT_FOUND(40006,"订单不存在或无权操作"),
    ORDER_NOT_FINISHED(40007,"订单完成后才能评价"),
    REVIEW_DUPLICATED(40008,"该商品已评价过了");

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
