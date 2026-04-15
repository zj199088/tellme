package com.fitness.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.app.entity.User;

public interface UserMapper extends BaseMapper<User> {
    User selectByOpenId(String openId);
    User selectByUsername(String username);
}