package com.seckill.common.exception;

import com.seckill.common.result.ResultCode;


import java.io.Serial;


public class BusinessException extends RuntimeException{
    @Serial
    private static  final long serialVersionUID=1L;

    private final ResultCode resultCode;

    public BusinessException(ResultCode  resultCode){
        super(resultCode.getMsg());
        this.resultCode=resultCode;
    }


    public ResultCode getResultCode(){
        return resultCode;
    }
}
