package com.seckill.order.domain.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum{

    NOT_PAYMENT(0,"未支付"),
    HAD_PAID(1,"已支付"),
    CANCEL(2,"取消");

    @EnumValue
    private Integer code;
    private String msg;
}
