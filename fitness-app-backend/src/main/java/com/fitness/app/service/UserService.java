package com.fitness.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.app.entity.User;

public interface UserService extends IService<User> {
    User findByOpenId(String openId);
    User findByUsername(String username);
    User createOrUpdateWechatUser(User user);
    User createAdminUser(User user);
}