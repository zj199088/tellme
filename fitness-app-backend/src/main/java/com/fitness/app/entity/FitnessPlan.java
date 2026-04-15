package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("fitness_plans")
public class FitnessPlan {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer user_id;
    private Integer template_id;
    private String name;
    private String type;
    private String goal;
    private String difficulty;
    private Integer duration_weeks;
    private LocalDate start_date;
    private LocalDate end_date;
    private String status;
    private Integer is_shared;
    private String shared_code;
    private LocalDate last_workout_date;
    private Integer is_deleted;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
