package com.fitness.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.app.entity.AppConfig;
import com.fitness.app.mapper.AppConfigMapper;
import com.fitness.app.service.AppConfigService;
import org.springframework.stereotype.Service;

@Service
public class AppConfigServiceImpl extends ServiceImpl<AppConfigMapper, AppConfig> implements AppConfigService {
}