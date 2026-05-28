package com.seckill.goods.domain.repository;

import com.seckill.common.enums.SortOrder;
import com.seckill.goods.domain.entity.Goods;

import java.util.List;
import java.util.Optional;

/**
 * 商品仓储接口(domain 层,不依赖任何框架)
 */
public interface GoodsRepository {

    /**
     * 根据 id 查询商品
     */
    Optional<Goods> findById(Long id);

    /**
     * 查询所有在售商品(化简版,不分页)
     */
    List<Goods> findAllOnSale(SortOrder asc);

    /**
     * 保存商品(新增或更新)
     */
    void save(Goods goods);
}