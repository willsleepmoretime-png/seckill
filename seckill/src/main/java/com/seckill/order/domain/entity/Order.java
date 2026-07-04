package com.seckill.order.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.seckill.common.entity.BaseEntity;
import com.seckill.common.exception.BusinessException;
import com.seckill.common.result.ResultCode;
import com.seckill.order.domain.constant.OrderStatusEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Getter
@NoArgsConstructor
@TableName("order_info")
public class Order extends BaseEntity {
    private Long userId;
    private Long goodsId;
    private Long seckillId;          // 可空,非秒杀订单为 null
    private String goodsName;
    private BigDecimal goodsPrice;
    private Integer goodsCount;
    private BigDecimal orderAmount;
    private OrderStatusEnum status;
    private LocalDateTime payTime;
    @Version
    private Integer version;

    public static Order create(Long userId, Long goodsId, Long seckillId,
                               String goodsName, BigDecimal goodsPrice, Integer goodsCount) {
        if (userId == null || goodsId == null || goodsPrice == null || goodsCount == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        Order order = new Order();
        order.version=0;
        order.userId = userId;
        order.goodsId = goodsId;
        order.seckillId = seckillId;
        order.goodsName = goodsName;
        order.goodsPrice = goodsPrice;
        order.goodsCount = goodsCount;
        order.orderAmount = goodsPrice.multiply(new BigDecimal(goodsCount));  // 金额算出来
        order.status = OrderStatusEnum.NOT_PAYMENT;   // 初始化为待支付
        return order;
    }

    // OrderInfo.entity —— 只管自己的状态
    public void pay() {
        if (this.status != OrderStatusEnum.NOT_PAYMENT) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        this.status = OrderStatusEnum.HAD_PAID;
        this.payTime = LocalDateTime.now();
    }

    public void cancel(){
        if (this.status != OrderStatusEnum.NOT_PAYMENT) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        this.status = OrderStatusEnum.CANCEL;
    }
}
