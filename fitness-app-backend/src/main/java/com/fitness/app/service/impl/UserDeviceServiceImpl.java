package com.fitness.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.app.entity.UserDevice;
import com.fitness.app.mapper.UserDeviceMapper;
import com.fitness.app.service.UserDeviceService;
import org.springframework.stereotype.Service;

@Service
public class UserDeviceServiceImpl extends ServiceImpl<UserDeviceMapper, UserDevice> implements UserDeviceService {
}