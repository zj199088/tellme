
package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("workout_records")
public class WorkoutRecord {
    /**
     * 训练记录ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 计划ID
     */
    private Integer planId;
    
    /**
     * 训练安排ID
     */
    private Integer scheduleId;
    
    /**
     * 训练安排动作ID
     */
    private Integer scheduleExerciseId;
    
    /**
     * 动作ID
     */
    private Integer exerciseId;
    
    /**
     * 动作名称
     */
    private String exerciseName;
    
    /**
     * 训练日期
     */
    private LocalDate date;
    
    /**
     * 完成的组数
     */
    private String setsCompleted;
    
    /**
     * 重量
     */
    private BigDecimal weight;
    
    /**
     * 时长（秒）
     */
    private Integer duration;
    
    /**
     * 备注
     */
    private String notes;
    
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

