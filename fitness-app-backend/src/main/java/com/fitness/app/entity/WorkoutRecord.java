
package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("workout_record")
public class WorkoutRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long user_id;
    
    private Long plan_id;
    
    private LocalDate date;
    
    private Integer day_number;
    
    private Boolean completed;
    
    private String exercises_json;
    
    private LocalDateTime created_at;
    
    private LocalDateTime updated_at;
}

