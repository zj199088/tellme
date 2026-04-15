package com.fitness.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.app.entity.WorkoutSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

@Mapper
public interface WorkoutScheduleMapper extends BaseMapper<WorkoutSchedule> {
    @Select("SELECT * FROM workout_schedule WHERE user_id = #{userId} AND date = #{date}")
    WorkoutSchedule getByUserIdAndDate(Long userId, LocalDate date);
}