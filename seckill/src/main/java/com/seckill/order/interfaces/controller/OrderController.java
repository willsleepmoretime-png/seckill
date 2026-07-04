package com.seckill.order.interfaces.controller;

import com.seckill.common.context.UserContext;
import com.seckill.common.result.Result;
import com.seckill.order.application.service.OrderService;
import com.seckill.order.interfaces.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 支付订单
     */
    @PostMapping("/pay/{orderId}")
    public Result<Void> pay(@PathVariable Long orderId) {
        Long userId = UserContext.getUserId();
        orderService.pay(orderId, userId);
        return Result.success();
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderId}")
    public Result<Void> cancel(@PathVariable Long orderId) {
        Long userId = UserContext.getUserId();
        orderService.cancel(orderId, userId);
        return Result.success();
    }

    /**
     * 查看订单详情
     */
    @GetMapping("/{orderId}")
    public Result<OrderVO> viewDetails(@PathVariable Long orderId) {
        Long userId = UserContext.getUserId();
        OrderVO vo = orderService.viewDetails(orderId, userId);
        return Result.success(vo);
    }

    @GetMapping("/list")
    public Result<List<OrderVO>> list() {
        Long userId = UserContext.getUserId();
        return Result.success(orderService.listMyOrders(userId));
    }
}