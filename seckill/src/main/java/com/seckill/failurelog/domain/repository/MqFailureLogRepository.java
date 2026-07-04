package com.seckill.failurelog.domain.repository;

import com.seckill.failurelog.domain.constant.MqFailureStageEnum;
import com.seckill.failurelog.domain.constant.MqFailureStatusEnum;
import com.seckill.failurelog.domain.entity.MqFailureLog;

public interface MqFailureLogRepository {
    void saveIgnoreDuplicate(MqFailureLog log);

    boolean updateStatus(String messageId,
                         MqFailureStageEnum failureStage,
                         MqFailureStatusEnum status,
                         String reason);
}
