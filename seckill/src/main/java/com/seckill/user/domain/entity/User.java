package com.seckill.user.domain.entity;


import com.seckill.common.entity.BaseEntity;
import com.seckill.user.domain.constant.UserStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
public class User extends BaseEntity {

    private String phone;
    private String password;
    private String salt;

    @Setter
    private String nickname;
    @Setter
    private String avatar;

    private UserStatusEnum status;

    public User(String phone, String password, String salt,
                String nickname, String avatar, UserStatusEnum status) {
        this.phone = phone;
        this.password = password;
        this.salt = salt;
        this.nickname = nickname;
        this.avatar = avatar;
        this.status = status;
    }

    public void changePassword(String oldRawPassword, String newRawPassword) {
        throw new UnsupportedOperationException("M3 实现");
    }
}
