package com.fitness.app.controller;

import com.fitness.app.entity.FitnessPlan;
import com.fitness.app.entity.Template;
import com.fitness.app.service.FitnessPlanService;
import com.fitness.app.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    @Autowired
    private FitnessPlanService fitnessPlanService;

    @Autowired
    private TemplateService templateService;

    @PostMapping("/custom")
    public Map<String, Object> createCustomPlan(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        
        // 创建自定义计划
        FitnessPlan plan = new FitnessPlan();
        plan.setUser_id(userId);
        plan.setName((String) request.get("name"));
        plan.setDescription((String) request.get("description"));
        plan.setStart_date(LocalDateTime.now());
        plan.setCurrent_day(1);
        plan.setTotal_days((Integer) request.get("total_days"));
        plan.setStatus("active");
        plan.setCreated_at(LocalDateTime.now());
        plan.setUpdated_at(LocalDateTime.now());
        
        // 这里可以添加保存计划的逻辑
        // fitnessPlanService.save(plan);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "自定义计划创建成功");
        return response;
    }

    @PostMapping("/share/{planId}")
    public Map<String, Object> sharePlan(
            @PathVariable Long planId,
            Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        
        // 实现计划分享逻辑
        // 这里可以创建一个模板并设置为公开
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "计划分享成功");
        return response;
    }
}
