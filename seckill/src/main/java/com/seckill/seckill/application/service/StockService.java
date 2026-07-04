package com.seckill.seckill.application.service;

public interface StockService {
    void preheatStock(Long seckillId);
    long DEDUCT_SUCCESS = 1L;
    long DEDUCT_NO_STOCK = 0L;
    long DEDUCT_NOT_PREHEAT = -1L;
    long DEDUCT_REPEAT = 2L;

    Long tryDeduct(Long seckillId, Long userId);

    Long rollback(Long seckillId,Long userId);
}
