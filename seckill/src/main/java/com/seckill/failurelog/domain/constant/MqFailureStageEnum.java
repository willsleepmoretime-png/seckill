package com.seckill.failurelog.domain.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MqFailureStageEnum {
    CONFIRM_NACK("CONFIRM_NACK", "Broker confirm nack"),
    RETURNED("RETURNED", "Message returned by broker"),
    CONSUME_DLQ("CONSUME_DLQ", "Message entered dead letter queue");

    @EnumValue
    private final String code;
    private final String desc;
}
