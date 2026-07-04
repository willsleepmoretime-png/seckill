package com.seckill.common.result;

public enum ResultCode {

    // ==== 成功 ====
    OK("00000", "ok"),

    // ==== A 类:应用错误(用户可见) ====

    // A0xxxx:通用错误
    PARAM_ERROR("A00001", "参数错误"),
    UNAUTHORIZED("A00002", "未登录"),
    NOT_FOUND("A00003", "资源不存在"),
    INSUFFICIENT_BALANCE("A00004","金额不足已支付"),
    // A1xxxx:用户模块(M1 当前需要)
    PHONE_ALREADY_REGISTERED("A10001", "手机号已注册"),
    USER_NOT_FOUND("A10002", "用户不存在"),
    PASSWORD_INCORRECT("A10003", "密码错误"),
    USER_DISABLED("A10004", "用户已禁用"),

    // A2xxxx:商品模块(M2 再加)
    GOODS_NOT_FOUND("A20001","商品不存在"),
    STOCK_INSUFFICIENT("A20002 ","库存不足"),

    // A3xxxx:秒杀模块(M3 再加)
    SECKILL_NOT_EXISTS("A30001","秒杀活动不存在"),
    SECKILL_NOT_START("A30002","秒杀活动还没有开始"),
    SECKILL_ENDED("A30003","秒杀活动已经结束"),
    SECKILL_SOLD_OUT("A30004","秒杀商品已经售罄"),
    SECKILL_REPEAT_BUY("A30005","请勿重复购买"),
    SECKILL_DEDUCT_FAIL("A30006", "扣减库存失败"),
    SECKILL_STATUS_INVALID("A30007", "秒杀活动状态不合法"),
    NO_STOCK("A30008","库存售罄"),
    NO_REPEAT_BUY("A30009","不能重复购买"),
    NO_PREHEAT("A30010","没有预热到Redis"),
    SUCCESS_SUBMIT_ORDER("A30011","成功提交订单"),
    SECKILL_PREHEAT_INTERRUPTED("A30012","预热中断"),
    SECKILL_ALREADY_PREHEATED("A300013","已经 预热"),
    // A4xxxx:订单模块(M4 再加)

    ORDER_NOT_EXIST("A40001","订单不存在"),
    ORDER_NOT_BELONG_TO_USER("A40002","无权访问该订单"),
    ORDER_STATUS_INVALID("A40003","订单无效"),
    ORDER_STATUS_ERROR("A40004","订单状态不对"),

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
