package com.fitness.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.app.entity.AppConfig;

public interface AppConfigService extends IService<AppConfig> {
    /**
     * 根据配置键获取配置值
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String configKey);
    
    /**
     * 根据配置键获取布尔类型配置值
     * @param configKey 配置键
     * @return 布尔值
     */
    boolean getBooleanConfig(String configKey);
    
    /**
     * 根据配置键获取数字类型配置值
     * @param configKey 配置键
     * @return 数字值
     */
    int getNumberConfig(String configKey);
    
    /**
     * 加载所有激活的配置
     */
    void loadConfigs();
}