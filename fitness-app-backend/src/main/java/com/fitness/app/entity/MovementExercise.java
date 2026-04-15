package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("movement_exercises")
public class MovementExercise {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("exercise_name")
    private String exerciseName;
    
    @TableField("en_name")
    private String enName;
    
    @TableField("category_id")
    private Integer categoryId;
    
    @TableField("difficulty_level")
    private String difficultyLevel;
    
    @TableField("target_muscle")
    private String targetMuscle;
    
    @TableField("equipment_needed")
    private String equipmentNeeded;
    
    private String description;
    
    @TableField("video_url")
    private String videoUrl;
    
    @TableField("image_url")
    private String imageUrl;
    
    @TableField("calories_per_hour")
    private Double caloriesPerHour;
    
    @TableField("is_popular")
    private Integer isPopular;
    
    @TableField("is_bodyweight")
    private Integer isBodyweight;
    
    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}