
package com.fitness.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.app.entity.WorkoutRecord;
import java.time.LocalDate;
import java.util.List;

public interface WorkoutRecordService extends IService&lt;WorkoutRecord&gt; {
    WorkoutRecord getTodayWorkout(Long userId, LocalDate date);
    List&lt;WorkoutRecord&gt; getRecentRecords(Long userId, int limit);
    WorkoutRecord createWorkoutRecord(WorkoutRecord record);
    WorkoutRecord updateWorkoutRecord(WorkoutRecord record);
}

