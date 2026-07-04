package com.seckill.order.interfaces.assembler;

import com.seckill.order.domain.entity.Order;
import com.seckill.order.interfaces.vo.OrderVO;

public class OrderAssembler {

    public static OrderVO toVO(Order order) {
        if (order == null) {
            return null;
        }
        return OrderVO.builder()
                .orderId(order.getId())
                .goodsId(order.getGoodsId())
                .seckillId(order.getSeckillId())
                .goodsName(order.getGoodsName())
                .goodsPrice(order.getGoodsPrice())
                .goodsCount(order.getGoodsCount())
                .orderAmount(order.getOrderAmount())
                .status(order.getStatus().getCode())       // 枚举转 code 给前端
                .payTime(order.getPayTime())
                .createTime(order.getCreateTime())
                .build();
    }
}