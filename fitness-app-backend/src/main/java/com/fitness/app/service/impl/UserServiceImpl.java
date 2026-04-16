package com.fitness.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.app.entity.User;
import com.fitness.app.mapper.UserMapper;
import com.fitness.app.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public User findByOpenId(String openId) {
        return userMapper.selectByOpenId(openId);
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User createOrUpdateWechatUser(User user) {
        User existingUser = findByOpenId(user.getOpenId());
        if (existingUser != null) {
            // 更新用户信息
            existingUser.setNickname(user.getNickname());
            existingUser.setAvatarUrl(user.getAvatarUrl());
            existingUser.setGender(user.getGender());
            existingUser.setCountry(user.getCountry());
            existingUser.setProvince(user.getProvince());
            existingUser.setCity(user.getCity());
            updateById(existingUser);
            return existingUser;
        } else {
            // 创建新用户
            user.setRole("user");
            user.setIsDeleted(0);
            save(user);
            return user;
        }
    }

    @Override
    public User createAdminUser(User user) {
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsDeleted(0);
        save(user);
        return user;
    }

    @Override
    public User registerUser(String username, String password, String nickname) {
        // 检查用户名是否已存在
        User existingUser = findByUsername(username);
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        user.setRole("user");
        user.setIsDeleted(0);
        save(user);
        return user;
    }
}