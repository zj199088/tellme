
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
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer user_id;
    
    private Integer plan_id;
    
    private Integer schedule_id;
    
    private Integer schedule_exercise_id;
    
    private Integer exercise_id;
    
    private String exercise_name;
    
    private LocalDate date;
    
    private String sets_completed;
    
    private BigDecimal weight;
    
    private Integer duration;
    
    private String notes;
    
    private Integer is_deleted;
    
    private LocalDateTime created_at;
    
    private LocalDateTime updated_at;
}

