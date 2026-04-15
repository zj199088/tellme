package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("workout_schedule")
public class WorkoutSchedule {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long user_id;
    
    private Long plan_id;
    
    private LocalDate date;
    
    private String status;
    
    private LocalDateTime created_at;
    
    private LocalDateTime updated_at;
}