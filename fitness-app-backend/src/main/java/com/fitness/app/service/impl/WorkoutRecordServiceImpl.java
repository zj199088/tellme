
package com.fitness.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.app.entity.WorkoutRecord;
import com.fitness.app.mapper.WorkoutRecordMapper;
import com.fitness.app.service.WorkoutRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WorkoutRecordServiceImpl extends ServiceImpl&lt;WorkoutRecordMapper, WorkoutRecord&gt; implements WorkoutRecordService {

    @Override
    public WorkoutRecord getTodayWorkout(Long userId, LocalDate date) {
        LambdaQueryWrapper&lt;WorkoutRecord&gt; wrapper = new LambdaQueryWrapper&lt;&gt;();
        wrapper.eq(WorkoutRecord::getUserId, userId)
               .eq(WorkoutRecord::getDate, date);
        return getOne(wrapper);
    }

    @Override
    public List&lt;WorkoutRecord&gt; getRecentRecords(Long userId, int limit) {
        LambdaQueryWrapper&lt;WorkoutRecord&gt; wrapper = new LambdaQueryWrapper&lt;&gt;();
        wrapper.eq(WorkoutRecord::getUserId, userId)
               .orderByDesc(WorkoutRecord::getDate)
               .last("LIMIT " + limit);
        return list(wrapper);
    }

    @Override
    public WorkoutRecord createWorkoutRecord(WorkoutRecord record) {
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
}

