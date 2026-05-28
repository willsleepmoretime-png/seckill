package com.seckill.user.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seckill.user.domain.entity.User;
import com.seckill.user.domain.repository.UserRepository;
import com.seckill.user.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public Optional<User>findByPhone(String phone){
        LambdaQueryWrapper<User> wrapper=new LambdaQueryWrapper<User>()
                .eq(User::getPhone,phone);
        User user=userMapper.selectOne(wrapper);
        return Optional.ofNullable(user);
    }

    //后续还有更新需要做
    @Override
    public void save(User user){
        //要写回数据库中
        userMapper.insert(user);
    }
}
