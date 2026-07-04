package com.seckill.infrastructure.message;

import com.seckill.failurelog.domain.constant.MqMessageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.amqp.rabbit.connection.CorrelationData;


@Getter
public class SeckillCorrelationData  extends CorrelationData {
    private final SeckillMessage message;
    private final MqMessageTypeEnum messageType;
    //延迟队列和正常链路队列出阿努人消息的区别是
    public SeckillCorrelationData(
            String id, SeckillMessage message,MqMessageTypeEnum messageType) {
        super(id);              // id 唯一即可,比如 userId + ":" + seckillId
        this.message = message;
        this.messageType=messageType;
    }
}
