package com.fitness.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.app.entity.MusicTrack;
import com.fitness.app.mapper.MusicTrackMapper;
import com.fitness.app.service.CosService;
import com.fitness.app.service.MusicTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MusicTrackServiceImpl extends ServiceImpl<MusicTrackMapper, MusicTrack> implements MusicTrackService {

    @Autowired
    private MusicTrackMapper musicTrackMapper;
    
    @Autowired
    private CosService cosService;

    @Override
    public List<MusicTrack> getActiveTracks() {
        return musicTrackMapper.getActiveTracks();
    }
    
    @Override
    public MusicTrack uploadMusic(MultipartFile file, String name, String artist, String album, String genre) throws IOException {
        // 上传音乐文件到COS
        String fileUrl = cosService.uploadMusic(file);
        
        // 创建新的音乐轨道对象
        MusicTrack track = new MusicTrack();
        track.setName(name);
        track.setArtist(artist);
        track.setAlbum(album);
        track.setGenre(genre);
        track.setFileUrl(fileUrl);
        track.setIsActive(1);
        track.setIsDeleted(0);
        track.setCreatedAt(LocalDateTime.now());
        track.setUpdatedAt(LocalDateTime.now());
        
        // 保存到数据库
        save(track);
        
        return track;
    }
}