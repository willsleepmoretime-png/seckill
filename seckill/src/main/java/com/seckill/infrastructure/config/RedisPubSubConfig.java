package com.seckill.infrastructure.config;

import com.seckill.infrastructure.Cache.CacheInvalidationPublisher;
import com.seckill.infrastructure.Cache.CacheInvalidationSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisPubSubConfig {

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            CacheInvalidationSubscriber subscriber
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        //让 CacheInvalidationSubscriber 订阅 cache:invalidate:channel 这个频道
        container.addMessageListener(subscriber, new ChannelTopic(CacheInvalidationPublisher.CHANNEL));
        return container;
    }
}
