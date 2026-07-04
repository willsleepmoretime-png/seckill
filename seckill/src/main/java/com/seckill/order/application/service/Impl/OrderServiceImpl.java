package com.seckill.order.application.service.Impl;

import com.seckill.common.exception.BusinessException;
import com.seckill.common.result.ResultCode;
import com.seckill.infrastructure.message.SeckillMessage;
import com.seckill.order.application.service.OrderService;
import com.seckill.order.domain.constant.OrderStatusEnum;
import com.seckill.order.domain.entity.Order;
import com.seckill.order.domain.repository.OrderRepository;
import com.seckill.order.interfaces.assembler.OrderAssembler;
import com.seckill.order.interfaces.vo.OrderVO;
import com.seckill.seckill.application.service.StockService;
import com.seckill.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private  final UserRepository userRepository;
    private  final OrderRepository orderRepository;
    private  final StockService stockService;
    @Override
    public void  cancel(Long orderId, Long userId){
         Order order= orderRepository.findById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }
       if(!order.getUserId().equals(userId)){
            throw new BusinessException(ResultCode.ORDER_NOT_BELONG_TO_USER);
       }
       order.cancel();
        boolean ok = orderRepository.cancelByCas(orderId,userId,order.getVersion());
        if (!ok) throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        if (order.getSeckillId() != null) {
            stockService.rollback(order.getSeckillId(), userId);
        }

    }


    //基于锁做的
    @Transactional
    @Override
    public void pay(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_BELONG_TO_USER);
        }
        // 先改订单状态(内含"必须是待支付"的校验),拦住重复支付
        order.pay();
        // 2. CAS:带 version 的 update 当并发闸门,只有一个线程能 ok=true
        boolean ok = orderRepository.payByCas(order.getId(),order.getUserId(),order.getVersion());
        if (!ok) {
            Order latest=orderRepository.findById(orderId);
            if(latest!=null&&latest.getStatus()== OrderStatusEnum.HAD_PAID) {
                return;// 撞车/重复支付,幂等返回,绝不扣款
            }
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }

        // 再扣款
        boolean deducted = userRepository.deductBalance(userId, order.getOrderAmount());
        if (!deducted) {
            throw new BusinessException(ResultCode.INSUFFICIENT_BALANCE);
        }
    }

    @Override
    public OrderVO viewDetails(Long orderId, Long userId){
        Order order= orderRepository.findById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }
        if(!order.getUserId().equals(userId)){
            throw new BusinessException(ResultCode.ORDER_NOT_BELONG_TO_USER);
        }
        return OrderAssembler.toVO(order);
    }

    @Override
    public List<OrderVO> listMyOrders(Long userId) {
        List<Order> orders = orderRepository.listByUserId(userId);
        return orders.stream().map(OrderAssembler::toVO).toList();
    }

    @Override
    public OrderVO createSeckillOrder(Long userId, Long goodsId, Long seckillId,
                                      String goodsName, BigDecimal seckillPrice) {
        // 秒杀固定买 1 件，成交价用秒杀价
        Order order = Order.create(userId, goodsId, seckillId, goodsName, seckillPrice, 1);
        orderRepository.save(order);
        return OrderAssembler.toVO(order);
    }


}
