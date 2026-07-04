package com.seckill.infrastructure.Cache;

//清理“本地缓存（Local Cache）”的标准规范
public interface MultiLevelCacheLocalInvalidator {

    void invalidateLocal(String key);
}

