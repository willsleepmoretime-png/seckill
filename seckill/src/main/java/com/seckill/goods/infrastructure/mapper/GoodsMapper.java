package com.seckill.goods.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.goods.domain.entity.Goods;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {
    // BaseMapper 已经提供 insert/selectById/updateById/delete 等基础方法
    // 暂时不写自定义查询,够用
}