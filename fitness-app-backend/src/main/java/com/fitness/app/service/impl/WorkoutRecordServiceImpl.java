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
    public WorkoutRecord getTodayWorkout(Integer userId, LocalDate date) {
        return workoutRecordMapper.getByUserIdAndDate(userId, date);
    }

    @Override
    public List<WorkoutRecord> getRecentRecords(Integer userId, int limit) {
        return workoutRecordMapper.getRecentRecords(userId, limit);
    }

    @Override
    public WorkoutRecord createWorkoutRecord(WorkoutRecord record) {
        record.setIs_deleted(0);
        record.setCreated_at(LocalDateTime.now());
        record.setUpdated_at(LocalDateTime.now());
        save(record);
        return record;
    }

    @Override
    public WorkoutRecord updateWorkoutRecord(WorkoutRecord record) {
        record.setUpdated_at(LocalDateTime.now());
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
            record.setUser_id(userId);
            record.setIs_deleted(0);
            record.setCreated_at(now);
            record.setUpdated_at(now);
        }
        saveBatch(records);
        return records;
    }
}