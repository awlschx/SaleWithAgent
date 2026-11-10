package com.chx.saleswithagent.Common;

/**
 * API响应类
 * @param <T> 响应数据的类型
 */
public class Result<T>{//T泛型声明后作用域是整个类
    private final int code;
    private final String message;
    private final T data;

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    //响应返回ok或fail
    //static不属于实例，要重新定义泛型
    public static <T> Result<T> ok(T data){
        return new Result<>(200, "ok", data);
    }
    public static<T> Result<T> fail(int code,String msg){
        return new Result<>(code ,msg,null);
    }
}
