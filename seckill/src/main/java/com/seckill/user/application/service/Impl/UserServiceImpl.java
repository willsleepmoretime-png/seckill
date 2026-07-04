package com.seckill.user.application.service.Impl;

import com.seckill.common.exception.BusinessException;
import com.seckill.common.result.ResultCode;

import com.seckill.common.util.PasswordUtil;

import com.seckill.common.util.TokenUtil;
import com.seckill.user.application.service.UserService;
import com.seckill.user.domain.constant.UserStatusEnum;
import com.seckill.user.domain.entity.User;
import com.seckill.user.domain.repository.UserRepository;
import com.seckill.user.infrastructure.mapper.UserMapper;
import com.seckill.user.interfaces.vo.UserLoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final String TOKEN_KEY_PREFIX="token:";
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final int  Token_EXPIRE_MINUTES=30;

    /**
     * 用户注册
     * 流程:
     * 1. 检查手机号是否已注册 → 已注册抛 PHONE_ALREADY_REGISTERED
     * 2. 创建新 User(M1 简化:salt = "", nickname = "新用户", avatar = null, status = NORMAL)
     * 3. 调 userRepository.save 持久化
     */
    @Override
    public void register(String phone, String password) {
        // 1. 查重:已存在则抛异常
        userRepository.findByPhone(phone).ifPresent(u -> {
            throw new BusinessException(ResultCode.PHONE_ALREADY_REGISTERED);
        });
        // 2. 创建新用户
        String encryptedPassword= PasswordUtil.encode(password);
        User user= User.create(phone, encryptedPassword,null, "新用户", null, UserStatusEnum.NORMAL);

        // 3. 持久化
        userRepository.save(user);
    }

    /**
     * 用户登录
     * 流程:
     * 1. 按手机号查用户 → 查不到抛 USER_NOT_FOUND
     * 2. 校验密码 → 不匹配抛 PASSWORD_INCORRECT
     * 3. 检查用户状态 → 禁用抛 USER_DISABLED
     * 4. 返回 User
     */
    @Override
    public UserLoginVO login(String phone, String password) {
        User user =userRepository.findByPhone(phone)
                .orElseThrow(()-> new BusinessException(ResultCode.USER_NOT_FOUND));
        if(!PasswordUtil.matches(password,user.getPassword())){
            throw new BusinessException(ResultCode.PASSWORD_INCORRECT);
        }
        if(user.getStatus()!=UserStatusEnum.NORMAL){
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        //写redis
        String token= TokenUtil.generate();
        redisTemplate.opsForValue().set(
                TOKEN_KEY_PREFIX+token,
                user.getId().toString(),
                Token_EXPIRE_MINUTES,
                TimeUnit.MINUTES
        );

        return UserLoginVO.from(user,token);
    }

    @Override
    public  Boolean recharge(Long userId, BigDecimal amount){
        if (amount == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        boolean success = userRepository.addBalance(userId, amount);
        if (!success) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return true;
    }
}