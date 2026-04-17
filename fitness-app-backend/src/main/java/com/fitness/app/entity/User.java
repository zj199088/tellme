package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 微信OpenID
     */
    private String openId;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像URL
     */
    private String avatarUrl;
    
    /**
     * 性别 0:未知 1:男 2:女
     */
    private Integer gender;
    
    /**
     * 国家
     */
    private String country;
    
    /**
     * 省份
     */
    private String province;
    
    /**
     * 城市
     */
    private String city;
    
    /**
     * 角色 user:普通用户 admin:管理员
     */
    private String role;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码（加密后）
     */
    private String password;
    
    /**
     * 健身目标
     */
    private String fitnessGoals;
    
    /**
     * 健身水平
     */
    private String fitnessLevel;
    
    /**
     * 身高（米）
     */
    private BigDecimal height;
    
    /**
     * 体重（公斤）
     */
    private BigDecimal weight;
    
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