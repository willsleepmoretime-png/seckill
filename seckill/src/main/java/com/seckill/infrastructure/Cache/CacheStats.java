package com.seckill.infrastructure.Cache;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class CacheStats {
    //AtomicLong 多线程 高效（无锁） 线程安全 用来缓存系统性能监控指标（Metrics）计数器。
    private final AtomicLong bloomReject = new AtomicLong();
    private final AtomicLong redisHit = new AtomicLong();
    private final AtomicLong redisMiss = new AtomicLong();
    private final AtomicLong dbQuery = new AtomicLong();
    private final AtomicLong cacheInvalidationPublished = new AtomicLong();
    private final AtomicLong cacheInvalidationReceived = new AtomicLong();

    //增/加
    public void incrementBloomReject() {
        bloomReject.incrementAndGet();
    }

    public void incrementRedisHit() {
        redisHit.incrementAndGet();
    }

    public void incrementRedisMiss() {
        redisMiss.incrementAndGet();
    }

    public void incrementDbQuery() {
        dbQuery.incrementAndGet();
    }

    public void incrementCacheInvalidationPublished() {
        cacheInvalidationPublished.incrementAndGet();
    }

    public void incrementCacheInvalidationReceived() {
        cacheInvalidationReceived.incrementAndGet();
    }

    //查
    public long getBloomReject() {
        return bloomReject.get();
    }

    public long getRedisHit() {
        return redisHit.get();
    }

    public long getRedisMiss() {
        return redisMiss.get();
    }

    public long getDbQuery() {
        return dbQuery.get();
    }

    public long getCacheInvalidationPublished() {
        return cacheInvalidationPublished.get();
    }

    public long getCacheInvalidationReceived() {
        return cacheInvalidationReceived.get();
    }
}
