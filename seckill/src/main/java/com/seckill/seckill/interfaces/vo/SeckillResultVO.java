package com.seckill.seckill.interfaces.vo;

import lombok.Getter;

@Getter
public class SeckillResultVO {
    private Long orderId;     // 抢到后的订单号

    public static SeckillResultVO of(Long orderId) {
        SeckillResultVO vo = new SeckillResultVO();
        vo.orderId = orderId;
        return vo;
    }
}


