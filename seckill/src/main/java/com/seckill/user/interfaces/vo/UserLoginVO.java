package com.seckill.user.interfaces.vo;

import com.seckill.user.domain.entity.User;

public class UserLoginVO {
    private Long id;
    private String phone;
    private String nickname;
    private String avatar;

    // 全字段构造器
    public UserLoginVO(Long id, String phone, String nickname, String avatar) {
        this.id = id;
        this.phone = phone;
        this.nickname = nickname;
        this.avatar = avatar;
    }

    public static UserLoginVO from(User user) {
        return new UserLoginVO(
                user.getId(),
                user.getPhone(),
                user.getNickname(),
                user.getAvatar()
        );
    }

    public Long getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatar() {
        return avatar;
    }
}