package com.seckill.seckill.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seckill.common.entity.BaseEntity;
import com.seckill.common.exception.BusinessException;
import com.seckill.common.result.ResultCode;
import com.seckill.seckill.domain.constant.SeckillStatusEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("seckill")
public class Seckill extends BaseEntity {

    private Long goodsId;

    private BigDecimal seckillPrice;

    private Integer stockCount;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private SeckillStatusEnum status;

    @Version
    private Integer version;


    //聚合根的创建必须使用工厂
    public static Seckill create(Long goodsId, BigDecimal seckillPrice,
                                 Integer stockCount,
                                 LocalDateTime startTime,
                                 LocalDateTime endTime) {
        // 1. 入参校验
        if (goodsId == null || seckillPrice == null || stockCount == null
                || startTime == null || endTime == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        if (stockCount <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        if (!startTime.isBefore(endTime)) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }

        // 2. 创建对象 + 填字段(类内部能直接访问 private 字段,不需要 setter)
        Seckill seckill = new Seckill();
        seckill.goodsId = goodsId;
        seckill.seckillPrice = seckillPrice;
        seckill.stockCount = stockCount;
        seckill.startTime = startTime;
        seckill.endTime = endTime;
        seckill.status = SeckillStatusEnum.NOT_START;   // 默认值,调用方不用传
        return seckill;
    }

    public void start() {
        if(SeckillStatusEnum.IN_PROGRESS.equals(this.status))  return;

        if(SeckillStatusEnum.ENDED.equals(this.status))
            throw new BusinessException(ResultCode.SECKILL_STATUS_INVALID);
        this.status=SeckillStatusEnum.IN_PROGRESS;
    }


    public void end() {
        if(SeckillStatusEnum.ENDED.equals(this.status)) return;
        this.status=SeckillStatusEnum.ENDED;
    }

    public void deductStock() {
        if (stockCount == null || stockCount <= 0) {
            throw new BusinessException(ResultCode.SECKILL_SOLD_OUT);
        }
        this.stockCount -= 1;
    }

    //：Jackson 会把 isInProgress() 当成 inProgress 字段写进 Redis，读回来时实体没有这个字段，导致反序列化失败。
    @JsonIgnore
    public boolean isInProgress() {
        LocalDateTime now=LocalDateTime.now();
        return !now.isBefore(startTime)&&!now.isAfter(endTime);
     }
}
