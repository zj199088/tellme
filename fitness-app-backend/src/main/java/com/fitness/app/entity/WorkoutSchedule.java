package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("workout_schedules")
public class WorkoutSchedule {
    /**
     * 训练安排ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 计划ID
     */
    private Integer planId;
    
    /**
     * 周数
     */
    private Integer weekNum;
    
    /**
     * 星期几 1:周日 2:周一 3:周二 4:周三 5:周四 6:周五 7:周六
     */
    private Integer dayOfWeek;
    
    /**
     * 训练日期
     */
    private LocalDate date;
    
    /**
     * 是否休息日 0:训练日 1:休息日
     */
    private Integer isRestDay;
    
    /**
     * 休息备注
     */
    private String restNote;
    
    /**
     * 预计训练时长（分钟）
     */
    private Integer estimatedDuration;
    
    /**
     * 模板训练日ID
     */
    private Integer templateDayId;
    
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