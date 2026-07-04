package com.seckill.infrastructure.Cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.seckill.goods.domain.entity.Goods;
import com.seckill.seckill.domain.entity.Seckill;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

//商品cache 会设置成为
@Configuration
public class LocalCacheConfig {

    //Caffeine 模板
    private <K,V> Cache<K,V> buildCache(long maxSize,long expireSeconds){
        return Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds,TimeUnit.SECONDS)
                .recordStats()
                .build();
    }


    @Bean
    public Cache<Long, Seckill> seckillDetailCache() {
        return buildCache(10_000, 30);
    }

    @Bean
    public Cache<String, List<Seckill>> seckillListCache() {
        return buildCache(100, 5);
    }

    @Bean
    public Cache<Long, Goods> goodsDetailCache() {
        return buildCache(10_000, 60);
    }

    @Bean
    public Cache<String, List<Goods>> goodsListCache() {
        return buildCache(100, 10);
    }
}
