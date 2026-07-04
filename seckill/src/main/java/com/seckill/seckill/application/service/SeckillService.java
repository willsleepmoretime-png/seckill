package com.seckill.seckill.application.service;


import com.seckill.infrastructure.message.SeckillMessage;
import com.seckill.order.interfaces.vo.OrderVO;
import com.seckill.seckill.domain.entity.Seckill;
import com.seckill.seckill.interfaces.dto.SeckillCreateDTO;
import com.seckill.seckill.interfaces.vo.SeckillVO;

import java.util.List;

public interface  SeckillService {

    SeckillVO createSeckill(SeckillCreateDTO dto);

    /**
     * 查询单个秒杀活动详情
     */
    SeckillVO getSeckillInfo(Long seckillId);

    /**
     * 列出所有进行中的活动
     * 判断依据:status 字段或时间区间(待讨论)
     */
    List<SeckillVO> listInProgress();
    void doSeckill(Long seckillId);
    void handleSeckillOrder(SeckillMessage msg);
    void cancelIfUnpaid(SeckillMessage msg);



}
