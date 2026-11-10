package com.chx.saleswithagent.Common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    //异常路由处理
    @ExceptionHandler(BizException.class)
    public ResponseEntity<Result> handleBizException(BizException e){
        return  ResponseEntity
                .status(400)
                .body(Result.fail(e.getErrorCode().getCode(),e.getErrorCode().getMessage()));
    }
}
