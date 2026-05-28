package com.seckill.common.exception;


import com.seckill.common.result.Result;
import com.seckill.common.result.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private  static final Logger log= LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public Result<?> businessExceptionHandler(BusinessException e){
        log.warn("业务异常: {}",e.getMessage());
        return Result.fail(e.getResultCode());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleSystem(Exception e){
        log.error("系统异常",e);
        return Result.fail(ResultCode.SYSTEM_ERROR);
    }
}
