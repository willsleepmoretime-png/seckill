package com.seckill.infrastructure.Cache;

import org.springframework.util.StringUtils;

public record CacheInvalidationMessage(
        String cacheType,
        String key,
        long timestamp
) {

    public static CacheInvalidationMessage of(String cacheType, String key) {
        return new CacheInvalidationMessage(cacheType, key, System.currentTimeMillis());
    }

    public boolean isValid() {
        return StringUtils.hasText(cacheType)
                && StringUtils.hasText(key)
                && timestamp > 0;
    }
}
