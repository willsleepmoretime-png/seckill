package com.seckill.user.domain.repository;

import com.seckill.user.domain.entity.User;


import java.math.BigDecimal;
import java.util.Optional;


public interface UserRepository {

    Optional<User> findByPhone(String phone);
    void save(User user);
    boolean deductBalance(Long userId, BigDecimal amount);
    boolean addBalance(Long userId, BigDecimal amount);
}
