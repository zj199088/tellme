package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("template_exercises")
public class TemplateExercise {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer templateDayId;
    private Integer exerciseId;
    private String exerciseName;
    private Integer sets;
    private String reps;
    private String duration;
    private String note;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer isDeleted;
}
