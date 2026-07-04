package com.seckill.user.interfaces.vo;

import com.seckill.user.domain.entity.User;

import java.math.BigDecimal;

public class UserLoginVO {
    private Long id;
    private String phone;
    private String nickname;
    private String avatar;
    private String token;
    private BigDecimal balance;
    // 全字段构造器
    public UserLoginVO(Long id, String phone, String nickname,
                       String avatar,String token ,BigDecimal balance) {
        this.id = id;
        this.phone = phone;
        this.nickname = nickname;
        this.avatar = avatar;
        this.token=token;
        this.balance=balance;
    }

    public static UserLoginVO from(User user,String token) {
        return new UserLoginVO(
                user.getId(),
                user.getPhone(),
                user.getNickname(),
                user.getAvatar(),
                token,
                user.getBalance()
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

    public String getToken() { return token; }

    public BigDecimal getBalance(){return balance;}
}