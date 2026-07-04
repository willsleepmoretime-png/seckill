package com.seckill.infrastructure.Cache;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

//缓存一般是使用然后保存
@Component
public class LocalCacheTemplate {

    //supplier 以后在执行的方法
    public <K, V> V get(
            Cache<K, V> cache,
            K key,
            Supplier<V> loader
    ){
        //cache.get(key, mappingFunction) 如果没有，就执行 mappingFunction，然后把结果放进缓存，再返回。
        return cache.get(key,k->loader.get());
    }
}
