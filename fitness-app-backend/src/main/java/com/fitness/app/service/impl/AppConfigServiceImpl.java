package com.fitness.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.app.entity.AppConfig;
import com.fitness.app.mapper.AppConfigMapper;
import com.fitness.app.service.AppConfigService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppConfigServiceImpl extends ServiceImpl<AppConfigMapper, AppConfig> implements AppConfigService {
    
    // 配置缓存
    private static final Map<String, String> configCache = new HashMap<>();
    
    @Override
    public String getConfigValue(String configKey) {
        // 如果缓存中没有，加载所有配置
        if (configCache.isEmpty()) {
            loadConfigs();
        }
        return configCache.getOrDefault(configKey, null);
    }
    
    @Override
    public boolean getBooleanConfig(String configKey) {
        String value = getConfigValue(configKey);
        return value != null && Boolean.parseBoolean(value);
    }
    
    @Override
    public int getNumberConfig(String configKey) {
        String value = getConfigValue(configKey);
        try {
            return value != null ? Integer.parseInt(value) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    @Override
    public void loadConfigs() {
        // 清空缓存
        configCache.clear();
        
        // 查询所有激活的配置
        List<AppConfig> configs = list(new LambdaQueryWrapper<AppConfig>()
                .eq(AppConfig::getIsActive, 1)
                .eq(AppConfig::getIsDeleted, 0));
        
        // 加载到缓存
        for (AppConfig config : configs) {
            configCache.put(config.getConfigKey(), config.getConfigValue());
        }
    }
}