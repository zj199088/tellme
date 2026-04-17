package com.fitness.app.controller;

import com.fitness.app.service.AppConfigService;
import com.fitness.app.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/config")
public class ConfigController {
    
    private final AppConfigService appConfigService;
    
    @Autowired
    public ConfigController(AppConfigService appConfigService) {
        this.appConfigService = appConfigService;
    }
    
    @GetMapping
    public Result<Map<String, Object>> getAppConfig() {
        Map<String, Object> configMap = new HashMap<>();
        
        // 获取所有配置项
        configMap.put("app_name", appConfigService.getConfigValue("app_name"));
        configMap.put("version", appConfigService.getConfigValue("version"));
        configMap.put("maintenance_mode", appConfigService.getBooleanConfig("maintenance_mode"));
        configMap.put("max_workout_records_p", appConfigService.getNumberConfig("max_workout_records_p"));
        configMap.put("default_plan_duration", appConfigService.getNumberConfig("default_plan_duration"));
        configMap.put("ai_enabled", appConfigService.getBooleanConfig("ai_enabled"));
        configMap.put("music_enabled", appConfigService.getBooleanConfig("music_enabled"));
        configMap.put("social_share_enabled", appConfigService.getBooleanConfig("social_share_enabled"));
        configMap.put("max_file_size_mb", appConfigService.getNumberConfig("max_file_size_mb"));
        configMap.put("workout_reminder_enable", appConfigService.getBooleanConfig("workout_reminder_enable"));
        
        return Result.success(configMap);
    }
}
