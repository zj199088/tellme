package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("movement_exercises")
public class MovementExercise {
    /**
     * 动作ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 动作名称
     */
    private String exerciseName;
    
    /**
     * 英文名称
     */
    private String enName;
    
    /**
     * 分类ID
     */
    private Integer categoryId;
    
    /**
     * 难度等级
     */
    private String difficultyLevel;
    
    /**
     * 目标肌群
     */
    private String targetMuscle;
    
    /**
     * 所需器材
     */
    private String equipmentNeeded;
    
    /**
     * 动作描述
     */
    private String description;
    
    /**
     * 视频URL
     */
    private String videoUrl;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 每小时消耗卡路里
     */
    private BigDecimal caloriesPerHour;
    
    /**
     * 是否热门 0:否 1:是
     */
    private Integer isPopular;
    
    /**
     * 是否无器械 0:否 1:是
     */
    private Integer isBodyweight;
    
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