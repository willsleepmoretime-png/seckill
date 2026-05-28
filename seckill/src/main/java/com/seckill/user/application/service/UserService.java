package com.seckill.user.application.service;

import com.seckill.common.exception.BusinessException;
import com.seckill.common.result.ResultCode;
import com.seckill.user.domain.constant.UserStatusEnum;
import com.seckill.user.domain.entity.User;
import com.seckill.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 用户注册
     * 流程:
     * 1. 检查手机号是否已注册 → 已注册抛 PHONE_ALREADY_REGISTERED
     * 2. 创建新 User(M1 简化:salt = "", nickname = "新用户", avatar = null, status = NORMAL)
     * 3. 调 userRepository.save 持久化
     */
    public void register(String phone, String password) {
        // 1. 查重:已存在则抛异常
        Optional<User> opt = userRepository.findByPhone(phone);
        if (opt.isPresent()) {
            throw new BusinessException(ResultCode.PHONE_ALREADY_REGISTERED);
        }

        // 2. 创建新用户
        User user = new User(phone, password, "", "新用户", null, UserStatusEnum.NORMAL);

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
    public User login(String phone, String password) {
        User user =userRepository.findByPhone(phone).orElseThrow(()-> new BusinessException(ResultCode.USER_NOT_FOUND));
        if(!password.equals(user.getPassword())){
            throw new BusinessException(ResultCode.PASSWORD_INCORRECT);
        }
        if(user.getStatus()!=UserStatusEnum.NORMAL){
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        return user;
    }
}