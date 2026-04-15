package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("workout_schedules")
public class WorkoutSchedule {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer plan_id;
    
    private Integer week_num;
    
    private Integer day_of_week;
    
    private Integer is_rest_day;
    
    private String rest_note;
    
    private Integer estimated_duration;
    
    private Integer template_day_id;
    
    private Integer is_deleted;
    
    private LocalDateTime created_at;
    
    private LocalDateTime updated_at;
}