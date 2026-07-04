package com.seckill.seckill.interfaces.consumer;

import com.seckill.failurelog.application.service.MqFailureLogService;
import com.seckill.failurelog.domain.constant.MqFailureStageEnum;
import com.seckill.failurelog.domain.constant.MqMessageTypeEnum;
import org.springframework.amqp.core.Message;
import com.seckill.infrastructure.message.SeckillMessage;
import com.seckill.infrastructure.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Map;


//死信消费者 s
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDlqConsumer {

    private final MqFailureLogService mqFailureLogService;

    @RabbitListener(queues = RabbitConfig.ORDER_DLQ)
    public void handleDeadLetter(SeckillMessage msg, Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        Long userId = msg.getUserId();
        Long seckillId = msg.getSeckillId();

            // 落库最终失败 → 回补 Redis 库存（incr + srem），让这份库存重新可抢
            try {
                Map<String, Object> headers = message.getMessageProperties().getHeaders();

                String messageId = getHeaderAsString(
                        headers,
                        "messageId",
                        userId + ":" + seckillId + ":" + MqMessageTypeEnum.ORDER_CREATE.name()
                );

                mqFailureLogService.recordAndRollback(
                        messageId,
                        userId,
                        seckillId,
                        MqMessageTypeEnum.ORDER_CREATE,
                        MqFailureStageEnum.CONSUME_DLQ,
                        RabbitConfig.ORDER_EXCHANGE,
                        RabbitConfig.ORDER_ROUTING,
                        "order create message entered DLQ"
                );
                log.error("订单落库失败进入死信，已回补库存 userId={} seckillId={}", userId, seckillId);
                channel.basicAck(tag, false);   // 回补成功，确认删除
            } catch (Exception e) {
                // 回补也失败了（极端：Redis 挂）→ 这条不能 ack（否则库存永久丢失且无人知）
                log.error("死信回补库存失败！需人工介入 userId={} seckillId={}", userId, seckillId, e);
                channel.basicNack(tag, false, false);  // 不重回，留在死信外（见下说明）
            }
    }

    private String getHeaderAsString(Map<String, Object> headers, String key, String defaultValue) {
        Object value = headers.get(key);
        return value == null ? defaultValue : value.toString();

    }
}
