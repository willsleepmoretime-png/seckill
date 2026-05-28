package com.seckill.goods.interfaces.controller;

import com.seckill.common.enums.SortOrder;
import com.seckill.common.result.Result;
import com.seckill.goods.application.service.GoodsService;
import com.seckill.goods.domain.entity.Goods;
import com.seckill.goods.interfaces.assembler.GoodsAssembler;
import com.seckill.goods.interfaces.vo.GoodsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    /**
     * 商品列表
     * GET /api/goods/list?order=asc
     * 默认升序
     */
    @GetMapping("/list")
    public Result<List<GoodsVO>> list(@RequestParam(defaultValue = "asc") String order) {
        SortOrder sortOrder = SortOrder.valueOf(order.toUpperCase());
        List<Goods> goodsList = goodsService.listOnSale(sortOrder);
        return Result.success(GoodsAssembler.toVOList(goodsList));
    }

    /**
     * 商品详情
     * GET /api/goods/{id}
     */
    @GetMapping("/{id}")
    public Result<GoodsVO> detail(@PathVariable Long id) {
        Goods goods = goodsService.getById(id);
        return Result.success(GoodsAssembler.toVO(goods));
    }
}