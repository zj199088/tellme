package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_devices")
public class UserDevice {
    /**
     * 设备ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 设备类型
     */
    private String deviceType;
    
    /**
     * 设备令牌
     */
    private String deviceToken;
    
    /**
     * 设备唯一标识
     */
    private String deviceId;
    
    /**
     * 应用版本
     */
    private String appVersion;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginAt;
    
    /**
     * 是否激活 0:未激活 1:激活
     */
    private Integer isActive;
    
    /**
     * 是否删除 0:未删除 1:已删除
     */
    private Integer isDeleted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}