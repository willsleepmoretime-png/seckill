package com.seckill.goods.infrastructure.repository.GoodsRepositoryImpl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seckill.common.enums.SortOrder;
import com.seckill.goods.domain.constant.GoodsStatusEnum;
import com.seckill.goods.domain.entity.Goods;
import com.seckill.goods.domain.repository.GoodsRepository;
import com.seckill.goods.infrastructure.mapper.GoodsMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static io.lettuce.core.GeoArgs.Sort.asc;
import static io.lettuce.core.GeoArgs.Sort.desc;

@RequiredArgsConstructor
@Repository
public class GoodsRepositoryImpl implements GoodsRepository {

    private final GoodsMapper goodsMapper;



    @Override
    public Optional<Goods> findById(Long id){
        Goods goods=goodsMapper.selectById(id);
        return Optional.ofNullable(goods);
    }

    @Override
    public List<Goods> findAllOnSale(SortOrder opt) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<Goods>()
                .eq(Goods::getStatus, GoodsStatusEnum.ON_SALE)
                .orderBy(true,opt == SortOrder.DESC, Goods::getPrice);
        return goodsMapper.selectList(wrapper);
    }

    @Override
    public void save(Goods goods) {
        if (goods.getId() == null) {
            goodsMapper.insert(goods);     // 没 id → 新增
        } else {
            goodsMapper.updateById(goods); // 有 id → 更新
        }
    }
}
