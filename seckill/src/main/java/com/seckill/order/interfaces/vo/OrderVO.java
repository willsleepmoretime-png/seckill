package com.seckill.order.interfaces.vo;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class OrderVO {
    private Long orderId;            // 订单号(就是 id)
    private Long goodsId;
    private Long seckillId;          // 非秒杀订单为 null
    private String goodsName;
    private BigDecimal goodsPrice;
    private Integer goodsCount;
    private BigDecimal orderAmount;
    private Integer status;          // 0待支付/1已支付/2已取消
    private String statusDesc;       // 状态中文描述,给前端直接显示
    private LocalDateTime payTime;
    private LocalDateTime createTime;
}