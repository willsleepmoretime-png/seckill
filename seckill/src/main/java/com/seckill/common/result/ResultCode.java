package com.seckill.common.result;

public enum ResultCode {

    // ==== 成功 ====
    OK("00000", "ok"),

    // ==== A 类:应用错误(用户可见) ====

    // A0xxxx:通用错误
    PARAM_ERROR("A00001", "参数错误"),
    UNAUTHORIZED("A00002", "未登录"),
    NOT_FOUND("A00003", "资源不存在"),

    // A1xxxx:用户模块(M1 当前需要)
    PHONE_ALREADY_REGISTERED("A10001", "手机号已注册"),
    USER_NOT_FOUND("A10002", "用户不存在"),
    PASSWORD_INCORRECT("A10003", "密码错误"),
    USER_DISABLED("A10004", "用户已禁用"),

    // A2xxxx:商品模块(M2 再加)
    GOODS_NOT_FOUND("A20001 ","商品不存在"),
    STOCK_INSUFFICIENT("A20002 ","库存不足"),

    // A3xxxx:秒杀模块(M3 再加)
    // A4xxxx:订单模块(M4 再加)

    // ==== B 类:系统错误 ====
    SYSTEM_ERROR("B00001", "系统错误");

    private final String code;
    private final String msg;

    public String value() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    ResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    // 注意:没有 toString,用枚举默认行为
}