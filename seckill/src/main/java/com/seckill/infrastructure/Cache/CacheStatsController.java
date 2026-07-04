package com.seckill.infrastructure.Cache;

import com.seckill.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheStatsController {

    private final CacheStatsService cacheStatsService;

    @GetMapping("/stats")
    public Result<CacheStatsVO> stats() {
        return Result.success(cacheStatsService.currentStats());
    }
}
