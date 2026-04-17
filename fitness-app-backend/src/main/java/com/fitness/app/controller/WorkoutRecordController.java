package com.fitness.app.controller;

import com.fitness.app.entity.WorkoutRecord;
import com.fitness.app.entity.WorkoutSchedule;
import com.fitness.app.entity.WorkoutScheduleExercise;
import com.fitness.app.service.WorkoutRecordService;
import com.fitness.app.service.AppConfigService;
import com.fitness.app.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/workout")
public class WorkoutRecordController {

    @Autowired
    private WorkoutRecordService workoutRecordService;
    
    @Autowired
    private AppConfigService appConfigService;

    @GetMapping("/today")
    public Result<?> getTodayWorkout(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Integer planId,
            Authentication authentication) {
        Integer userId = Integer.parseInt(authentication.getName());
        
        if (date == null) {
            date = LocalDate.now();
        }
        
        Map<String, Object> result = workoutRecordService.getTodayWorkout(userId, date, planId);
        return Result.success(result);
    }

    @GetMapping("/recent")
    public Result<?> getRecentRecords(
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        Integer userId = Integer.parseInt(authentication.getName());
        
        List<WorkoutRecord> records = workoutRecordService.getRecentRecords(userId, limit);
        int total = workoutRecordService.countAllRecords(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        
        return Result.success(result);
    }

    @GetMapping("/records")
    public Result<?> getRecords(
            @RequestParam(required = false) Integer planId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            Authentication authentication) {
        Integer userId = Integer.parseInt(authentication.getName());
        
        Map<String, Object> result = workoutRecordService.getRecordsWithDetails(userId, planId, date, page, pageSize);
        return Result.success(result);
    }

    @PostMapping("/record")
    public Result<?> createWorkoutRecord(
            @RequestBody WorkoutRecord record,
            Authentication authentication) {
        Integer userId = Integer.parseInt(authentication.getName());
        
        // 检查用户记录数是否超过限制
        int maxRecords = appConfigService.getNumberConfig("max_workout_records_per_user");
        int currentCount = workoutRecordService.countAllRecords(userId);
        if (currentCount >= maxRecords) {
            return Result.error("锻炼记录数已达上限，最大允许" + maxRecords + "条记录");
        }
        
        record.setUserId(userId);
        WorkoutRecord savedRecord = workoutRecordService.createWorkoutRecord(record);
        return Result.success(savedRecord);
    }

    @PutMapping("/record/{id}")
    public Result<?> updateWorkoutRecord(
            @PathVariable Integer id,
            @RequestBody WorkoutRecord record,
            Authentication authentication) {
        Integer userId = Integer.parseInt(authentication.getName());
        
        record.setId(id);
        record.setUserId(userId);
        WorkoutRecord updatedRecord = workoutRecordService.updateWorkoutRecord(record);
        return Result.success(updatedRecord);
    }

    @GetMapping("/schedule")
    public Result<?> getScheduleByPlanAndDate(
            @RequestParam Integer planId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        WorkoutSchedule schedule = workoutRecordService.getScheduleByPlanAndDate(planId, date);
        return Result.success(schedule);
    }

    @GetMapping("/schedule/{scheduleId}/exercises")
    public Result<?> getScheduleExercises(@PathVariable Integer scheduleId) {
        List<WorkoutScheduleExercise> exercises = workoutRecordService.getScheduleExercises(scheduleId);
        return Result.success(exercises);
    }

    @PostMapping("/batch")
    public Result<?> createWorkoutRecords(
            @RequestBody List<WorkoutRecord> records,
            Authentication authentication) {
        Integer userId = Integer.parseInt(authentication.getName());
        
        // 检查用户记录数是否超过限制
        int maxRecords = appConfigService.getNumberConfig("max_workout_records_per_user");
        int currentCount = workoutRecordService.countAllRecords(userId);
        if (currentCount + records.size() > maxRecords) {
            return Result.error("锻炼记录数将超过上限，最大允许" + maxRecords + "条记录");
        }
        
        List<WorkoutRecord> savedRecords = workoutRecordService.createWorkoutRecords(records, userId);
        return Result.success(savedRecords);
    }
}