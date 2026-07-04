package com.seckill.seckill.application.service.Impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.hash.BloomFilter;
import com.seckill.common.enums.SortOrder;
import com.seckill.goods.domain.entity.Goods;
import com.seckill.goods.domain.repository.GoodsRepository;
import com.seckill.infrastructure.Cache.CacheInvalidationPublisher;
import com.seckill.infrastructure.Cache.CacheStats;
import com.seckill.infrastructure.Cache.MultiLevelCacheLocalInvalidator;
import com.seckill.seckill.application.service.MultiLevelCacheService;
import com.seckill.seckill.domain.entity.Seckill;
import com.seckill.seckill.domain.repository.SeckillRepository;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MultiLevelCacheServiceImpl implements MultiLevelCacheService, MultiLevelCacheLocalInvalidator {

    // redis 前缀标签
    private static final String GOODS_DETAIL_KEY_PREFIX = "goods:detail:";
    private static final String GOODS_LIST_ON_SALE_KEY_PREFIX = "goods:list:on-sale:";
    private static final String SECKILL_DETAIL_KEY_PREFIX = "seckill:detail:";
    private static final String SECKILL_LIST_IN_PROGRESS_KEY = "seckill:list:in-progress";
    private static final String CACHE_LOCK_KEY_PREFIX = "lock:cache:";
    private static final String NULL_CACHE_VALUE = "__NULL__";

    //过期时间常量
    private static final long GOODS_DETAIL_TTL_MINUTES = 10;
    private static final long GOODS_LIST_TTL_MINUTES = 1;
    private static final long SECKILL_DETAIL_TTL_MINUTES = 1;
    private static final long SECKILL_LIST_TTL_SECONDS = 30;
    private static final long NULL_CACHE_TTL_SECONDS = 60;
    private static final long TTL_JITTER_SECONDS = 30;
    private static final long LOCK_WAIT_SECONDS = 1;
    private static final long LOCK_LEASE_SECONDS = 10;
    private static final int CACHE_REBUILD_RETRY_TIMES = 3;
    private static final long CACHE_REBUILD_RETRY_INTERVAL_MILLIS = 50;

    // BloomFilter Caffeine
    private final Cache<Long, Seckill> seckillDetailCache;
    private final Cache<String, List<Seckill>> seckillListCache;
    private final Cache<Long, Goods> goodsDetailCache;
    private final Cache<String, List<Goods>> goodsListCache;
    private final BloomFilter<Long> goodsBloomFilter;
    private final BloomFilter<Long> seckillBloomFilter;

    private final SeckillRepository seckillRepository;
    private final GoodsRepository goodsRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private final RedissonClient redissonClient;
    private final CacheInvalidationPublisher invalidationPublisher;
    private final CacheStats cacheStats;

    //构造函数
    public MultiLevelCacheServiceImpl(
            @Qualifier("seckillDetailCache") Cache<Long, Seckill> seckillDetailCache,
            @Qualifier("seckillListCache") Cache<String, List<Seckill>> seckillListCache,
            @Qualifier("goodsDetailCache") Cache<Long, Goods> goodsDetailCache,
            @Qualifier("goodsListCache") Cache<String, List<Goods>> goodsListCache,
            SeckillRepository seckillRepository,
            GoodsRepository goodsRepository,
            RedisTemplate<String, Object> redisTemplate,
            @Qualifier("goodsBloomFilter") BloomFilter<Long> goodsBloomFilter,
            @Qualifier("seckillBloomFilter") BloomFilter<Long> seckillBloomFilter,
            RedissonClient redissonClient,
            CacheInvalidationPublisher invalidationPublisher,
            CacheStats cacheStats
    ) {
        this.seckillDetailCache = seckillDetailCache;
        this.seckillListCache = seckillListCache;
        this.goodsDetailCache = goodsDetailCache;
        this.goodsListCache = goodsListCache;
        this.seckillRepository = seckillRepository;
        this.goodsRepository = goodsRepository;
        this.redisTemplate = redisTemplate;
        this.goodsBloomFilter = goodsBloomFilter;
        this.seckillBloomFilter = seckillBloomFilter;
        this.redissonClient = redissonClient;
        this.invalidationPublisher = invalidationPublisher;
        this.cacheStats = cacheStats;
    }

    @Override
    public Seckill getSeckillDetail(Long seckillId) {
        //先进BloomFilter 查看有或者没有
        if (!seckillBloomFilter.mightContain(seckillId)) {
            cacheStats.incrementBloomReject();
            log.warn("BloomFilter rejected seckill id: {}", seckillId);
            return null;
        }

        //检查 caffeine
        Seckill localValue = seckillDetailCache.getIfPresent(seckillId);
        if (localValue != null) {
            return localValue;
        }

        //检查Redis
        String redisKey = SECKILL_DETAIL_KEY_PREFIX + seckillId;
        Object redisValue = redisTemplate.opsForValue().get(redisKey);
        if (NULL_CACHE_VALUE.equals(redisValue)) {
            cacheStats.incrementRedisHit();
            return null;
        }
        if (redisValue instanceof Seckill seckill) {
            cacheStats.incrementRedisHit();
            seckillDetailCache.put(seckillId, seckill);
            return seckill;
        }
        cacheStats.incrementRedisMiss();

        //缓存重建时候上锁 避免过期的数据，在数据库中被大量查询
        RLock lock = redissonClient.getLock(CACHE_LOCK_KEY_PREFIX + redisKey);
        boolean locked = false;
        try {
            //设置等待时间和取用时间
            locked = lock.tryLock(LOCK_WAIT_SECONDS, LOCK_LEASE_SECONDS, TimeUnit.SECONDS);
            //当用户现在没有抢到锁的时候 ，再一次检查Redis
            if (!locked) {
                Object retryValue = retryReadRedis(redisKey);
                if (NULL_CACHE_VALUE.equals(retryValue)) {
                    cacheStats.incrementRedisHit();
                    return null;
                }
                if (retryValue instanceof Seckill seckill) {
                    cacheStats.incrementRedisHit();
                    seckillDetailCache.put(seckillId, seckill);
                    return seckill;
                }
                return null;
            }

            Object redisValueAfterLock = redisTemplate.opsForValue().get(redisKey);
            if (NULL_CACHE_VALUE.equals(redisValueAfterLock)) {
                cacheStats.incrementRedisHit();
                return null;
            }
            if (redisValueAfterLock instanceof Seckill seckill) {
                cacheStats.incrementRedisHit();
                seckillDetailCache.put(seckillId, seckill);
                return seckill;
            }
            cacheStats.incrementRedisMiss();

            cacheStats.incrementDbQuery();
            Seckill dbValue = seckillRepository.findById(seckillId);
            if (dbValue == null) {
                cacheNullValue(redisKey);
                return null;
            }

            seckillBloomFilter.put(seckillId);
            setWithJitter(redisKey, dbValue, TimeUnit.MINUTES.toSeconds(SECKILL_DETAIL_TTL_MINUTES));
            seckillDetailCache.put(seckillId, dbValue);
            return dbValue;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public List<Seckill> getListSeckill() {
        //caffeine
        List<Seckill> localValue = seckillListCache.getIfPresent(SECKILL_LIST_IN_PROGRESS_KEY);
        if (localValue != null) {
            return localValue;
        }
        //redis
        Object redisValue = redisTemplate.opsForValue().get(SECKILL_LIST_IN_PROGRESS_KEY);
        if (redisValue instanceof List<?> list) {
            cacheStats.incrementRedisHit();
            @SuppressWarnings("unchecked")
            List<Seckill> seckillList = (List<Seckill>) list;
            seckillListCache.put(SECKILL_LIST_IN_PROGRESS_KEY, seckillList);
            return seckillList;
        }
        cacheStats.incrementRedisMiss();

        RLock lock = redissonClient.getLock(CACHE_LOCK_KEY_PREFIX + SECKILL_LIST_IN_PROGRESS_KEY);
        boolean locked = false;
        try {
            locked = lock.tryLock(LOCK_WAIT_SECONDS, LOCK_LEASE_SECONDS, TimeUnit.SECONDS);
            if (!locked) {
                Object retryValue = retryReadRedis(SECKILL_LIST_IN_PROGRESS_KEY);
                if (retryValue instanceof List<?> list) {
                    cacheStats.incrementRedisHit();
                    @SuppressWarnings("unchecked")
                    List<Seckill> seckillList = (List<Seckill>) list;
                    seckillListCache.put(SECKILL_LIST_IN_PROGRESS_KEY, seckillList);
                    return seckillList;
                }
                return List.of();
            }

            Object redisValueAfterLock = redisTemplate.opsForValue().get(SECKILL_LIST_IN_PROGRESS_KEY);
            if (redisValueAfterLock instanceof List<?> list) {
                cacheStats.incrementRedisHit();
                @SuppressWarnings("unchecked")
                List<Seckill> seckillList = (List<Seckill>) list;
                seckillListCache.put(SECKILL_LIST_IN_PROGRESS_KEY, seckillList);
                return seckillList;
            }
            cacheStats.incrementRedisMiss();

            cacheStats.incrementDbQuery();
            List<Seckill> dbValue = seckillRepository.findInProgressList();
            setWithJitter(SECKILL_LIST_IN_PROGRESS_KEY, dbValue, SECKILL_LIST_TTL_SECONDS);
            seckillListCache.put(SECKILL_LIST_IN_PROGRESS_KEY, dbValue);
            return dbValue;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return List.of();
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public Goods getGoodsDetail(Long goodsId) {
        if (!goodsBloomFilter.mightContain(goodsId)) {
            cacheStats.incrementBloomReject();
            log.warn("BloomFilter rejected goods id: {}", goodsId);
            return null;
        }

        Goods localValue = goodsDetailCache.getIfPresent(goodsId);
        if (localValue != null) {
            return localValue;
        }

        String redisKey = GOODS_DETAIL_KEY_PREFIX + goodsId;
        Object redisValue = redisTemplate.opsForValue().get(redisKey);
        if (NULL_CACHE_VALUE.equals(redisValue)) {
            cacheStats.incrementRedisHit();
            return null;
        }
        if (redisValue instanceof Goods goods) {
            cacheStats.incrementRedisHit();
            goodsDetailCache.put(goodsId, goods);
            return goods;
        }
        cacheStats.incrementRedisMiss();

        RLock lock = redissonClient.getLock(CACHE_LOCK_KEY_PREFIX + redisKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(LOCK_WAIT_SECONDS, LOCK_LEASE_SECONDS, TimeUnit.SECONDS);
            if (!locked) {
                Object retryValue = retryReadRedis(redisKey);
                if (NULL_CACHE_VALUE.equals(retryValue)) {
                    cacheStats.incrementRedisHit();
                    return null;
                }
                if (retryValue instanceof Goods goods) {
                    cacheStats.incrementRedisHit();
                    goodsDetailCache.put(goodsId, goods);
                    return goods;
                }
                return null;
            }

            Object redisValueAfterLock = redisTemplate.opsForValue().get(redisKey);
            if (NULL_CACHE_VALUE.equals(redisValueAfterLock)) {
                cacheStats.incrementRedisHit();
                return null;
            }
            if (redisValueAfterLock instanceof Goods goods) {
                cacheStats.incrementRedisHit();
                goodsDetailCache.put(goodsId, goods);
                return goods;
            }
            cacheStats.incrementRedisMiss();

            cacheStats.incrementDbQuery();
            Goods dbValue = goodsRepository.findById(goodsId).orElse(null);
            if (dbValue == null) {
                cacheNullValue(redisKey);
                return null;
            }

            goodsBloomFilter.put(goodsId);
            setWithJitter(redisKey, dbValue, TimeUnit.MINUTES.toSeconds(GOODS_DETAIL_TTL_MINUTES));
            goodsDetailCache.put(goodsId, dbValue);
            return dbValue;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public List<Goods> getListGoods(SortOrder order) {
        String listKey = GOODS_LIST_ON_SALE_KEY_PREFIX + order.name().toLowerCase();
        List<Goods> localValue = goodsListCache.getIfPresent(listKey);
        if (localValue != null) {
            return localValue;
        }

        Object redisValue = redisTemplate.opsForValue().get(listKey);
        if (redisValue instanceof List<?> list) {
            cacheStats.incrementRedisHit();
            @SuppressWarnings("unchecked")
            List<Goods> goodsList = (List<Goods>) list;
            goodsListCache.put(listKey, goodsList);
            return goodsList;
        }
        cacheStats.incrementRedisMiss();

        RLock lock = redissonClient.getLock(CACHE_LOCK_KEY_PREFIX + listKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(LOCK_WAIT_SECONDS, LOCK_LEASE_SECONDS, TimeUnit.SECONDS);
            if (!locked) {
                Object retryValue = retryReadRedis(listKey);
                if (retryValue instanceof List<?> list) {
                    cacheStats.incrementRedisHit();
                    @SuppressWarnings("unchecked")
                    List<Goods> goodsList = (List<Goods>) list;
                    goodsListCache.put(listKey, goodsList);
                    return goodsList;
                }
                return List.of();
            }

            Object redisValueAfterLock = redisTemplate.opsForValue().get(listKey);
            if (redisValueAfterLock instanceof List<?> list) {
                cacheStats.incrementRedisHit();
                @SuppressWarnings("unchecked")
                List<Goods> goodsList = (List<Goods>) list;
                goodsListCache.put(listKey, goodsList);
                return goodsList;
            }
            cacheStats.incrementRedisMiss();

            cacheStats.incrementDbQuery();
            List<Goods> dbValue = goodsRepository.findAllOnSale(order);
            setWithJitter(listKey, dbValue, TimeUnit.MINUTES.toSeconds(GOODS_LIST_TTL_MINUTES));
            goodsListCache.put(listKey, dbValue);
            return dbValue;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return List.of();
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    //删除Redis 删除Caffeine 广播信息
    @Override
    public void invalidateSeckillDetail(Long seckillId) {
        String key = SECKILL_DETAIL_KEY_PREFIX + seckillId;
        redisTemplate.delete(key);
        invalidateLocal(key);
        invalidationPublisher.publish("SECKILL", key);
    }

    @Override
    public void invalidateSeckillList() {
        redisTemplate.delete(SECKILL_LIST_IN_PROGRESS_KEY);
        invalidateLocal(SECKILL_LIST_IN_PROGRESS_KEY);
        invalidationPublisher.publish("SECKILL", SECKILL_LIST_IN_PROGRESS_KEY);
    }

    //多级缓存一致性
    @Override
    public void invalidateLocal(String key) {
        if (key == null) {
            return;
        }
        if (key.startsWith(SECKILL_DETAIL_KEY_PREFIX)) {
            Long seckillId = parseId(key, SECKILL_DETAIL_KEY_PREFIX);
            if (seckillId != null) {
                seckillDetailCache.invalidate(seckillId);
            }
            return;
        }
        if (SECKILL_LIST_IN_PROGRESS_KEY.equals(key)) {
            seckillListCache.invalidate(SECKILL_LIST_IN_PROGRESS_KEY);
            return;
        }
        if (key.startsWith(GOODS_DETAIL_KEY_PREFIX)) {
            Long goodsId = parseId(key, GOODS_DETAIL_KEY_PREFIX);
            if (goodsId != null) {
                goodsDetailCache.invalidate(goodsId);
            }
            return;
        }
        if (key.startsWith(GOODS_LIST_ON_SALE_KEY_PREFIX)) {
            goodsListCache.invalidate(key);
        }
    }

    //解析key 取出前缀prefix
    private Long parseId(String key, String prefix) {
        try {
            // 去掉前缀
            return Long.valueOf(key.substring(prefix.length()));
        } catch (RuntimeException e) {
            log.warn("Invalid cache key id, key={}, prefix={}", key, prefix);
            return null;
        }
    }

    //为了防止一批数据都崩掉了，加随机的抖动时间
    private void cacheNullValue(String redisKey) {
        setWithJitter(redisKey, NULL_CACHE_VALUE, NULL_CACHE_TTL_SECONDS);
    }

    private Object retryReadRedis(String redisKey) throws InterruptedException {
        for (int i = 0; i < CACHE_REBUILD_RETRY_TIMES; i++) {
            TimeUnit.MILLISECONDS.sleep(CACHE_REBUILD_RETRY_INTERVAL_MILLIS);
            Object retryValue = redisTemplate.opsForValue().get(redisKey);
            if (retryValue != null) {
                return retryValue;
            }
        }
        return null;
    }

    private void setWithJitter(String redisKey, Object value, long baseTtlSeconds) {
        long ttlSeconds = baseTtlSeconds + ThreadLocalRandom.current().nextLong(TTL_JITTER_SECONDS + 1);
        redisTemplate.opsForValue().set(redisKey, value, ttlSeconds, TimeUnit.SECONDS);
    }
}
