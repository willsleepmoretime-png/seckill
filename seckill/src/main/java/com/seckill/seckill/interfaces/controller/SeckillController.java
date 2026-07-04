package com.seckill.seckill.interfaces.controller;

import com.seckill.common.result.Result;
import com.seckill.common.result.ResultCode;
import com.seckill.seckill.application.service.SeckillService;
import com.seckill.seckill.application.service.StockService;
import com.seckill.seckill.interfaces.dto.SeckillCreateDTO;
import com.seckill.seckill.interfaces.vo.SeckillVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/seckill")
@RequiredArgsConstructor
public class SeckillController {

    private final SeckillService seckillService;
    private final StockService stockService;

    /**
     * 创建秒杀活动
     */
    @PostMapping
    public Result<SeckillVO> createSeckill(@Valid @RequestBody SeckillCreateDTO dto) {
        return Result.success(seckillService.createSeckill(dto));
    }

    /**
     * 查询单个活动详情
     */
    @GetMapping("/{id}")
    public Result<SeckillVO> getSeckill(@PathVariable("id") Long seckillId) {
        return Result.success(seckillService.getSeckillInfo(seckillId));
    }

    /**
     * 列出所有进行中的活动
     */
    @GetMapping("/list/in-progress")
    public Result<List<SeckillVO>> listInProgress() {
        return Result.success(seckillService.listInProgress());
    }

    @PostMapping("/{id}/preheat")
    public Result<Void> preheat(@PathVariable Long id) {
        stockService.preheatStock(id);
        return Result.success();
    }

    @PostMapping("/{id}/doSeckill")
    public Result<ResultCode> doSeckill(@PathVariable("id") Long seckillId) {
        seckillService.doSeckill(seckillId);
        return Result.success(ResultCode.SUCCESS_SUBMIT_ORDER);
    }
}
