package com.seckill.failurelog.domain.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MqFailureStatusEnum {
    INIT("INIT", "Failure recorded"),
    ROLLBACK_SUCCESS("ROLLBACK_SUCCESS", "Redis stock rollback succeeded"),
    ROLLBACK_FAILED("ROLLBACK_FAILED", "Redis stock rollback failed"),
    HANDLED("HANDLED", "Failure handled");

    @EnumValue
    private final String code;
    private final String desc;
}
