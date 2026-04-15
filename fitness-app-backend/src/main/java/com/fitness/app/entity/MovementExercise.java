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
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String exerciseName;
    private String enName;
    private Integer categoryId;
    private String difficultyLevel;
    private String targetMuscle;
    private String equipmentNeeded;
    private String description;
    private String videoUrl;
    private String imageUrl;
    private BigDecimal caloriesPerHour;
    private Integer isPopular;
    private Integer isBodyweight;
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}