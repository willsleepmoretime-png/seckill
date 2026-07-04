package  com.seckill.goods.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.seckill.common.entity.BaseEntity;
import com.seckill.common.exception.BusinessException;
import com.seckill.common.result.ResultCode;
import com.seckill.goods.domain.constant.GoodsStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@TableName("goods")
public class Goods extends BaseEntity {

    // 基本信息字段,允许直接修改
    @Setter
    private String name;
    @Setter private String description;
    @Setter private String imageUrl;

    // 受控字段,必须通过业务方法修改
    private BigDecimal price;
    private Integer stock;
    private GoodsStatusEnum status;

    public static Goods create(String name, String description, String imageUrl,
                               BigDecimal price, Integer stock) {
        Goods goods = new Goods();
        goods.name = name;
        goods.description = description;
        goods.imageUrl = imageUrl;
        goods.price = price;
        goods.stock = stock;
        goods.status = GoodsStatusEnum.ON_SALE;
        return goods;
    }

    public void deductStock(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        if (stock < amount) {
            throw new BusinessException(ResultCode.STOCK_INSUFFICIENT);
        }
        this.stock -= amount;
    }

    public void putOnSale() {
        if (this.status == GoodsStatusEnum.ON_SALE) return;
        this.status = GoodsStatusEnum.ON_SALE;
    }

    public void takeOffShelf() {
        if (this.status == GoodsStatusEnum.OFF_SHELF) return;
        this.status = GoodsStatusEnum.OFF_SHELF;
    }
}