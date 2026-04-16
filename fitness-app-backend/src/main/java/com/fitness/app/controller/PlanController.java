package com.fitness.app.controller;

import com.fitness.app.entity.FitnessPlan;
import com.fitness.app.entity.Template;
import com.fitness.app.entity.TemplateDay;
import com.fitness.app.entity.TemplateExercise;
import com.fitness.app.service.FitnessPlanService;
import com.fitness.app.service.TemplateService;
import com.fitness.app.service.TemplateDayService;
import com.fitness.app.service.TemplateExerciseService;
import com.fitness.app.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/plans")
public class PlanController {

    @Autowired
    private FitnessPlanService fitnessPlanService;
    
    @Autowired
    private TemplateService templateService;
    
    @Autowired
    private TemplateDayService templateDayService;
    
    @Autowired
    private TemplateExerciseService templateExerciseService;

    @PostMapping("/template")
    public Result<?> createPlanFromTemplate(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        Integer userId = Integer.parseInt(authentication.getName());
        Integer templateId = (Integer) request.get("templateId");
        log.info("从模板创建计划: userId={}, templateId={}", userId, templateId);
        
        String name = (String) request.get("name");
        String goal = (String) request.get("goal");
        String difficulty = (String) request.get("difficulty");
        Integer durationWeeks = (Integer) request.get("durationWeeks");
        
        Integer planId = fitnessPlanService.createPlanFromTemplate(userId, templateId, name, goal, difficulty, durationWeeks);
        log.info("从模板创建计划成功: planId={}", planId);
        return Result.success(planId);
    }

    @PostMapping("/custom")
    public Result<?> createCustomPlan(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        Integer userId = Integer.parseInt(authentication.getName());
        log.info("创建自定义计划: userId={}", userId);
        
        // 创建模板
        Template template = new Template();
        template.setName((String) request.get("name"));
        template.setDescription((String) request.get("goal") + "计划");
        template.setDifficulty((String) request.get("difficulty"));
        template.setIsPublic(0); // 默认为私有
        template.setCreatedBy(userId);
        template.setIsDeleted(0);
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        
        templateService.save(template);
        
        // 创建模板训练日和动作
        if (request.containsKey("days")) {
            List<Map<String, Object>> days = (List<Map<String, Object>>) request.get("days");
            for (Map<String, Object> dayData : days) {
                // 创建模板训练日
                TemplateDay templateDay = new TemplateDay();
                templateDay.setTemplateId(template.getId());
                templateDay.setDayOfWeek((Integer) dayData.get("dayOfWeek"));
                templateDay.setIsRestDay((Integer) dayData.get("isRestDay"));
                templateDay.setRestNote((String) dayData.get("restNote"));
                templateDay.setEstimatedDuration(30); // 默认30分钟
                templateDay.setIsDeleted(0);
                templateDay.setCreatedAt(LocalDateTime.now());
                templateDay.setUpdatedAt(LocalDateTime.now());
                
                templateDayService.save(templateDay);
                
                // 创建模板动作
                if (dayData.containsKey("exercises")) {
                    List<Map<String, Object>> exercises = (List<Map<String, Object>>) dayData.get("exercises");
                    int sortOrder = 1;
                    for (Map<String, Object> exerciseData : exercises) {
                        TemplateExercise templateExercise = new TemplateExercise();
                        templateExercise.setTemplateDayId(templateDay.getId());
                        templateExercise.setExerciseId((Integer) exerciseData.get("exerciseId"));
                        templateExercise.setExerciseName((String) exerciseData.get("exerciseName"));
                        templateExercise.setSets((Integer) exerciseData.get("sets"));
                        templateExercise.setReps((String) exerciseData.get("reps"));
                        templateExercise.setSortOrder(sortOrder++);
                        templateExercise.setIsDeleted(0);
                        templateExercise.setCreatedAt(LocalDateTime.now());
                        templateExercise.setUpdatedAt(LocalDateTime.now());
                        
                        templateExerciseService.save(templateExercise);
                    }
                }
            }
        }
        
        // 创建自定义计划
        FitnessPlan plan = new FitnessPlan();
        plan.setUser_id(userId);
        plan.setTemplate_id(template.getId());
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
        log.info("创建自定义计划成功: planId={}", plan.getId());
        return Result.success(plan.getId());
    }

    @PostMapping("/share/{planId}")
    public Result<?> sharePlan(
            @PathVariable Integer planId,
            Authentication authentication) {
        Integer userId = Integer.parseInt(authentication.getName());
        log.info("分享计划: planId={}, userId={}", planId, userId);
        
        // 获取计划
        FitnessPlan plan = fitnessPlanService.getById(planId);
        if (plan == null || !plan.getUser_id().equals(userId)) {
            return Result.error("计划不存在或无权限");
        }
        
        // 获取模板
        if (plan.getTemplate_id() == null) {
            return Result.error("计划未关联模板");
        }
        
        Template template = templateService.getById(plan.getTemplate_id());
        if (template == null) {
            return Result.error("模板不存在");
        }
        
        // 设置模板为公开
        template.setIsPublic(1);
        template.setUpdatedAt(LocalDateTime.now());
        templateService.updateById(template);
        
        // 更新计划的分享状态
        plan.setIs_shared(1);
        plan.setUpdated_at(LocalDateTime.now());
        fitnessPlanService.updateById(plan);
        
        log.info("分享计划成功: planId={}", planId);
        return Result.success("计划分享成功");
    }

    @PutMapping("/status/{planId}")
    public Result<?> updatePlanStatus(
            @PathVariable Integer planId,
            @RequestParam String status,
            Authentication authentication) {
        Integer userId = Integer.parseInt(authentication.getName());
        log.info("更新计划状态: planId={}, status={}, userId={}", planId, status, userId);
        
        FitnessPlan plan = fitnessPlanService.getById(planId);
        if (plan == null || !plan.getUser_id().equals(userId)) {
            return Result.error("计划不存在或无权限");
        }
        
        plan.setStatus(status);
        plan.setUpdated_at(LocalDateTime.now());
        fitnessPlanService.updateById(plan);
        
        log.info("更新计划状态成功: planId={}, status={}", planId, status);
        return Result.success("计划状态更新成功");
    }

    @GetMapping("/user")
    public Result<?> getUserPlans(Authentication authentication) {
        Integer userId = Integer.parseInt(authentication.getName());
        log.info("获取用户计划列表: userId={}", userId);
        
        List<FitnessPlan> plans = fitnessPlanService.lambdaQuery()
                .eq(FitnessPlan::getUser_id, userId)
                .eq(FitnessPlan::getIs_deleted, 0)
                .list();
        
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (FitnessPlan plan : plans) {
            Map<String, Object> planMap = new HashMap<>();
            planMap.put("id", plan.getId());
            planMap.put("userId", plan.getUser_id());
            planMap.put("templateId", plan.getTemplate_id());
            planMap.put("name", plan.getName());
            planMap.put("type", plan.getType());
            planMap.put("goal", plan.getGoal());
            planMap.put("difficulty", plan.getDifficulty());
            planMap.put("durationWeeks", plan.getDuration_weeks());
            planMap.put("startDate", plan.getStart_date() != null ? plan.getStart_date().toString() : null);
            planMap.put("endDate", plan.getEnd_date() != null ? plan.getEnd_date().toString() : null);
            planMap.put("status", plan.getStatus());
            planMap.put("isShared", plan.getIs_shared());
            planMap.put("sharedCode", plan.getShared_code());
            planMap.put("lastWorkoutDate", plan.getLast_workout_date() != null ? plan.getLast_workout_date().toString() : null);
            planMap.put("description", plan.getGoal() != null ? plan.getGoal() + "计划" : "健身计划");
            
            int totalDays = plan.getDuration_weeks() != null ? plan.getDuration_weeks() * 7 : 28;
            int currentDay = 1;
            
            if (plan.getStart_date() != null) {
                long daysBetween = ChronoUnit.DAYS.between(plan.getStart_date(), today);
                currentDay = (int) Math.min(Math.max(daysBetween + 1, 1), totalDays);
            }
            
            planMap.put("currentDay", currentDay);
            planMap.put("totalDays", totalDays);
            
            result.add(planMap);
        }
        
        log.info("获取用户计划列表成功: userId={}, 数量: {}", userId, result.size());
        return Result.success(result);
    }
}
