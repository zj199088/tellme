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
import java.util.ArrayList;
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
    public Map<String, Object> getTodayWorkout(Integer userId, LocalDate date, Integer planId) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取今日的训练安排
        WorkoutSchedule schedule;
        if (planId != null) {
            // 根据计划ID和日期获取训练安排
            schedule = workoutScheduleMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<WorkoutSchedule>()
                    .eq("plan_id", planId)
                    .eq("date", date)
                    .eq("is_deleted", 0)
            );
        } else {
            // 原来的逻辑，根据用户ID和日期获取训练安排
            schedule = workoutScheduleMapper.getByUserIdAndDate(userId, date);
        }
        result.put("schedule", schedule);
        
        // 如果有训练安排，获取训练动作和对应的训练记录
        if (schedule != null) {
            List<WorkoutScheduleExercise> exercises = workoutScheduleExerciseMapper.getByScheduleId(schedule.getId());
            result.put("exercises", exercises);
            
            // 获取今日的所有训练记录
            List<WorkoutRecord> records = workoutRecordMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<WorkoutRecord>()
                    .eq("user_id", userId)
                    .eq("date", date)
                    .eq("is_deleted", 0)
            );
            result.put("records", records);
            
            // 为了保持向后兼容性，返回第一条训练记录
            if (!records.isEmpty()) {
                result.put("record", records.get(0));
            } else {
                result.put("record", null);
            }
        } else {
            result.put("exercises", null);
            result.put("records", null);
            result.put("record", null);
        }
        
        return result;
    }

    @Override
    public List<WorkoutRecord> getRecentRecords(Integer userId, int limit) {
        return workoutRecordMapper.getRecentRecords(userId, limit);
    }

    @Override
    public WorkoutRecord createWorkoutRecord(WorkoutRecord record) {
        LocalDateTime now = LocalDateTime.now();
        
        // 如果设置了 scheduleId，从训练安排中获取 planId
        if (record.getScheduleId() != null) {
            WorkoutSchedule schedule = workoutScheduleMapper.selectById(record.getScheduleId());
            if (schedule != null) {
                record.setPlanId(schedule.getPlanId());
            }
        }
        record.setIsDeleted(0);
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        
        // 检查是否所有组数都完成
        boolean allSetsCompleted = false;
        if (record.getSetsCompleted() != null) {
            try {
                String[] setsArray = record.getSetsCompleted().replace("[", "").replace("]", "").split(",");
                allSetsCompleted = true;
                for (String set : setsArray) {
                    if (set.trim().equals("0")) {
                        allSetsCompleted = false;
                        break;
                    }
                }
            } catch (Exception e) {
                // 解析失败，默认不计算时长
            }
        }
        
        // 如果所有组数都完成，时长设置为0（刚创建）
        if (allSetsCompleted) {
            record.setDuration(0);
        }
        
        save(record);
        return record;
    }

    @Override
    public WorkoutRecord updateWorkoutRecord(WorkoutRecord record) {
        LocalDateTime now = LocalDateTime.now();
        record.setUpdatedAt(now);
        
        // 检查是否所有组数都完成
        boolean allSetsCompleted = false;
        if (record.getSetsCompleted() != null) {
            try {
                String[] setsArray = record.getSetsCompleted().replace("[", "").replace("]", "").split(",");
                allSetsCompleted = true;
                for (String set : setsArray) {
                    if (set.trim().equals("0")) {
                        allSetsCompleted = false;
                        break;
                    }
                }
            } catch (Exception e) {
                // 解析失败，默认不计算时长
            }
        }
        
        // 如果所有组数都完成，计算时长
        if (allSetsCompleted) {
            // 获取现有记录的创建时间
            WorkoutRecord existingRecord = getById(record.getId());
            if (existingRecord != null && existingRecord.getCreatedAt() != null) {
                long durationSeconds = java.time.Duration.between(existingRecord.getCreatedAt(), now).getSeconds();
                record.setDuration((int) durationSeconds);
            }
        }
        
        updateById(record);
        return record;
    }

    @Override
    public WorkoutSchedule getScheduleByPlanAndDate(Integer planId, LocalDate date) {
        // 根据计划ID和日期获取训练安排
        return workoutScheduleMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<WorkoutSchedule>()
                .eq("plan_id", planId)
                .eq("date", date)
                .eq("is_deleted", 0)
        );
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
        List<WorkoutRecord> result = new ArrayList<>();
        
        for (WorkoutRecord record : records) {
            // 如果设置了 scheduleId，从训练安排中获取 planId
            if (record.getScheduleId() != null) {
                WorkoutSchedule schedule = workoutScheduleMapper.selectById(record.getScheduleId());
                if (schedule != null) {
                    record.setPlanId(schedule.getPlanId());
                }
            }
            record.setUserId(userId);
            record.setIsDeleted(0);
            
            // 查找是否已存在该动作的训练记录
            WorkoutRecord existingRecord = workoutRecordMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<WorkoutRecord>()
                    .eq("user_id", userId)
                    .eq("date", record.getDate())
                    .eq("schedule_exercise_id", record.getScheduleExerciseId())
                    .eq("is_deleted", 0)
            );
            
            // 检查是否所有组数都完成
            boolean allSetsCompleted = false;
            if (record.getSetsCompleted() != null) {
                try {
                    String[] setsArray = record.getSetsCompleted().replace("[", "").replace("]", "").split(",");
                    allSetsCompleted = true;
                    for (String set : setsArray) {
                        if (set.trim().equals("0")) {
                            allSetsCompleted = false;
                            break;
                        }
                    }
                } catch (Exception e) {
                    // 解析失败，默认不计算时长
                }
            }
            
            if (existingRecord != null) {
                // 更新现有记录
                existingRecord.setSetsCompleted(record.getSetsCompleted());
                existingRecord.setWeight(record.getWeight());
                existingRecord.setNotes(record.getNotes());
                existingRecord.setUpdatedAt(now);
                
                // 如果所有组数都完成，计算时长
                if (allSetsCompleted && existingRecord.getCreatedAt() != null) {
                    long durationSeconds = java.time.Duration.between(existingRecord.getCreatedAt(), now).getSeconds();
                    existingRecord.setDuration((int) durationSeconds);
                } else {
                    // 未完成所有组数，保持原有时长或设置为0
                    if (record.getDuration() != null) {
                        existingRecord.setDuration(record.getDuration());
                    }
                }
                
                updateById(existingRecord);
                result.add(existingRecord);
            } else {
                // 创建新记录
                record.setCreatedAt(now);
                record.setUpdatedAt(now);
                
                // 如果所有组数都完成，时长设置为0（刚创建）
                if (allSetsCompleted) {
                    record.setDuration(0);
                }
                
                save(record);
                result.add(record);
            }
        }
        return result;
    }
}