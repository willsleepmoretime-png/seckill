package com.seckill.failurelog.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.failurelog.domain.constant.MqFailureStageEnum;
import com.seckill.failurelog.domain.constant.MqFailureStatusEnum;
import com.seckill.failurelog.domain.entity.MqFailureLog;
import com.seckill.failurelog.domain.repository.MqFailureLogRepository;
import com.seckill.failurelog.infrastructure.mapper.MqFailureLogMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

@Repository
public class MqFailureLogRepositoryImpl extends ServiceImpl<MqFailureLogMapper, MqFailureLog>
        implements MqFailureLogRepository {

    @Override
    public void saveIgnoreDuplicate(MqFailureLog log) {
        try {
            save(log);
        } catch (DuplicateKeyException ignored) {
            // Duplicate callbacks for the same message/stage are expected to be idempotent.
        }
    }

    @Override
    public boolean updateStatus(String messageId,
                                MqFailureStageEnum failureStage,
                                MqFailureStatusEnum status,
                                String reason) {
        return lambdaUpdate()
                .eq(MqFailureLog::getMessageId, messageId)
                .eq(MqFailureLog::getFailureStage, failureStage)
                .set(MqFailureLog::getStatus, status)
                .set(MqFailureLog::getReason, reason)
                .update();
    }
}
