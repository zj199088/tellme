package com.fitness.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.app.entity.WorkoutRecord;
import com.fitness.app.entity.WorkoutSchedule;
import com.fitness.app.entity.WorkoutScheduleExercise;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutRecordService extends IService<WorkoutRecord> {
    WorkoutRecord getTodayWorkout(Integer userId, LocalDate date);
    List<WorkoutRecord> getRecentRecords(Integer userId, int limit);
    WorkoutRecord createWorkoutRecord(WorkoutRecord record);
    WorkoutRecord updateWorkoutRecord(WorkoutRecord record);
    WorkoutSchedule getScheduleByPlanAndDate(Integer planId, LocalDate date);
    List<WorkoutScheduleExercise> getScheduleExercises(Integer scheduleId);
}