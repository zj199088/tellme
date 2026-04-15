package com.fitness.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("music_tracks")
public class MusicTrack {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String artist;
    private String album;
    private Integer duration;
    private String fileUrl;
    private String coverUrl;
    private String genre;
    private Integer bpm;
    private Integer isActive;
    private Integer isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}