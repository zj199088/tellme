package com.fitness.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.app.entity.WorkoutRecord;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

public interface WorkoutRecordMapper extends BaseMapper<WorkoutRecord> {
    @Select("SELECT * FROM workout_records WHERE user_id = #{userId} AND date = #{date}")
    WorkoutRecord getByUserIdAndDate(Integer userId, LocalDate date);
    
    @Select("SELECT * FROM workout_records WHERE user_id = #{userId} ORDER BY date DESC LIMIT #{limit}")
    List<WorkoutRecord> getRecentRecords(Integer userId, int limit);
}