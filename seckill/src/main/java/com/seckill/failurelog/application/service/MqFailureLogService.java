package com.seckill.failurelog.application.service;

import com.seckill.failurelog.domain.constant.MqFailureStageEnum;
import com.seckill.failurelog.domain.constant.MqMessageTypeEnum;


public interface MqFailureLogService {
    void recordAndRollback(String messageId,
                           Long userId,
                           Long seckillId,
                           MqMessageTypeEnum messageType,
                           MqFailureStageEnum failureStage,
                           String exchangeName,
                           String routingKey,
                           String reason);
}
