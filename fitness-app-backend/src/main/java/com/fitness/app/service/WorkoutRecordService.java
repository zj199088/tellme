package com.fitness.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.app.entity.WorkoutRecord;
import com.fitness.app.entity.WorkoutSchedule;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutRecordService extends IService<WorkoutRecord> {
    WorkoutRecord getTodayWorkout(Long userId, LocalDate date);
    List<WorkoutRecord> getRecentRecords(Long userId, int limit);
    WorkoutRecord createWorkoutRecord(WorkoutRecord record);
    WorkoutRecord updateWorkoutRecord(WorkoutRecord record);
    WorkoutSchedule getOrCreateSchedule(Long userId, Long planId, LocalDate date);
}