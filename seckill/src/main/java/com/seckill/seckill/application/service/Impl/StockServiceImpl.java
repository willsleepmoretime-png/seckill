package com.seckill.seckill.application.service.Impl;

import com.seckill.common.exception.BusinessException;
import com.seckill.common.result.ResultCode;
import com.seckill.seckill.application.service.MultiLevelCacheService;
import com.seckill.seckill.application.service.StockService;
import com.seckill.seckill.domain.constant.SeckillStatusEnum;
import com.seckill.seckill.domain.entity.Seckill;
import com.seckill.seckill.domain.repository.SeckillRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class StockServiceImpl implements StockService {

    private  final StringRedisTemplate redisTemplate;
    private  final SeckillRepository seckillRepository;
    private final RedisScript<Long> seckillScript;
    private final RedisScript<Long> rollbackScript; ;
    private final RedissonClient redissonClient;

    private static final String STOCK_KEY_PREFIX = "seckill:stock:";
    private static final String BOUGHT_KEY_PREFIX = "seckill:bought:";
    @Override
    public void preheatStock(Long seckillId){
        Seckill seckill = seckillRepository.findById(seckillId);
            RLock lock=redissonClient.getLock("preheat:lock:"+ seckillId);
        try {
            // tryLock(等待0秒, 锁租期10秒, 单位)
            // 等待0 = 抢不到立刻放弃（预热不需要排队，别人在刷就够了）
            if (!lock.tryLock(0, 10, TimeUnit.SECONDS)) {
                return;   // 没抢到锁 = 别人正在预热
            }
            try {
                 seckill = seckillRepository.findById(seckillId);
                if (seckill == null) {
                    throw new BusinessException(ResultCode.SECKILL_NOT_EXISTS);
                } else if (seckill.getStatus().equals(SeckillStatusEnum.ENDED)) {
                    throw new BusinessException(ResultCode.SECKILL_ENDED);
                }
                Boolean ok = redisTemplate.opsForValue()
                        .setIfAbsent(STOCK_KEY_PREFIX + seckillId,
                                String.valueOf(seckill.getStockCount()));
                if (Boolean.FALSE.equals(ok)) {
                    throw new BusinessException(ResultCode.SECKILL_ALREADY_PREHEATED);
                }
            }finally{
                lock.unlock();
            }
        }catch(InterruptedException e){
                Thread.currentThread().interrupt();   // tryLock 会抛中断异常，规范处理
                throw new BusinessException(ResultCode.SECKILL_PREHEAT_INTERRUPTED);
            }
    }


    //Lua 脚本
    @Override
    public Long tryDeduct(Long seckillId, Long userId) {
        return redisTemplate.execute(
                seckillScript,
                java.util.List.of(STOCK_KEY_PREFIX + seckillId, BOUGHT_KEY_PREFIX + seckillId),
                String.valueOf(userId)
        );
    }

    @Override
    public Long rollback(Long seckillId,Long userId){
        return redisTemplate.execute(
                    rollbackScript,
                    List.of(STOCK_KEY_PREFIX + seckillId,                    // → KEYS[1]
                        BOUGHT_KEY_PREFIX + seckillId),                  // → KEYS[2]
                        String.valueOf(userId)
        );
    }
}
