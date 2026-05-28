package com.seckill.user.domain.repository;

import com.seckill.user.domain.entity.User;


import java.util.Optional;


public interface UserRepository {

    Optional<User> findByPhone(String phone);
    void save(User user);
}
