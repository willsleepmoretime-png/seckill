package com.seckill.goods.application.service.Impl;



import com.seckill.common.enums.SortOrder;
import com.seckill.common.exception.BusinessException;
import com.seckill.common.result.ResultCode;
import com.seckill.goods.application.service.GoodsService;
import com.seckill.goods.domain.entity.Goods;
import com.seckill.goods.domain.repository.GoodsRepository;

import com.seckill.goods.interfaces.assembler.GoodsAssembler;
import com.seckill.goods.interfaces.vo.GoodsVO;
import com.seckill.seckill.application.service.MultiLevelCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class GoodsServiceImpl implements GoodsService {

    private final GoodsRepository goodsRepository;
    private final MultiLevelCacheService multiLevelCacheService;
    @Override
    public Goods getById(Long id){
        Goods goods = multiLevelCacheService.getGoodsDetail(id);
        if (goods == null) {
            throw new BusinessException(ResultCode.GOODS_NOT_FOUND);
        }
        return goods;
    }

    @Override
    public List<Goods> listOnSale(SortOrder order){
        return  multiLevelCacheService.getListGoods(order);

    }
}
