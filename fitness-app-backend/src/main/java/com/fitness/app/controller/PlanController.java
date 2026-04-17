package com.fitness.app.controller;

import com.fitness.app.entity.FitnessPlan;
import com.fitness.app.entity.Template;
import com.fitness.app.entity.TemplateDay;
import com.fitness.app.entity.TemplateExercise;
import com.fitness.app.entity.WorkoutRecord;
import com.fitness.app.entity.WorkoutSchedule;
import com.fitness.app.entity.WorkoutScheduleExercise;
import com.fitness.app.service.FitnessPlanService;
import com.fitness.app.service.TemplateService;
import com.fitness.app.service.TemplateDayService;
import com.fitness.app.service.TemplateExerciseService;
import com.fitness.app.service.WorkoutRecordService;
import com.fitness.app.service.WorkoutScheduleService;
import com.fitness.app.service.WorkoutScheduleExerciseService;
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
    
    @Autowired
    private WorkoutScheduleService workoutScheduleService;
    
    @Autowired
    private WorkoutScheduleExerciseService workoutScheduleExerciseService;
    
    @Autowired
    private WorkoutRecordService workoutRecordService;

    @PostMapping("/template")
    public Result<?> createPlanFromTemplate(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        Integer userId = Integer.parseInt(authentication.getName());
        Integer templateId = (Integer) request.get("templateId");
        String name = (String) request.get("name");
        String goal = (String) request.get("goal");
        String difficulty = (String) request.get("difficulty");
        Integer durationWeeks = (Integer) request.get("durationWeeks");
        String startDateStr = (String) request.get("startDate");
        
        log.info("从模板创建计划: userId={}, templateId={}, name={}, goal={}, difficulty={}, durationWeeks={}, startDate={}", 
                 userId, templateId, name, goal, difficulty, durationWeeks, startDateStr);
        
        // 参数验证
        if (templateId == null) {
            log.warn("模板ID不能为空");
            return Result.error("模板ID不能为空");
        }
        if (name == null || name.isEmpty()) {
            log.warn("计划名称不能为空");
            return Result.error("计划名称不能为空");
        }
        if (goal == null || goal.isEmpty()) {
            log.warn("健身目标不能为空");
            return Result.error("健身目标不能为空");
        }
        if (difficulty == null || difficulty.isEmpty()) {
            log.warn("难度等级不能为空");
            return Result.error("难度等级不能为空");
        }
        if (durationWeeks == null || durationWeeks <= 0) {
            log.warn("训练周期必须大于0");
            return Result.error("训练周期必须大于0");
        }
        
        // 检查用户进行中的计划数量（最多2个）
        long activePlanCount = fitnessPlanService.lambdaQuery()
                .eq(FitnessPlan::getUserId, userId)
                .eq(FitnessPlan::getStatus, "active")
                .eq(FitnessPlan::getIsDeleted, 0)
                .count();
        
        if (activePlanCount >= 2) {
            log.warn("用户进行中的计划已达上限（2个）: userId={}", userId);
            return Result.error("进行中的计划最多2个，请先完成或暂停其他计划");
        }
        
        Integer planId = fitnessPlanService.createPlanFromTemplate(userId, templateId, name, goal, difficulty, durationWeeks, startDateStr);
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
        template.setImage((String) request.get("image")); // 保存封面图片
        template.setIsPublic(0); // 默认为私有
        template.setCreatedBy(userId);
        template.setIsDeleted(0);
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        
        templateService.save(template);
        
        // 创建模板训练日和动作，同时保存它们的关系
        List<TemplateDay> createdTemplateDays = new ArrayList<>();
        if (request.containsKey("days")) {
            List<Map<String, Object>> days = (List<Map<String, Object>>) request.get("days");
            log.info("处理训练日数据，共 {} 天", days.size());
            for (Map<String, Object> dayData : days) {
                log.info("处理训练日: dayOfWeek={}, isRestDay={}", dayData.get("dayOfWeek"), dayData.get("isRestDay"));
                // 创建模板训练日
                TemplateDay templateDay = new TemplateDay();
                templateDay.setTemplateId(template.getId());
                templateDay.setDayOfWeek((Integer) dayData.get("dayOfWeek"));
                templateDay.setIsRestDay((Integer) dayData.get("isRestDay"));
                templateDay.setRestNote((String) dayData.get("restNote"));
                templateDay.setEstimatedDuration(30);
                templateDay.setIsDeleted(0);
                templateDay.setCreatedAt(LocalDateTime.now());
                templateDay.setUpdatedAt(LocalDateTime.now());
                
                templateDayService.save(templateDay);
                createdTemplateDays.add(templateDay);
                
                // 创建模板动作
                if (dayData.containsKey("exercises")) {
                    List<Map<String, Object>> exercises = (List<Map<String, Object>>) dayData.get("exercises");
                    log.info("处理训练日动作，共 {} 个动作", exercises.size());
                    int sortOrder = 1;
                    for (Map<String, Object> exerciseData : exercises) {
                        TemplateExercise templateExercise = new TemplateExercise();
                        templateExercise.setTemplateDayId(templateDay.getId());
                        templateExercise.setExerciseId((Integer) exerciseData.get("exerciseId"));
                        templateExercise.setExerciseName((String) exerciseData.get("exerciseName"));
                        // 处理sets字段，支持数字和字符串类型
                        Object setsObj = exerciseData.get("sets");
                        if (setsObj != null) {
                            if (setsObj instanceof Number) {
                                templateExercise.setSets(((Number) setsObj).intValue());
                            } else if (setsObj instanceof String) {
                                try {
                                    templateExercise.setSets(Integer.parseInt((String) setsObj));
                                } catch (NumberFormatException e) {
                                    templateExercise.setSets(3);
                                }
                            } else {
                                templateExercise.setSets(3);
                            }
                        } else {
                            templateExercise.setSets(3);
                        }
                        // 处理reps字段，支持数字和字符串类型
                        Object repsObj = exerciseData.get("reps");
                        if (repsObj != null) {
                            templateExercise.setReps(repsObj.toString());
                        } else {
                            templateExercise.setReps("12");
                        }
                        templateExercise.setSortOrder(sortOrder++);
                        templateExercise.setIsDeleted(0);
                        templateExercise.setCreatedAt(LocalDateTime.now());
                        templateExercise.setUpdatedAt(LocalDateTime.now());
                        
                        templateExerciseService.save(templateExercise);
                    }
                }
            }
        }
        
        // 检查用户进行中的计划数量（最多2个）
        long activePlanCount = fitnessPlanService.lambdaQuery()
                .eq(FitnessPlan::getUserId, userId)
                .eq(FitnessPlan::getStatus, "active")
                .eq(FitnessPlan::getIsDeleted, 0)
                .count();
        
        if (activePlanCount >= 2) {
            log.warn("用户进行中的计划已达上限（2个）: userId={}", userId);
            return Result.error("进行中的计划最多2个，请先完成或暂停其他计划");
        }
        
        // 创建自定义计划
        FitnessPlan plan = new FitnessPlan();
        plan.setUserId(userId);
        plan.setTemplateId(template.getId());
        plan.setName((String) request.get("name"));
        plan.setType("custom");
        plan.setGoal((String) request.get("goal"));
        plan.setDifficulty((String) request.get("difficulty"));
        plan.setDurationWeeks((Integer) request.get("durationWeeks"));
        
        // 处理开始日期
        String startDateStr = (String) request.get("startDate");
        LocalDate startDate = startDateStr != null ? LocalDate.parse(startDateStr) : LocalDate.now();
        plan.setStartDate(startDate);
        plan.setEndDate(startDate.plusWeeks((Integer) request.get("durationWeeks")));
        
        plan.setStatus("active");
        plan.setIsShared(0);
        plan.setIsDeleted(0);
        plan.setCreatedAt(LocalDateTime.now());
        plan.setUpdatedAt(LocalDateTime.now());
        
        fitnessPlanService.save(plan);
        
        // 创建训练日程（根据计划的周数，每周重复模板的训练日）
        Integer durationWeeks = (Integer) request.get("durationWeeks");
        
        // 获取开始日期是星期几（1-7，周一到周日）
        int startDayOfWeek = startDate.getDayOfWeek().getValue();
        
        for (int weekNum = 1; weekNum <= durationWeeks; weekNum++) {
            for (TemplateDay templateDay : createdTemplateDays) {
                int trainDayOfWeek = templateDay.getDayOfWeek();
                
                // 计算训练日相对于开始日期的偏移天数
                int offset;
                if (trainDayOfWeek >= startDayOfWeek) {
                    offset = trainDayOfWeek - startDayOfWeek;
                } else {
                    offset = trainDayOfWeek - startDayOfWeek + 7;
                }
                
                // 计算当前训练日程的日期
                // 第1周的训练日 = 开始日期 + offset天
                // 第2周的训练日 = 开始日期 + offset天 + 7天
                // 第n周的训练日 = 开始日期 + offset天 + (n-1)*7天
                LocalDate scheduleDate = startDate.plusDays(offset).plusWeeks(weekNum - 1);
                
                // 创建训练日程
                WorkoutSchedule workoutSchedule = new WorkoutSchedule();
                workoutSchedule.setPlanId(plan.getId());
                workoutSchedule.setWeekNum(weekNum);
                workoutSchedule.setDayOfWeek(templateDay.getDayOfWeek());
                workoutSchedule.setDate(scheduleDate);
                workoutSchedule.setIsRestDay(templateDay.getIsRestDay());
                workoutSchedule.setRestNote(templateDay.getRestNote());
                workoutSchedule.setEstimatedDuration(templateDay.getEstimatedDuration());
                workoutSchedule.setTemplateDayId(templateDay.getId());
                workoutSchedule.setIsDeleted(0);
                workoutSchedule.setCreatedAt(LocalDateTime.now());
                workoutSchedule.setUpdatedAt(LocalDateTime.now());
                
                workoutScheduleService.save(workoutSchedule);
                
                // 如果不是休息日，创建训练日程动作
                if (templateDay.getIsRestDay() == 0) {
                    List<TemplateExercise> templateExercises = templateExerciseService.lambdaQuery()
                            .eq(TemplateExercise::getTemplateDayId, templateDay.getId())
                            .list();
                    
                    for (TemplateExercise templateExercise : templateExercises) {
                        WorkoutScheduleExercise workoutScheduleExercise = new WorkoutScheduleExercise();
                        workoutScheduleExercise.setScheduleId(workoutSchedule.getId());
                        workoutScheduleExercise.setExerciseId(templateExercise.getExerciseId());
                        workoutScheduleExercise.setExerciseName(templateExercise.getExerciseName());
                        workoutScheduleExercise.setSets(templateExercise.getSets());
                        workoutScheduleExercise.setReps(templateExercise.getReps());
                        workoutScheduleExercise.setSortOrder(templateExercise.getSortOrder());
                        workoutScheduleExercise.setIsDeleted(0);
                        workoutScheduleExercise.setCreatedAt(LocalDateTime.now());
                        workoutScheduleExercise.setUpdatedAt(LocalDateTime.now());
                        
                        workoutScheduleExerciseService.save(workoutScheduleExercise);
                    }
                }
            }
        }
        
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
        if (plan == null || !plan.getUserId().equals(userId)) {
            return Result.error("计划不存在或无权限");
        }
        
        // 获取模板
        if (plan.getTemplateId() == null) {
            return Result.error("计划未关联模板");
        }
        
        Template template = templateService.getById(plan.getTemplateId());
        if (template == null) {
            return Result.error("模板不存在");
        }
        
        // 设置模板为公开
        template.setIsPublic(1);
        template.setUpdatedAt(LocalDateTime.now());
        templateService.updateById(template);
        
        // 更新计划的分享状态
        plan.setIsShared(1);
        plan.setUpdatedAt(LocalDateTime.now());
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
        if (plan == null || !plan.getUserId().equals(userId)) {
            return Result.error("计划不存在或无权限");
        }
        
        plan.setStatus(status);
        plan.setUpdatedAt(LocalDateTime.now());
        fitnessPlanService.updateById(plan);
        
        log.info("更新计划状态成功: planId={}, status={}", planId, status);
        return Result.success("计划状态更新成功");
    }

    @GetMapping("/user")
    public Result<?> getUserPlans(Authentication authentication) {
        Integer userId = Integer.parseInt(authentication.getName());
        log.info("获取用户计划列表: userId={}", userId);
        
        List<FitnessPlan> plans = fitnessPlanService.lambdaQuery()
                .eq(FitnessPlan::getUserId, userId)
                .eq(FitnessPlan::getIsDeleted, 0)
                .list();
        
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (FitnessPlan plan : plans) {
            Map<String, Object> planMap = new HashMap<>();
            planMap.put("id", plan.getId());
            planMap.put("userId", plan.getUserId());
            planMap.put("templateId", plan.getTemplateId());
            planMap.put("name", plan.getName());
            planMap.put("type", plan.getType());
            planMap.put("goal", plan.getGoal());
            planMap.put("difficulty", plan.getDifficulty());
            planMap.put("durationWeeks", plan.getDurationWeeks());
            planMap.put("startDate", plan.getStartDate() != null ? plan.getStartDate().toString() : null);
            planMap.put("endDate", plan.getEndDate() != null ? plan.getEndDate().toString() : null);
            planMap.put("status", plan.getStatus());
            planMap.put("isShared", plan.getIsShared());
            planMap.put("sharedCode", plan.getSharedCode());
            planMap.put("lastWorkoutDate", plan.getLastWorkoutDate() != null ? plan.getLastWorkoutDate().toString() : null);
            planMap.put("description", plan.getGoal() != null ? plan.getGoal() + "计划" : "健身计划");
            
            // 获取模板图片
            if (plan.getTemplateId() != null) {
                try {
                    Template template = templateService.getById(plan.getTemplateId());
                    if (template != null) {
                        planMap.put("image", template.getImage());
                    }
                } catch (Exception e) {
                    log.warn("获取模板图片失败: planId={}, templateId={}", plan.getId(), plan.getTemplateId(), e);
                }
            }
            
            int totalDays = plan.getDurationWeeks() != null ? plan.getDurationWeeks() * 7 : 28;
            int currentDay = 1;
            
            if (plan.getStartDate() != null) {
                long daysBetween = ChronoUnit.DAYS.between(plan.getStartDate(), today);
                currentDay = (int) Math.min(Math.max(daysBetween + 1, 1), totalDays);
            }
            
            planMap.put("currentDay", currentDay);
            planMap.put("totalDays", totalDays);
            
            result.add(planMap);
        }
        
        log.info("获取用户计划列表成功: userId={}, 数量: {}", userId, result.size());
        return Result.success(result);
    }
    
    @DeleteMapping("/{planId}")
    public Result<?> deletePlan(
            @PathVariable Integer planId,
            Authentication authentication) {
        Integer userId = Integer.parseInt(authentication.getName());
        log.info("删除计划: planId={}, userId={}", planId, userId);
        
        try {
            // 获取计划
            FitnessPlan plan = fitnessPlanService.getById(planId);
            if (plan == null) {
                log.warn("计划不存在: planId={}", planId);
                return Result.error("计划不存在");
            }
            
            // 验证权限
            if (!plan.getUserId().equals(userId)) {
                log.warn("没有权限删除此计划: planId={}, userId={}", planId, userId);
                return Result.error("没有权限删除此计划");
            }
            
            // 1. 删除锻炼记录 (workout_records)
            workoutRecordService.lambdaQuery()
                    .eq(com.fitness.app.entity.WorkoutRecord::getPlanId, planId)
                    .list()
                    .forEach(workoutRecordService::removeById);
            log.info("删除锻炼记录成功: planId={}", planId);
            
            // 2. 获取所有训练日程ID
            List<Integer> scheduleIds = workoutScheduleService.lambdaQuery()
                    .eq(com.fitness.app.entity.WorkoutSchedule::getPlanId, planId)
                    .list()
                    .stream()
                    .map(com.fitness.app.entity.WorkoutSchedule::getId)
                    .collect(java.util.stream.Collectors.toList());
            
            if (!scheduleIds.isEmpty()) {
                // 3. 删除训练日程动作 (workout_schedule_exercises)
                workoutScheduleExerciseService.lambdaQuery()
                        .in(com.fitness.app.entity.WorkoutScheduleExercise::getScheduleId, scheduleIds)
                        .list()
                        .forEach(workoutScheduleExerciseService::removeById);
                log.info("删除训练日程动作成功: planId={}", planId);
                
                // 4. 删除训练日程 (workout_schedules)
                workoutScheduleService.removeByIds(scheduleIds);
                log.info("删除训练日程成功: planId={}", planId);
            }
            
            // 5. 如果是自定义计划，删除关联的模板数据
            if ("custom".equals(plan.getType()) && plan.getTemplateId() != null) {
                Integer templateId = plan.getTemplateId();
                
                // 获取所有模板训练日ID
                List<Integer> templateDayIds = templateDayService.lambdaQuery()
                        .eq(com.fitness.app.entity.TemplateDay::getTemplateId, templateId)
                        .list()
                        .stream()
                        .map(com.fitness.app.entity.TemplateDay::getId)
                        .collect(java.util.stream.Collectors.toList());
                
                if (!templateDayIds.isEmpty()) {
                    // 6. 删除模板动作关联 (template_exercises)
                    templateExerciseService.lambdaQuery()
                            .in(com.fitness.app.entity.TemplateExercise::getTemplateDayId, templateDayIds)
                            .list()
                            .forEach(templateExerciseService::removeById);
                    log.info("删除模板动作关联成功: planId={}", planId);
                    
                    // 7. 删除模板训练日 (template_days)
                    templateDayService.removeByIds(templateDayIds);
                    log.info("删除模板训练日成功: planId={}", planId);
                }
                
                // 8. 删除模板 (templates)
                templateService.removeById(templateId);
                log.info("删除模板成功: planId={}", planId);
            }
            
            // 9. 删除健身计划 (fitness_plans)
            fitnessPlanService.removeById(planId);
            log.info("删除健身计划成功: planId={}", planId);
            
            log.info("删除计划成功: planId={}", planId);
            return Result.success("计划删除成功");
        } catch (Exception e) {
            log.error("删除计划失败: planId={}", planId, e);
            return Result.error("删除计划失败: " + e.getMessage());
        }
    }
}
