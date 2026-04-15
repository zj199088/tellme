package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_devices")
public class UserDevice {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String deviceType;
    private String deviceToken;
    private String deviceId;
    private String appVersion;
    private LocalDateTime lastLoginAt;
    private Integer isActive;
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}