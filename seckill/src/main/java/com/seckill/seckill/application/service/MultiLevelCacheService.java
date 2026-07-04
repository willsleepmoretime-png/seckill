package com.seckill.seckill.application.service;

import com.seckill.common.enums.SortOrder;
import com.seckill.goods.domain.entity.Goods;
import com.seckill.seckill.domain.entity.Seckill;

import java.util.List;

public interface MultiLevelCacheService {

    Seckill getSeckillDetail(Long seckillId);
    List<Seckill> getListSeckill();
    Goods  getGoodsDetail(Long goods);
    List<Goods> getListGoods(SortOrder order);

    void invalidateSeckillDetail(Long seckillId);

    void invalidateSeckillList();

}
