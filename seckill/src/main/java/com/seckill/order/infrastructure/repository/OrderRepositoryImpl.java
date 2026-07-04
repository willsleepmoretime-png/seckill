package com.seckill.order.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.order.domain.constant.OrderStatusEnum;
import com.seckill.order.domain.entity.Order;
import com.seckill.order.domain.repository.OrderRepository;
import com.seckill.order.infrastructure.mapper.OrderMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class OrderRepositoryImpl extends ServiceImpl<OrderMapper, Order>
        implements OrderRepository {

    @Override
    public boolean save(Order order) {
      return   super.save(order);            // MP 自带,insert
    }

    @Override
    public Order findById(Long orderId) {
        return getById(orderId);      // MP 自带
    }

    @Override
    public boolean updateById(Order order) {
        return super.updateById(order);  // MP 自带,按 id 更新
    }

    // Impl
    @Override
    public List<Order> listByUserId(Long userId) {
        return lambdaQuery()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime)
                .list();
    }
    @Override
    public Order findByMsg(Long userId,Long seckillId){
        return lambdaQuery()
                .eq(Order::getUserId,userId)
                .eq(Order::getSeckillId,seckillId)
                .one();
    }

    @Override
    public boolean cancelByCas(Long orderId, Long userId, Integer version) {
        return lambdaUpdate()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId)
                .eq(Order::getStatus, OrderStatusEnum.NOT_PAYMENT)
                .eq(Order::getVersion,version)
                .set(Order::getStatus,OrderStatusEnum.CANCEL)
                .set(Order::getUpdateTime, LocalDateTime.now())
                .set(Order::getVersion,version+1)
                .update();
    }

    @Override
    public boolean payByCas(Long orderId, Long userId, Integer version){
        return lambdaUpdate()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId)
                .eq(Order::getStatus, OrderStatusEnum.NOT_PAYMENT)
                .eq(Order::getVersion,version)
                .set(Order::getStatus,OrderStatusEnum.HAD_PAID)
                .set(Order::getPayTime, LocalDateTime.now())
                .set(Order::getVersion,version+1)
                .update();
    }
}