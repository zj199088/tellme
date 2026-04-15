package com.fitness.app.controller;

import com.fitness.app.entity.FitnessPlan;
import com.fitness.app.service.FitnessPlanService;
import com.fitness.app.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    @Autowired
    private FitnessPlanService fitnessPlanService;

    @PostMapping("/template")
    public Result<?> createPlanFromTemplate(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        try {
            Integer userId = Integer.parseInt(authentication.getName());
            Integer templateId = (Integer) request.get("templateId");
            String name = (String) request.get("name");
            String goal = (String) request.get("goal");
            String difficulty = (String) request.get("difficulty");
            Integer durationWeeks = (Integer) request.get("durationWeeks");
            
            Integer planId = fitnessPlanService.createPlanFromTemplate(userId, templateId, name, goal, difficulty, durationWeeks);
            return Result.success(planId);
        } catch (Exception e) {
            return Result.error("创建计划失败: " + e.getMessage());
        }
    }

    @PostMapping("/custom")
    public Result<?> createCustomPlan(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        try {
            Integer userId = Integer.parseInt(authentication.getName());
            
            // 创建自定义计划
            FitnessPlan plan = new FitnessPlan();
            plan.setUser_id(userId);
            plan.setName((String) request.get("name"));
            plan.setType("custom");
            plan.setGoal((String) request.get("goal"));
            plan.setDifficulty((String) request.get("difficulty"));
            plan.setDuration_weeks((Integer) request.get("durationWeeks"));
            plan.setStart_date(LocalDate.now());
            plan.setEnd_date(LocalDate.now().plusWeeks((Integer) request.get("durationWeeks")));
            plan.setStatus("active");
            plan.setIs_shared(0);
            plan.setIs_deleted(0);
            plan.setCreated_at(LocalDateTime.now());
            plan.setUpdated_at(LocalDateTime.now());
            
            fitnessPlanService.save(plan);
            return Result.success(plan.getId());
        } catch (Exception e) {
            return Result.error("创建自定义计划失败: " + e.getMessage());
        }
    }

    @PostMapping("/share/{planId}")
    public Result<?> sharePlan(
            @PathVariable Integer planId,
            Authentication authentication) {
        try {
            Integer userId = Integer.parseInt(authentication.getName());
            
            // 实现计划分享逻辑
            // 这里可以创建一个模板并设置为公开
            
            return Result.success("计划分享成功");
        } catch (Exception e) {
            return Result.error("分享计划失败: " + e.getMessage());
        }
    }

    @PutMapping("/status/{planId}")
    public Result<?> updatePlanStatus(
            @PathVariable Integer planId,
            @RequestParam String status,
            Authentication authentication) {
        try {
            Integer userId = Integer.parseInt(authentication.getName());
            
            FitnessPlan plan = fitnessPlanService.getById(planId);
            if (plan == null || !plan.getUser_id().equals(userId)) {
                return Result.error("计划不存在或无权限");
            }
            
            plan.setStatus(status);
            plan.setUpdated_at(LocalDateTime.now());
            fitnessPlanService.updateById(plan);
            
            return Result.success("计划状态更新成功");
        } catch (Exception e) {
            return Result.error("更新计划状态失败: " + e.getMessage());
        }
    }

    @GetMapping("/user")
    public Result<?> getUserPlans(Authentication authentication) {
        try {
            Integer userId = Integer.parseInt(authentication.getName());
            
            // 这里可以添加获取用户计划的逻辑
            // List<FitnessPlan> plans = fitnessPlanService.getUserPlans(userId);
            
            return Result.success("获取用户计划成功");
        } catch (Exception e) {
            return Result.error("获取用户计划失败: " + e.getMessage());
        }
    }
}
