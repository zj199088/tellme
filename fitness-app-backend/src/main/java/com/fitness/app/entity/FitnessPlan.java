package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("fitness_plans")
public class FitnessPlan {
    /**
     * 计划ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 模板ID
     */
    private Integer templateId;
    
    /**
     * 计划名称
     */
    private String name;
    
    /**
     * 计划类型 template:模板 custom:自定义
     */
    private String type;
    
    /**
     * 健身目标
     */
    private String goal;
    
    /**
     * 难度等级
     */
    private String difficulty;
    
    /**
     * 训练周期（周）
     */
    private Integer durationWeeks;
    
    /**
     * 开始日期
     */
    private LocalDate startDate;
    
    /**
     * 结束日期
     */
    private LocalDate endDate;
    
    /**
     * 状态 active:进行中 completed:已完成 paused:暂停
     */
    private String status;
    
    /**
     * 是否分享 0:未分享 1:已分享
     */
    private Integer isShared;
    
    /**
     * 分享码
     */
    private String sharedCode;
    
    /**
     * 最后训练日期
     */
    private LocalDate lastWorkoutDate;
    
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
