package com.fitness.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.app.entity.MovementExercise;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MovementExerciseMapper extends BaseMapper<MovementExercise> {
    @Select("SELECT * FROM movement_exercise WHERE category_id = #{categoryId}")
    List<MovementExercise> getByCategoryId(Long categoryId);
}