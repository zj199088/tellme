package com.fitness.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.app.entity.WorkoutSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface WorkoutScheduleMapper extends BaseMapper<WorkoutSchedule> {
    WorkoutSchedule getByUserIdAndDate(@Param("userId") Integer userId, @Param("date") LocalDate date);
}