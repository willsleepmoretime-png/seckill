package com.seckill.goods.application.service;


import com.seckill.common.enums.SortOrder;
import com.seckill.goods.domain.entity.Goods;
import com.seckill.goods.interfaces.vo.GoodsVO;

import java.util.List;

public interface GoodsService {

    Goods getById(Long id);

    List<Goods> listOnSale(SortOrder Order);
}
