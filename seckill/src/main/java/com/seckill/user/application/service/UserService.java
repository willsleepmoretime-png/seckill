package com.seckill.user.application.service;


import com.seckill.user.interfaces.vo.UserLoginVO;

import java.math.BigDecimal;

public interface UserService {


    /**
     * 用户注册
     * 流程:
     * 1. 检查手机号是否已注册 → 已注册抛 PHONE_ALREADY_REGISTERED
     * 2. 创建新 User(M1 简化:salt = "", nickname = "新用户", avatar = null, status = NORMAL)
     * 3. 调 userRepository.save 持久化
     */
    void register(String phone, String password);

    /**
     * 用户登录
     * 流程:
     * 1. 按手机号查用户 → 查不到抛 USER_NOT_FOUND
     * 2. 校验密码 → 不匹配抛 PASSWORD_INCORRECT
     * 3. 检查用户状态 → 禁用抛 USER_DISABLED
     * 4. 返回 User
     */
     UserLoginVO login(String phone, String password);

     Boolean recharge(Long userId, BigDecimal amount);
}