package com.seckill.infrastructure.config;


//把lua 加载成Bean


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

//注入lua
@Configuration
public class SeckillScriptConfig {

    @Bean
    public RedisScript<Long> seckillScript() {
        // 默认redis 脚本 lua是脚本
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/seckill_stock.lua")));
        //返回的是Long 是对的
        script.setResultType(Long.class);
        return script;
    }

    @Bean
    public RedisScript<Long> rollbackScript() {   // 新的独立 Bean
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/rollback_stock.lua")));
        script.setResultType(Long.class);
        return script;
    }
}