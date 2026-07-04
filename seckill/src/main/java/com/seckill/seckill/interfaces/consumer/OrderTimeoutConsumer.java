package com.seckill.seckill.interfaces.consumer;

import com.rabbitmq.client.Channel;
import com.seckill.infrastructure.config.RabbitConfig;
import com.seckill.infrastructure.message.SeckillMessage;
import com.seckill.seckill.application.service.SeckillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderTimeoutConsumer {
    private final SeckillService seckillService;

    @RabbitListener(queues= RabbitConfig.DELAY_DLQ)
    public void  handleTimeout(SeckillMessage msg, Message message, Channel channel) throws IOException{
        Long tag=message.getMessageProperties().getDeliveryTag();
        try{

            seckillService.cancelIfUnpaid(msg);
            channel.basicAck(tag,false);
        }catch (Exception e){
            log.error("超时取消处理失败 userId={} seckillId={}", msg.getUserId(), msg.getSeckillId(), e);
            channel.basicNack(tag, false, false);
        }
    }
}
