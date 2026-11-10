package com.chx.saleswithagent.Common;

import com.chx.saleswithagent.Constant.ErrorCode;

/*
*
*业务异常类
*
*/
public class BizException extends RuntimeException{
//把code和message封装成errorcode
//传异常以errorcode传，创建成员属性接收
    private ErrorCode errorCode;

    public BizException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    //可能还会传入message添加说明
    public BizException(ErrorCode errorCode,String message){
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
