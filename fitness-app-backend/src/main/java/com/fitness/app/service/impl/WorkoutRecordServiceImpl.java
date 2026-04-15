package com.fitness.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.app.entity.WorkoutRecord;
import com.fitness.app.entity.WorkoutSchedule;
import com.fitness.app.mapper.WorkoutRecordMapper;
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

    @Override
    public WorkoutRecord getTodayWorkout(Long userId, LocalDate date) {
        return workoutRecordMapper.getByUserIdAndDate(userId, date);
    }

    @Override
    public List<WorkoutRecord> getRecentRecords(Long userId, int limit) {
        return workoutRecordMapper.getRecentRecords(userId, limit);
    }

    @Override
    public WorkoutRecord createWorkoutRecord(WorkoutRecord record) {
        // 创建或更新训练日程
        WorkoutSchedule schedule = getOrCreateSchedule(record.getUser_id(), record.getPlan_id(), record.getDate());
        schedule.setStatus("in_progress");
        workoutScheduleMapper.updateById(schedule);
        
        record.setCreated_at(LocalDateTime.now());
        record.setUpdated_at(LocalDateTime.now());
        save(record);
        return record;
    }

    @Override
    public WorkoutRecord updateWorkoutRecord(WorkoutRecord record) {
        record.setUpdated_at(LocalDateTime.now());
        updateById(record);
        
        // 如果训练完成，更新日程状态
        if (record.getCompleted()) {
            WorkoutSchedule schedule = getOrCreateSchedule(record.getUser_id(), record.getPlan_id(), record.getDate());
            schedule.setStatus("completed");
            workoutScheduleMapper.updateById(schedule);
        }
        
        return record;
    }

    @Override
    public WorkoutSchedule getOrCreateSchedule(Long userId, Long planId, LocalDate date) {
        WorkoutSchedule schedule = workoutScheduleMapper.getByUserIdAndDate(userId, date);
        if (schedule == null) {
            schedule = new WorkoutSchedule();
            schedule.setUser_id(userId);
            schedule.setPlan_id(planId);
            schedule.setDate(date);
            schedule.setStatus("scheduled");
            schedule.setCreated_at(LocalDateTime.now());
            schedule.setUpdated_at(LocalDateTime.now());
            workoutScheduleMapper.insert(schedule);
        }
        return schedule;
    }
}