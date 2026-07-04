package com.seckill.infrastructure.Cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.seckill.goods.domain.entity.Goods;
import com.seckill.seckill.domain.entity.Seckill;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CacheStatsService {

    private final Cache<Long, Seckill> seckillDetailCache;
    private final Cache<String, List<Seckill>> seckillListCache;
    private final Cache<Long, Goods> goodsDetailCache;
    private final Cache<String, List<Goods>> goodsListCache;
    private final CacheStats cacheStats;

    public CacheStatsService(
            @Qualifier("seckillDetailCache") Cache<Long, Seckill> seckillDetailCache,
            @Qualifier("seckillListCache") Cache<String, List<Seckill>> seckillListCache,
            @Qualifier("goodsDetailCache") Cache<Long, Goods> goodsDetailCache,
            @Qualifier("goodsListCache") Cache<String, List<Goods>> goodsListCache,
            CacheStats cacheStats
    ) {
        this.seckillDetailCache = seckillDetailCache;
        this.seckillListCache = seckillListCache;
        this.goodsDetailCache = goodsDetailCache;
        this.goodsListCache = goodsListCache;
        this.cacheStats = cacheStats;
    }

    public CacheStatsVO currentStats() {
        com.github.benmanes.caffeine.cache.stats.CacheStats caffeineStats =
                seckillDetailCache.stats()
                        .plus(seckillListCache.stats())
                        .plus(goodsDetailCache.stats())
                        .plus(goodsListCache.stats());

        long redisHit = cacheStats.getRedisHit();
        long redisMiss = cacheStats.getRedisMiss();
        long redisTotal = redisHit + redisMiss;
        double redisHitRate = redisTotal == 0 ? 0.0 : (double) redisHit / redisTotal;

        return new CacheStatsVO(
                cacheStats.getBloomReject(),
                caffeineStats.hitCount(),
                caffeineStats.missCount(),
                caffeineStats.hitRate(),
                caffeineStats.evictionCount(),
                redisHit,
                redisMiss,
                redisHitRate,
                cacheStats.getDbQuery(),
                cacheStats.getCacheInvalidationPublished(),
                cacheStats.getCacheInvalidationReceived()
        );
    }
}
