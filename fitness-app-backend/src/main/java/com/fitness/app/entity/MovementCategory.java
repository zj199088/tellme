package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("movement_category")
public class MovementCategory {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String description;
    
    private LocalDateTime created_at;
    
    private LocalDateTime updated_at;
}