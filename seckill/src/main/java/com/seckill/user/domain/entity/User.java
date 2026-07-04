package com.seckill.user.domain.entity;


import com.seckill.common.entity.BaseEntity;
import com.seckill.common.exception.BusinessException;
import com.seckill.common.result.ResultCode;
import com.seckill.user.domain.constant.UserStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
public class User extends BaseEntity {

    private String phone;
    private String password;
    private String salt;

    private BigDecimal balance;
    @Setter
    private String nickname;
    @Setter
    private String avatar;

    private UserStatusEnum status;

    public static User create(String phone, String encryptedPassword, String salt,
                              String nickname, String avatar, UserStatusEnum status) {
        // 1. 入参校验
        if (phone == null || encryptedPassword == null  || status == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }

        // 2. 创建对象 + 填字段(类内部可直接访问 private)
        User user = new User();
        user.phone = phone;
        user.password = encryptedPassword;
        user.salt = salt == null ? "" : salt;
        user.nickname = nickname;
        user.avatar = avatar;
        user.status = status;
        return user;
    }

    public void changePassword(String newEncryptedPassword) {
        if (newEncryptedPassword == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
        this.password = newEncryptedPassword;

    }
}
