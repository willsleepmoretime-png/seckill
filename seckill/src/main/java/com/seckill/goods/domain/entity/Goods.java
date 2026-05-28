package com.seckill.goods.domain.entity;

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

    @Setter
    private String name;

    @Setter
    private String description;

    @Setter
    private String imageUrl;
    private BigDecimal price;
    private int stock;
    private GoodsStatusEnum status;

    Goods(String name, String description, String imageUrl, BigDecimal price, int stock, GoodsStatusEnum status) {
        this.description = description;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.stock = stock;
        this.status = status;
    }

    public void changePrice(BigDecimal price) {

    }

    public void deductStock(Integer amount){
        if(amount==null||amount<0) {
            throw  new BusinessException(ResultCode.PARAM_ERROR);
        }
        if(stock<amount){
            throw new BusinessException(ResultCode.STOCK_INSUFFICIENT);
        }
        this.stock-=amount;
    }
    public void putOnSale() {
        if (this.status==GoodsStatusEnum.ON_SALE){
            return;
        }
        status=GoodsStatusEnum.ON_SALE;
    }
    public void takeOffShelf(){
        if (this.status==GoodsStatusEnum.OFF_SHELF){
            return;
        }
        status=GoodsStatusEnum.OFF_SHELF;
    }
}

