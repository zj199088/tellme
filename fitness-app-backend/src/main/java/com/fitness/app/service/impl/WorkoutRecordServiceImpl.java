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
import java.util.List;

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
}