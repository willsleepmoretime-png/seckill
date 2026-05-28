package com.seckill.goods.domain.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum GoodsStatusEnum {
    ON_SALE(1, "在售"),
    OFF_SHELF(0, "下架");

    @EnumValue
    private  final int code;

    private  final String msg;
    GoodsStatusEnum(int code,String msg){
        this.code=code;
        this.msg=msg;
    }

    public int value(){
        return code;
    }

    public String getMsg(){
        return msg;
    }

    public static GoodsStatusEnum fromCode(Integer code){
        if(code==null){
            throw new IllegalArgumentException("code 不存在");
        }

        for(GoodsStatusEnum status:GoodsStatusEnum.values()){
            if(code.equals(status.value())){
                return status;
            }
        }
        throw  new IllegalArgumentException("未知 code: " + code);
    }
}
