package com.seckill.seckill.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.seckill.domain.entity.Seckill;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SeckillMapper extends BaseMapper<Seckill> {
}