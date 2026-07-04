package com.seckill.seckill.domain.repository;

import com.seckill.seckill.domain.constant.SeckillStatusEnum;
import com.seckill.seckill.domain.entity.Seckill;

import java.util.List;

public interface SeckillRepository {

    void save(Seckill seckill);

    Seckill findById(Long id);

    List<Seckill> findAll();

    /**
     * 查询所有进行中的活动(预热用)
     * WHERE start_time <= now AND end_time >= now
     */
    List<Seckill> findInProgressList();

    void updateById(Seckill seckill);

    List<Seckill> findByStatus(SeckillStatusEnum status);
}