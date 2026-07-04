package com.seckill.infrastructure.Cache;


import com.google.common.hash.BloomFilter;
import com.seckill.common.enums.SortOrder;
import com.seckill.goods.domain.entity.Goods;
import com.seckill.goods.domain.repository.GoodsRepository;

import com.seckill.seckill.domain.entity.Seckill;
import com.seckill.seckill.domain.repository.SeckillRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

//初始化
@Slf4j
@RequiredArgsConstructor
@Component
public class GuavaBloomFilterInitializer {
    private  final GoodsRepository goodsRepository;
    private  final SeckillRepository seckillRepository;
    //订单号和商单号都是Long
    @Resource(name = "goodsBloomFilter")
    private BloomFilter<Long> goodsBloomFilter;

    @Resource(name = "seckillBloomFilter")
    private BloomFilter<Long> seckillBloomFilter;

    private  boolean isReady=false;
    @PostConstruct
    public void init(){
        //要将goods，seckill 数据加入到这个中
        log.info("===============================================");
        log.info("🚀 正在初始化 Guava BloomFilter 防穿透保护...");
        try{
            List<Long> allGoodsIds = goodsRepository.findAllOnSale(SortOrder.ASC)
                    .stream().
                    map(Goods::getId)
                    .collect(Collectors.toList());
            if (allGoodsIds != null && !allGoodsIds.isEmpty()) {
                for (Long id : allGoodsIds) {
                    goodsBloomFilter.put(id);
                }
                log.info("✅ 商品 BloomFilter 初始化完成，加载了 {} 个 ID。", allGoodsIds.size());
            }

            // 2. 预热秒杀活动 ID
            List<Long> allSeckillIds = seckillRepository.findAll()
                    .stream()
                    .map(Seckill::getId)
                    .collect(Collectors.toList());

            if (allSeckillIds != null && !allSeckillIds.isEmpty()) {
                for (Long id : allSeckillIds) {
                    seckillBloomFilter.put(id);
                }
                log.info("✅ 秒杀活动 BloomFilter 初始化完成，加载了 {} 个 ID。", allSeckillIds.size());
            }

            // 数据全部加载完毕后，将布隆过滤器标记为“可信”
            isReady = true;
        }catch (Exception e) {
            log.error("❌ BloomFilter 初始化失败！降级为不可信状态，所有请求将放行至数据库兜底", e);
        }
        log.info("===============================================");
    }
}
