package com.seckill.order.domain.repository;

import com.seckill.order.domain.entity.Order;

import java.util.List;

public interface OrderRepository {
    boolean save(Order order);
    Order findById(Long orderId);
    boolean updateById(Order order);
    // 接口
    List<Order> listByUserId(Long userId);

    Order findByMsg(Long userId,Long orderId);

    boolean payByCas(Long orderId, Long userId, Integer version);
    boolean cancelByCas(Long orderId, Long userId, Integer version);
}