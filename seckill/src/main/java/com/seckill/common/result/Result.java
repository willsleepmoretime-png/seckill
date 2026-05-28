package com.seckill.common.result;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String code;
    private final String msg;
    private final T data;

    // 私有构造器:外部不能 new,只能通过静态工厂创建
    private Result(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public boolean isSuccess() {
        return Objects.equals(ResultCode.OK.value(), code);
    }

    @Override
    public String toString() {
        return "Result{code=" + code + ", msg='" + msg + "', data=" + data + '}';
    }

    // ==== 静态工厂方法 ====

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.OK.value(), ResultCode.OK.getMsg(), data);
    }

    public static <T> Result<T> success() {
        return new Result<>(ResultCode.OK.value(), ResultCode.OK.getMsg(), null);
    }

    public static <T> Result<T> fail(ResultCode resultCode, T data) {
        return new Result<>(resultCode.value(), resultCode.getMsg(), data);
    }

    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(resultCode.value(), resultCode.getMsg(), null);
    }
}