package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("workout_schedule_exercises")
public class WorkoutScheduleExercise {
    /**
     * 训练安排动作ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 训练安排ID
     */
    private Integer scheduleId;
    
    /**
     * 动作ID
     */
    private Integer exerciseId;
    
    /**
     * 动作名称
     */
    private String exerciseName;
    
    /**
     * 组数
     */
    private Integer sets;
    
    /**
     * 次数
     */
    private String reps;
    
    /**
     * 时长
     */
    private String duration;
    
    /**
     * 备注
     */
    private String note;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 是否删除 0:未删除 1:已删除
     */
    private Integer isDeleted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}