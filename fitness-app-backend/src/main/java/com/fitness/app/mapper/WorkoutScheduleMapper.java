package com.fitness.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.app.entity.WorkoutSchedule;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WorkoutScheduleMapper extends BaseMapper<WorkoutSchedule> {
}