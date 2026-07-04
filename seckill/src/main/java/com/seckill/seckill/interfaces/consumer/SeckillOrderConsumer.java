package com.seckill.seckill.interfaces.consumer;



import com.rabbitmq.client.Channel;

import com.seckill.infrastructure.message.SeckillMessage;
import com.seckill.infrastructure.config.RabbitConfig;

import com.seckill.seckill.application.service.SeckillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeckillOrderConsumer {

    private final SeckillService seckillService;

    @RabbitListener(queues = RabbitConfig.ORDER_QUEUE)
    public void handle(SeckillMessage msg, Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            seckillService.handleSeckillOrder(msg);   // 落库编排在 Service 里（含幂等+事务）
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("订单落库失败 userId={} seckillId={}", msg.getUserId(), msg.getSeckillId(), e);
            channel.basicNack(tag, false, false);     // 失败进死信
        }
    }


}