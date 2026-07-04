package com.seckill.seckill.interfaces.dto;

import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.seckill.seckill.domain.entity.Seckill;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class SeckillCreateDTO {
    @NotNull(message = "商品ID不能为空")
    private Long goodsId;

    @Positive
    private BigDecimal seckillPrice;

    @Positive(message = "库存必须大于0")
    private Integer  stockCount;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    public Seckill toEntity() {
        return Seckill.create(
                this.goodsId,
                this.seckillPrice,
                this.stockCount,
                this.startTime,
                this.endTime
        );
    }
}
