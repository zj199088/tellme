package com.fitness.app.config;

import com.fitness.app.service.AppConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class AppConfigLoader implements ApplicationListener<ApplicationReadyEvent> {
    
    private final AppConfigService appConfigService;
    
    @Autowired
    public AppConfigLoader(AppConfigService appConfigService) {
        this.appConfigService = appConfigService;
    }
    
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 应用启动时加载配置
        appConfigService.loadConfigs();
        System.out.println("App configs loaded successfully!");
    }
}
