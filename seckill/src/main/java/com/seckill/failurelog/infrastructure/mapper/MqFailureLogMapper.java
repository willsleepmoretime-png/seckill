package com.seckill.failurelog.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.failurelog.domain.entity.MqFailureLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MqFailureLogMapper extends BaseMapper<MqFailureLog> {
}
