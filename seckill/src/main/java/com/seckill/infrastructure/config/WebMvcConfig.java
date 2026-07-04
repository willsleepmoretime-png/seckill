package com.seckill.infrastructure.config;

import com.seckill.infrastructure.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private  final AuthInterceptor authInterceptor;
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")                    // 只对 /api/ 开头的接口开放跨域
                .allowedOriginPatterns("*")               // 允许所有源(开发阶段方便)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)                   // 允许带 cookie
                .maxAge(3600);                            // 预检请求缓存 1 小时
    }

    @Override
    public  void  addInterceptors(InterceptorRegistry registry){

        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/register",
                        "/error",
                        "/actuator/**"
                );
    }
}