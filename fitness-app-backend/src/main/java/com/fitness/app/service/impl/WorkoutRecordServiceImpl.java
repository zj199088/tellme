package com.fitness.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.app.entity.WorkoutRecord;
import com.fitness.app.entity.WorkoutSchedule;
import com.fitness.app.entity.WorkoutScheduleExercise;
import com.fitness.app.mapper.WorkoutRecordMapper;
import com.fitness.app.mapper.WorkoutScheduleExerciseMapper;
import com.fitness.app.mapper.WorkoutScheduleMapper;
import com.fitness.app.service.WorkoutRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkoutRecordServiceImpl extends ServiceImpl<WorkoutRecordMapper, WorkoutRecord> implements WorkoutRecordService {

    @Autowired
    private WorkoutRecordMapper workoutRecordMapper;

    @Autowired
    private WorkoutScheduleMapper workoutScheduleMapper;

    @Autowired
    private WorkoutScheduleExerciseMapper workoutScheduleExerciseMapper;

    @Override
    public Map<String, Object> getTodayWorkout(Integer userId, LocalDate date) {
        Map<String, Object> result = new HashMap<>();
        
        // 首先尝试获取今日的训练记录
        WorkoutRecord record = workoutRecordMapper.getByUserIdAndDate(userId, date);
        
        // 获取今日的训练安排
        WorkoutSchedule schedule = workoutScheduleMapper.getByUserIdAndDate(userId, date);
        result.put("schedule", schedule);
        
        // 如果有训练安排，获取训练动作
        if (schedule != null) {
            List<WorkoutScheduleExercise> exercises = workoutScheduleExerciseMapper.getByScheduleId(schedule.getId());
            result.put("exercises", exercises);
        } else {
            result.put("exercises", null);
        }
        
        return result;
    }

    @Override
    public List<WorkoutRecord> getRecentRecords(Integer userId, int limit) {
        return workoutRecordMapper.getRecentRecords(userId, limit);
    }

    @Override
    public WorkoutRecord createWorkoutRecord(WorkoutRecord record) {
        record.setIsDeleted(0);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        save(record);
        return record;
    }

    @Override
    public WorkoutRecord updateWorkoutRecord(WorkoutRecord record) {
        record.setUpdatedAt(LocalDateTime.now());
        updateById(record);
        return record;
    }

    @Override
    public WorkoutSchedule getScheduleByPlanAndDate(Integer planId, LocalDate date) {
        // 这里需要实现根据计划ID和日期获取训练日程的逻辑
        // 可以通过查询workout_schedules表实现
        return null;
    }

    @Override
    public List<WorkoutScheduleExercise> getScheduleExercises(Integer scheduleId) {
        return workoutScheduleExerciseMapper.getByScheduleId(scheduleId);
    }

    @Override
    public Map<String, Object> getRecordsWithDetails(Integer userId, Integer planId, LocalDate date, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<WorkoutRecord> records = workoutRecordMapper.getRecordsWithDetails(userId, planId, date, pageSize, offset);
        int total = workoutRecordMapper.countRecords(userId, planId, date);
        int totalPages = (total + pageSize - 1) / pageSize;
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("totalPages", totalPages);
        result.put("currentPage", page);
        result.put("pageSize", pageSize);
        
        return result;
    }
    
    @Override
    public int countAllRecords(Integer userId) {
        return workoutRecordMapper.countAllRecords(userId);
    }

    @Override
    public List<WorkoutRecord> createWorkoutRecords(List<WorkoutRecord> records, Integer userId) {
        LocalDateTime now = LocalDateTime.now();
        for (WorkoutRecord record : records) {
            record.setUserId(userId);
            record.setIsDeleted(0);
            record.setCreatedAt(now);
            record.setUpdatedAt(now);
        }
        saveBatch(records);
        return records;
    }
}