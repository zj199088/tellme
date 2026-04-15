package com.fitness.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.app.entity.MusicTrack;
import com.fitness.app.mapper.MusicTrackMapper;
import com.fitness.app.service.MusicTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicTrackServiceImpl extends ServiceImpl<MusicTrackMapper, MusicTrack> implements MusicTrackService {

    @Autowired
    private MusicTrackMapper musicTrackMapper;

    @Override
    public List<MusicTrack> getActiveTracks() {
        return musicTrackMapper.getActiveTracks();
    }
}