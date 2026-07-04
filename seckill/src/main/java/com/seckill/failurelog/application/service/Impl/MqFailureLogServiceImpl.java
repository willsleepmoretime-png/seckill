package com.seckill.failurelog.application.service.Impl;

import com.seckill.failurelog.application.service.MqFailureLogService;
import com.seckill.failurelog.domain.constant.MqFailureStageEnum;
import com.seckill.failurelog.domain.constant.MqFailureStatusEnum;
import com.seckill.failurelog.domain.constant.MqMessageTypeEnum;
import com.seckill.failurelog.domain.entity.MqFailureLog;
import com.seckill.failurelog.domain.repository.MqFailureLogRepository;
import com.seckill.seckill.application.service.StockService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Slf4j
@Service
public class MqFailureLogServiceImpl implements MqFailureLogService {

        private final MqFailureLogRepository mqFailureLogRepository;
        private final StockService stockService;

        @Override
        public void recordAndRollback(String messageId,
                                           Long userId,
                                           Long seckillId,
                                           MqMessageTypeEnum messageType,
                                           MqFailureStageEnum failureStage,
                                           String exchangeName,
                                           String routingKey,
                                           String reason) {
            //记录和回滚 首先就是记录
            MqFailureLog Mqlog=MqFailureLog.create(
                    messageId,
                    userId,
                    seckillId,
                    messageType,
                    failureStage,
                    exchangeName,
                    routingKey,
                    reason
            );
            mqFailureLogRepository.saveIgnoreDuplicate(Mqlog);
            if(messageType==MqMessageTypeEnum.ORDER_CREATE){
                try{
                    stockService.rollback(seckillId, userId);
                    mqFailureLogRepository.updateStatus(
                            messageId,
                            failureStage,
                            MqFailureStatusEnum.ROLLBACK_SUCCESS,
                            reason
                    );
                }catch (Exception e){
                    mqFailureLogRepository.updateStatus(
                            messageId,
                            failureStage,
                            MqFailureStatusEnum.ROLLBACK_FAILED,
                            reason + "; rollback failed: " + e.getMessage()
                    );
                    log.error("Redis rollback failed messageId={} userId={} seckillId={}",
                            messageId, userId, seckillId, e);
                }
            }

        }
    }
