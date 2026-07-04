package com.seckill.infrastructure.Cache;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

//承接消息
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheInvalidationSubscriber implements MessageListener {

    private final MultiLevelCacheLocalInvalidator localInvalidator;
    private final CacheStats cacheStats;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        try {
            // 解析
            CacheInvalidationMessage invalidationMessage = parseMessage(body);
            if (invalidationMessage == null || !invalidationMessage.isValid()) {
                log.warn("Invalid cache invalidation message: {}", body);
                return;
            }
            localInvalidator.invalidateLocal(invalidationMessage.key());
            cacheStats.incrementCacheInvalidationReceived();
        } catch (Exception e) {
            log.warn("Failed to handle cache invalidation message: {}", body, e);
        }
    }

    //将解析消息
    private CacheInvalidationMessage parseMessage(String body) {
        //之前旧的消息体
        if (body == null || !body.trim().startsWith("{")) {
            return parseLegacyMessage(body);
        }
        try {
            //生成json 消息体
            JSONObject json = JSONUtil.parseObj(body);
            return new CacheInvalidationMessage(
                    json.getStr("cacheType"),
                    json.getStr("key"),
                    parseTimestamp(json.get("timestamp"))
            );
        } catch (RuntimeException ignored) {
            return parseLegacyMessage(body);
        }
    }

    //解析旧版消息
    private CacheInvalidationMessage parseLegacyMessage(String body) {
        if (body == null) {
            return null;
        }
        String[] parts = body.split("\\|", 3);
        if (parts.length < 2) {
            return null;
        }
        long timestamp = System.currentTimeMillis();
        if (parts.length == 3) {
            try {
                timestamp = Long.parseLong(parts[2]);
            } catch (NumberFormatException ignored) {
                timestamp = System.currentTimeMillis();
            }
        }
        return new CacheInvalidationMessage(parts[0], parts[1], timestamp);
    }

    private long parseTimestamp(Object value) {
        //数字
        if (value instanceof Number number) {
            return number.longValue();
        }
        //String 字符串
        if (value instanceof String text) {
            try {
                return Long.parseLong(text);
            } catch (NumberFormatException ignored) {
                return 0;
            }
        }
        return 0;
    }
}
