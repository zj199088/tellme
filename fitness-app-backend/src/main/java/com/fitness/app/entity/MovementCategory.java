package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("movement_categories")
public class MovementCategory {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @TableField("category_name")
    private String categoryName;
    
    private String description;
    private String icon;
    
    @TableField("sort_order")
    private Integer sortOrder;
    
    @TableField("parent_id")
    private Integer parentId;
    
    @TableField("is_active")
    private Integer isActive;
    
    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}