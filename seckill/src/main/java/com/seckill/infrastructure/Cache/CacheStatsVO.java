package com.seckill.infrastructure.Cache;

public record CacheStatsVO(
        long bloomReject,
        long caffeineHit,
        long caffeineMiss,
        double caffeineHitRate,
        long caffeineEviction,
        long redisHit,
        long redisMiss,
        double redisHitRate,
        long dbQuery,
        long cacheInvalidationPublished,
        long cacheInvalidationReceived
) {
}
