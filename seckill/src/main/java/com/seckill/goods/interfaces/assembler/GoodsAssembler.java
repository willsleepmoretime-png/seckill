package com.seckill.goods.interfaces.assembler;

import com.seckill.goods.domain.entity.Goods;
import com.seckill.goods.interfaces.vo.GoodsVO;

import java.util.List;
import java.util.stream.Collectors;

public class GoodsAssembler {

    public static GoodsVO toVO(Goods goods) {
        return new GoodsVO(
                goods.getId(),
                goods.getName(),
                goods.getDescription(),
                goods.getImageUrl(),
                goods.getPrice(),
                goods.getStock(),
                goods.getStatus().value()   // 枚举转数字
        );
    }

    public static List<GoodsVO> toVOList(List<Goods> list) {
        return list.stream()
                .map(GoodsAssembler::toVO)
                .collect(Collectors.toList());
    }
}