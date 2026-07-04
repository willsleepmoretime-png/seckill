package com.seckill.infrastructure.Cache;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

//发缓存中的不合规信息
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheInvalidationPublisher {
    //Redis Channel 负责中转消息
    public static final String CHANNEL = "cache:invalidate:channel";

    private final StringRedisTemplate stringRedisTemplate;
    private final CacheStats cacheStats;

    public void publish(String cacheType, String key) {
        publish(CacheInvalidationMessage.of(cacheType, key));
    }

    //广播信息
    public void publish(CacheInvalidationMessage message) {
        if (message == null || !message.isValid()) {
            log.warn("Skip invalid cache invalidation message: {}", message);
            return;
        }
        String body = JSONUtil.createObj()
                .set("cacheType", message.cacheType())
                .set("key", message.key())
                .set("timestamp", message.timestamp())
                .toString();
        stringRedisTemplate.convertAndSend(CHANNEL, body);
        cacheStats.incrementCacheInvalidationPublished();
    }
}
