package com.fitness.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.app.entity.MovementExercise;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MovementExerciseMapper extends BaseMapper<MovementExercise> {
    @Select("SELECT * FROM movement_exercises WHERE category_id = #{categoryId} AND is_deleted = 0")
    List<MovementExercise> getByCategoryId(Integer categoryId);
}