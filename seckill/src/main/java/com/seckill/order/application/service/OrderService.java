package com.seckill.order.application.service;

import com.seckill.infrastructure.message.SeckillMessage;
import com.seckill.order.interfaces.vo.OrderVO;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    void pay(Long orderId, Long userId);

    OrderVO viewDetails(Long orderId, Long userId);

    void cancel(Long orderId, Long userId);

    List<OrderVO> listMyOrders(Long userId);

    OrderVO createSeckillOrder(Long userId, Long goodsId, Long seckillId,
                               String goodsName, BigDecimal seckillPrice);

}
