package com.seckill.failurelog.domain.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MqMessageTypeEnum {
    ORDER_CREATE("ORDER_CREATE", "Create seckill order"),
    ORDER_TIMEOUT("ORDER_TIMEOUT", "Cancel unpaid order after timeout");

    @EnumValue
    private final String code;
    private final String desc;
}
