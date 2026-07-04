package com.seckill.infrastructure.interceptor;

import com.seckill.common.context.UserContext;
import com.seckill.common.exception.BusinessException;
import com.seckill.common.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private static final String TOKEN_KEY_PREFIX = "token:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // 1. 从 header 取 token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        // 2. 查 Redis 找 userId
        Object userIdObj = redisTemplate.opsForValue().get(TOKEN_KEY_PREFIX + token);
        if (userIdObj == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        // 3. 塞 UserContext
        UserContext.setUserId(Long.valueOf(userIdObj.toString()));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        // 务必清,Tomcat 线程复用!
        UserContext.clear();
    }
}