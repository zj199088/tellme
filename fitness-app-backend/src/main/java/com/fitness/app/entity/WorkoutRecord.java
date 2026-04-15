
package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;

@Data
@TableName("workout_record")
public class WorkoutRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long planId;
    
    private LocalDate date;
    
    private Integer dayNumber;
    
    private Boolean completed;
    
    private String exercisesJson;
    
    private java.time.LocalDateTime createdAt;
    
    private java.time.LocalDateTime updatedAt;
}

