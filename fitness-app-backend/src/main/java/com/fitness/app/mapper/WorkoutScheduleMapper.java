package com.fitness.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.app.entity.WorkoutSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

@Mapper
public interface WorkoutScheduleMapper extends BaseMapper<WorkoutSchedule> {
    @Select("SELECT ws.* FROM workout_schedules ws JOIN fitness_plans fp ON ws.plan_id = fp.id WHERE fp.user_id = #{userId} AND ws.date = #{date} AND ws.is_deleted = 0 AND fp.is_deleted = 0")
    WorkoutSchedule getByUserIdAndDate(@Param("userId") Integer userId, @Param("date") LocalDate date);
}