package com.seckill.infrastructure.Cache;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GuavaBloomFilterConfig {

    @Bean("goodsBloomFilter")
    public BloomFilter<Long> goodsBloomFilter() {
        return BloomFilter.create(
                Funnels.longFunnel(),
                10_000,
                0.01
        );
    }

    @Bean("seckillBloomFilter")
    public BloomFilter<Long> seckillBloomFilter() {
        return BloomFilter.create(
                Funnels.longFunnel(),
                10_000,
                0.01
        );
    }
}