package com.fitness.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.app.entity.WorkoutScheduleExercise;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface WorkoutScheduleExerciseMapper extends BaseMapper<WorkoutScheduleExercise> {
    @Select("SELECT * FROM workout_schedule_exercises WHERE schedule_id = #{scheduleId} AND is_deleted = 0 ORDER BY sort_order")
    List<WorkoutScheduleExercise> getByScheduleId(Integer scheduleId);
}