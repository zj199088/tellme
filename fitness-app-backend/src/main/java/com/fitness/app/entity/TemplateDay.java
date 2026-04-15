package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("template_days")
public class TemplateDay {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer templateId;
    private Integer dayOfWeek;
    private Integer isRestDay;
    private String restNote;
    private Integer estimatedDuration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer isDeleted;
}
