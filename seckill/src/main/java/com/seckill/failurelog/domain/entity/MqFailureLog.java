package com.seckill.failurelog.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seckill.common.entity.BaseEntity;
import com.seckill.failurelog.domain.constant.MqFailureStageEnum;
import com.seckill.failurelog.domain.constant.MqFailureStatusEnum;
import com.seckill.failurelog.domain.constant.MqMessageTypeEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("mq_failure_log")
public class MqFailureLog extends BaseEntity {
    private String messageId;
    private Long userId;
    private Long seckillId;
    private MqMessageTypeEnum messageType;
    private MqFailureStageEnum failureStage;
    private String exchangeName;
    private String routingKey;
    private String reason;
    private MqFailureStatusEnum status;

    public static MqFailureLog create(String messageId,
                                      Long userId,
                                      Long seckillId,
                                      MqMessageTypeEnum messageType,
                                      MqFailureStageEnum failureStage,
                                      String exchangeName,
                                      String routingKey,
                                      String reason) {
        MqFailureLog log = new MqFailureLog();
        log.messageId = messageId;
        log.userId = userId;
        log.seckillId = seckillId;
        log.messageType = messageType;
        log.failureStage = failureStage;
        log.exchangeName = exchangeName;
        log.routingKey = routingKey;
        log.reason = reason;
        log.status = MqFailureStatusEnum.INIT;
        return log;
    }
}
