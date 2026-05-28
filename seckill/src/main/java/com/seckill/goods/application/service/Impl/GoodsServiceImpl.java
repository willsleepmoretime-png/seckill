package com.seckill.goods.application.service.Impl;



import com.seckill.common.enums.SortOrder;
import com.seckill.common.exception.BusinessException;
import com.seckill.common.result.ResultCode;
import com.seckill.goods.application.service.GoodsService;
import com.seckill.goods.domain.entity.Goods;
import com.seckill.goods.domain.repository.GoodsRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
public class GoodsServiceImpl implements GoodsService {

    private final GoodsRepository goodsRepository;

    @Override
    public Goods getById(Long id){
        return goodsRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.GOODS_NOT_FOUND));
    }

    @Override
    public List<Goods> listOnSale(SortOrder order){
        return  goodsRepository.findAllOnSale(order);
    }
}
