package com.seckill.seckill.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seckill.seckill.domain.constant.SeckillStatusEnum;
import com.seckill.seckill.domain.entity.Seckill;
import com.seckill.seckill.domain.repository.SeckillRepository;
import com.seckill.seckill.infrastructure.mapper.SeckillMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SeckillRepositoryImpl implements SeckillRepository {


    private final SeckillMapper seckillMapper;

    @Override
    public void save(Seckill seckill) {
        seckillMapper.insert(seckill);
    }

    @Override
    public Seckill findById(Long id) {
        return seckillMapper.selectById(id);
    }

    @Override
    public List<Seckill> findAll() {
        return seckillMapper.selectList(null);
    }

    @Override
    public List<Seckill> findInProgressList() {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<Seckill> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(Seckill::getStartTime, now)
                .ge(Seckill::getEndTime, now);
        return seckillMapper.selectList(wrapper);
    }

    @Override
    public void updateById(Seckill seckill) {
        seckillMapper.updateById(seckill);
    }

    @Override
    public List<Seckill> findByStatus(SeckillStatusEnum status) {
        LambdaQueryWrapper<Seckill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Seckill::getStatus, status);
        return seckillMapper.selectList(wrapper);
    }
}