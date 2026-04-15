package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("fitness_plan")
public class FitnessPlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long user_id;
    private Long template_id;
    private String name;
    private String description;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
    private Integer current_day;
    private Integer total_days;
    private String status;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
