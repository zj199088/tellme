package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("music_tracks")
public class MusicTrack {
    /**
     * 音乐ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 音乐名称
     */
    private String name;
    
    /**
     * 艺术家
     */
    private String artist;
    
    /**
     * 专辑
     */
    private String album;
    
    /**
     * 时长（秒）
     */
    private Integer duration;
    
    /**
     * 文件URL
     */
    private String fileUrl;
    
    /**
     * 封面URL
     */
    private String coverUrl;
    
    /**
     * 音乐类型
     */
    private String genre;
    
    /**
     * 节拍数（BPM）
     */
    private Integer bpm;
    
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