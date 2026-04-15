package com.fitness.app.controller;

import com.fitness.app.entity.WorkoutRecord;
import com.fitness.app.service.WorkoutRecordService;
import com.fitness.app.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/workout")
public class WorkoutRecordController {

    @Autowired
    private WorkoutRecordService workoutRecordService;

    @GetMapping("/today")
    public Result<?> getTodayWorkout(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            
            if (date == null) {
                date = LocalDate.now();
            }
            
            WorkoutRecord record = workoutRecordService.getTodayWorkout(userId, date);
            return Result.success(record);
        } catch (Exception e) {
            return Result.error("获取今日训练失败: " + e.getMessage());
        }
    }

    @GetMapping("/recent")
    public Result<?> getRecentRecords(
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            
            List<WorkoutRecord> records = workoutRecordService.getRecentRecords(userId, limit);
            return Result.success(records);
        } catch (Exception e) {
            return Result.error("获取最近训练记录失败: " + e.getMessage());
        }
    }

    @PostMapping("/record")
    public Result<?> createWorkoutRecord(
            @RequestBody WorkoutRecord record,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            
            record.setUser_id(userId);
            WorkoutRecord savedRecord = workoutRecordService.createWorkoutRecord(record);
            return Result.success(savedRecord);
        } catch (Exception e) {
            return Result.error("创建训练记录失败: " + e.getMessage());
        }
    }

    @PutMapping("/record/{id}")
    public Result<?> updateWorkoutRecord(
            @PathVariable Long id,
            @RequestBody WorkoutRecord record,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            
            record.setId(id);
            record.setUser_id(userId);
            WorkoutRecord updatedRecord = workoutRecordService.updateWorkoutRecord(record);
            return Result.success(updatedRecord);
        } catch (Exception e) {
            return Result.error("更新训练记录失败: " + e.getMessage());
        }
    }
}