package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("workout_schedule_exercises")
public class WorkoutScheduleExercise {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer scheduleId;
    private Integer exerciseId;
    private String exerciseName;
    private Integer sets;
    private String reps;
    private String duration;
    private String note;
    private Integer sortOrder;
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}