// interfaces/assembler/SeckillAssembler.java
package com.seckill.seckill.interfaces.assembler;

import com.seckill.seckill.domain.entity.Seckill;
import com.seckill.seckill.interfaces.vo.SeckillVO;

public class SeckillAssembler {

    public static SeckillVO toVO(Seckill seckill) {
        if (seckill == null) return null;
        SeckillVO vo = new SeckillVO();
        vo.setId(seckill.getId());
        vo.setGoodsId(seckill.getGoodsId());
        vo.setSeckillPrice(seckill.getSeckillPrice());
        vo.setStockCount(seckill.getStockCount());
        vo.setStartTime(seckill.getStartTime());
        vo.setEndTime(seckill.getEndTime());
        vo.setStatus(seckill.getStatus().getCode());
        vo.setStatusDesc(seckill.getStatus().getDesc());
        return vo;
    }
}