package com.seckill.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/api/**")                    // 只对 /api/ 开头的接口开放跨域
                .allowedOriginPatterns("*")               // 允许所有源(开发阶段方便)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)                   // 允许带 cookie
                .maxAge(3600);                            // 预检请求缓存 1 小时
    }
}