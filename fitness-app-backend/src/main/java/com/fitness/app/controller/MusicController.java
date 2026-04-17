package com.fitness.app.controller;

import com.fitness.app.entity.MusicTrack;
import com.fitness.app.service.AppConfigService;
import com.fitness.app.service.MusicTrackService;
import com.fitness.app.common.Result;
import com.fitness.app.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/music")
public class MusicController {
    
    private final MusicTrackService musicTrackService;
    private final AppConfigService appConfigService;
    
    @Autowired
    public MusicController(MusicTrackService musicTrackService, AppConfigService appConfigService) {
        this.musicTrackService = musicTrackService;
        this.appConfigService = appConfigService;
    }
    
    @GetMapping("/tracks")
    public Result<List<MusicTrack>> getMusicList() {
        // 获取当前用户ID
        Integer userId = getCurrentUserId();
        List<MusicTrack> tracks = musicTrackService.getUserMusicList(userId);
        return Result.success(tracks);
    }
    
    @PostMapping("/upload")
    public Result<MusicTrack> uploadMusic(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("artist") String artist,
            @RequestParam("album") String album,
            @RequestParam("genre") String genre
    ) throws IOException {
        // 获取当前用户ID
        Integer userId = getCurrentUserId();
        
        // 检查用户是否为管理员（管理员无限制）
        boolean isAdmin = isAdminUser();
        if (!isAdmin) {
            // 获取用户上传音乐限制
            int maxMusicUploads = appConfigService.getNumberConfig("max_music_uploads_per_user");
            if (maxMusicUploads <= 0) {
                maxMusicUploads = 10; // 默认限制10首
            }
            
            // 检查用户已上传音乐数量
            int currentCount = musicTrackService.countUserMusic(userId);
            if (currentCount >= maxMusicUploads) {
                throw new BusinessException("音乐上传数量已达到上限，最大允许" + maxMusicUploads + "首");
            }
        }
        
        MusicTrack track = musicTrackService.uploadMusic(file, name, artist, album, genre, userId);
        return Result.success(track);
    }
    
    @DeleteMapping("/tracks/{id}")
    public Result<Void> deleteMusic(@PathVariable Integer id) {
        // 获取当前用户ID
        Integer userId = getCurrentUserId();
        
        // 检查音乐是否属于当前用户（管理员可以删除所有）
        MusicTrack track = musicTrackService.getById(id);
        if (track != null && track.getUserId() != null && !track.getUserId().equals(userId) && !isAdminUser()) {
            throw new BusinessException("无权删除他人的音乐");
        }
        
        musicTrackService.removeById(id);
        return Result.success();
    }
    
    // 获取当前用户ID
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            // 假设用户对象存储在principal中，具体实现可能需要调整
            try {
                return (Integer) authentication.getPrincipal();
            } catch (Exception e) {
                throw new BusinessException("获取用户信息失败");
            }
        }
        throw new BusinessException("用户未登录");
    }
    
    // 检查用户是否为管理员
    private boolean isAdminUser() {
        // 具体实现需要根据系统的权限管理方式调整
        // 这里假设通过角色判断
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
