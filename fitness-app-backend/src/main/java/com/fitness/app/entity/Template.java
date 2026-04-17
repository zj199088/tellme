package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("templates")
public class Template {
    /**
     * 模板ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 模板名称
     */
    private String name;
    
    /**
     * 模板描述
     */
    private String description;
    
    /**
     * 模板图片URL
     */
    private String image;
    
    /**
     * 难度等级
     */
    private String difficulty;
    
    /**
     * 是否公开 0:私有 1:公开
     */
    private Integer isPublic;
    
    /**
     * 创建者ID
     */
    private Integer createdBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 是否删除 0:未删除 1:已删除
     */
    private Integer isDeleted;
}
