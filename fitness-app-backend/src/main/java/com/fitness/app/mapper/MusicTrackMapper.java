package com.fitness.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.app.entity.MusicTrack;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface MusicTrackMapper extends BaseMapper<MusicTrack> {
    @Select("SELECT * FROM music_tracks WHERE is_active = 1 AND is_deleted = 0 ORDER BY id")
    List<MusicTrack> getActiveTracks();
}