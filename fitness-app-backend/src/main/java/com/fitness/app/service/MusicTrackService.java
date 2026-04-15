package com.fitness.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.app.entity.MusicTrack;

import java.util.List;

public interface MusicTrackService extends IService<MusicTrack> {
    List<MusicTrack> getActiveTracks();
}