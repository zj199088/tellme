package com.fitness.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.app.entity.WorkoutRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface WorkoutRecordMapper extends BaseMapper<WorkoutRecord> {
    @Select("SELECT * FROM workout_record WHERE user_id = #{userId} AND date = #{date}")
    WorkoutRecord getByUserIdAndDate(Long userId, LocalDate date);
    
    @Select("SELECT * FROM workout_record WHERE user_id = #{userId} ORDER BY date DESC LIMIT #{limit}")
    List<WorkoutRecord> getRecentRecords(Long userId, int limit);
}