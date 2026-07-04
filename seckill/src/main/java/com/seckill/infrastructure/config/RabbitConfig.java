package com.seckill.infrastructure.config;


import com.seckill.failurelog.application.service.MqFailureLogService;
import com.seckill.failurelog.domain.constant.MqFailureStageEnum;
import com.seckill.failurelog.domain.constant.MqMessageTypeEnum;
import com.seckill.infrastructure.message.SeckillCorrelationData;
import com.seckill.infrastructure.message.SeckillMessage;
import com.seckill.seckill.application.service.StockService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.Map;

@Slf4j
@AllArgsConstructor
@Configuration
public class RabbitConfig {

    // ========== 链路一：订单落库主链路 + 死信兜底 ==========
    public static final String ORDER_EXCHANGE   = "seckill.order.exchange";
    public static final String ORDER_QUEUE      = "seckill.order.queue";
    public static final String ORDER_ROUTING    = "seckill.order";

    public static final String ORDER_DLX        = "seckill.order.dlx";       // 死信交换机
    public static final String ORDER_DLQ        = "seckill.order.dlq";       // 死信队列
    public static final String ORDER_DL_ROUTING = "seckill.order.dl";

    // ========== 链路二：超时取消延时链路 ==========
    public static final String DELAY_EXCHANGE   = "seckill.delay.exchange";
    public static final String DELAY_QUEUE      = "seckill.delay.queue";     // TTL队列，无消费者
    public static final String DELAY_ROUTING    = "seckill.delay";

    public static final String DELAY_DLX        = "seckill.delay.dlx";       // 延时到期转发的交换机
    public static final String DELAY_DLQ        = "seckill.delay.dlq";       // 超时消费者监听这个
    public static final String DELAY_DL_ROUTING = "seckill.delay.dl";

    public static final long   ORDER_TIMEOUT_MS = 30 * 60 * 1000L;           // 30分钟

    // ---------- 链路一：主队列（绑定死信，落库失败转发到 order.dlq）----------
    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(ORDER_QUEUE)
                           .deadLetterExchange(ORDER_DLX)
                           .deadLetterRoutingKey(ORDER_DL_ROUTING)
                           .build();
    }

    @Bean
    public DirectExchange orderExchange() {
        return ExchangeBuilder.directExchange(ORDER_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue()).to(orderExchange()).with(ORDER_ROUTING);
    }

    // ---------- 链路一：死信队列（接落库失败的消息）----------
    @Bean
    public Queue orderDlq() {
        return QueueBuilder.durable(ORDER_DLQ).build();
    }

    @Bean
    public DirectExchange orderDlx() {
        return ExchangeBuilder.directExchange(ORDER_DLX).durable(true).build();
    }

    @Bean
    public Binding orderDlqBinding() {
        return BindingBuilder.bind(orderDlq()).to(orderDlx()).with(ORDER_DL_ROUTING);
    }

    // ---------- 链路二：延时队列（设TTL + 无消费者，到期转发死信）----------
    @Bean
    public Queue delayQueue() {
       return  QueueBuilder.durable(DELAY_QUEUE)
               .ttl((int)ORDER_TIMEOUT_MS)
               .deadLetterExchange(DELAY_DLX)
               .deadLetterRoutingKey(DELAY_DL_ROUTING)
               .build();
    }

    @Bean
    public DirectExchange delayExchange() {
        return ExchangeBuilder.directExchange(DELAY_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding delayBinding() {
        return BindingBuilder.bind(delayQueue()).to(delayExchange()).with(DELAY_ROUTING);
    }

    // ---------- 链路二：延时死信队列（超时消费者监听这个）----------
    @Bean
    public Queue delayDlq() {
        return QueueBuilder.durable(DELAY_DLQ).build();
    }

    @Bean
    public DirectExchange delayDlx() {
        return ExchangeBuilder.directExchange(DELAY_DLX).durable(true).build();
    }

    @Bean
    public Binding delayDlqBinding() {
        return BindingBuilder.bind(delayDlq()).to(delayDlx()).with(DELAY_DL_ROUTING);
    }

    @Bean
    public static MessageConverter messageConverter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.addAllowedListPatterns("com.seckill.infrastructure.message.*");
        return converter;
    }

    private final RabbitTemplate rabbitTemplate;
    private final StockService stockService;
    private final MqFailureLogService mqFailureLogService;

    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback((correlationData,ack,cause)->{
            if (ack) {
                return;   // broker 收下了,正常,啥也不做
            }
            if(correlationData instanceof SeckillCorrelationData scd){
                   SeckillMessage msg=scd.getMessage();
                mqFailureLogService.recordAndRollback(
                        scd.getId(),
                        msg.getUserId(),
                        msg.getSeckillId(),
                        scd.getMessageType(),
                        MqFailureStageEnum.CONFIRM_NACK,
                        null,
                        null,
                        cause
                );
            }else {
                log.error("confirm nack but correlationData is not SeckillCorrelationData: {}", correlationData);
            }
        });
        rabbitTemplate.setReturnsCallback( returned -> {
                    Map<String, Object> headers = returned.getMessage()
                            .getMessageProperties()
                            .getHeaders();

                    String messageId = String.valueOf(headers.get("messageId"));
                    Long userId = Long.valueOf(String.valueOf(headers.get("userId")));
                    Long seckillId = Long.valueOf(String.valueOf(headers.get("seckillId")));
                    MqMessageTypeEnum messageType =
                            MqMessageTypeEnum.valueOf(String.valueOf(headers.get("messageType")));

                    String reason = returned.getReplyCode() + ":" + returned.getReplyText();

                    mqFailureLogService.recordAndRollback(
                            messageId,
                            userId,
                            seckillId,
                            messageType,
                            MqFailureStageEnum.RETURNED,
                            returned.getExchange(),
                            returned.getRoutingKey(),
                            reason
                    );
        }
        );
    }
}
