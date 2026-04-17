package com.fitness.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.app.entity.MusicTrack;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MusicTrackService extends IService<MusicTrack> {
    List<MusicTrack> getActiveTracks();
    MusicTrack uploadMusic(MultipartFile file, String name, String artist, String album, String genre) throws IOException;
}