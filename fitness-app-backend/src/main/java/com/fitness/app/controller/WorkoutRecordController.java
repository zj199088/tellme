package com.fitness.app.controller;

import com.fitness.app.entity.WorkoutRecord;
import com.fitness.app.entity.WorkoutSchedule;
import com.fitness.app.entity.WorkoutScheduleExercise;
import com.fitness.app.service.WorkoutRecordService;
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

    @GetMapping("/today")
    public Result<?> getTodayWorkout(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {
        try {
            Integer userId = Integer.parseInt(authentication.getName());
            
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
            Integer userId = Integer.parseInt(authentication.getName());
            
            List<WorkoutRecord> records = workoutRecordService.getRecentRecords(userId, limit);
            int total = workoutRecordService.countAllRecords(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("records", records);
            result.put("total", total);
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取最近训练记录失败: " + e.getMessage());
        }
    }

    @GetMapping("/records")
    public Result<?> getRecords(
            @RequestParam(required = false) Integer planId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            Authentication authentication) {
        try {
            Integer userId = Integer.parseInt(authentication.getName());
            
            Map<String, Object> result = workoutRecordService.getRecordsWithDetails(userId, planId, date, page, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取训练记录失败: " + e.getMessage());
        }
    }

    @PostMapping("/record")
    public Result<?> createWorkoutRecord(
            @RequestBody WorkoutRecord record,
            Authentication authentication) {
        try {
            Integer userId = Integer.parseInt(authentication.getName());
            
            record.setUser_id(userId);
            WorkoutRecord savedRecord = workoutRecordService.createWorkoutRecord(record);
            return Result.success(savedRecord);
        } catch (Exception e) {
            return Result.error("创建训练记录失败: " + e.getMessage());
        }
    }

    @PutMapping("/record/{id}")
    public Result<?> updateWorkoutRecord(
            @PathVariable Integer id,
            @RequestBody WorkoutRecord record,
            Authentication authentication) {
        try {
            Integer userId = Integer.parseInt(authentication.getName());
            
            record.setId(id);
            record.setUser_id(userId);
            WorkoutRecord updatedRecord = workoutRecordService.updateWorkoutRecord(record);
            return Result.success(updatedRecord);
        } catch (Exception e) {
            return Result.error("更新训练记录失败: " + e.getMessage());
        }
    }

    @GetMapping("/schedule")
    public Result<?> getScheduleByPlanAndDate(
            @RequestParam Integer planId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            WorkoutSchedule schedule = workoutRecordService.getScheduleByPlanAndDate(planId, date);
            return Result.success(schedule);
        } catch (Exception e) {
            return Result.error("获取训练日程失败: " + e.getMessage());
        }
    }

    @GetMapping("/schedule/{scheduleId}/exercises")
    public Result<?> getScheduleExercises(@PathVariable Integer scheduleId) {
        try {
            List<WorkoutScheduleExercise> exercises = workoutRecordService.getScheduleExercises(scheduleId);
            return Result.success(exercises);
        } catch (Exception e) {
            return Result.error("获取训练日程动作失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch")
    public Result<?> createWorkoutRecords(
            @RequestBody List<WorkoutRecord> records,
            Authentication authentication) {
        try {
            Integer userId = Integer.parseInt(authentication.getName());
            
            List<WorkoutRecord> savedRecords = workoutRecordService.createWorkoutRecords(records, userId);
            return Result.success(savedRecords);
        } catch (Exception e) {
            return Result.error("批量创建训练记录失败: " + e.getMessage());
        }
    }
}