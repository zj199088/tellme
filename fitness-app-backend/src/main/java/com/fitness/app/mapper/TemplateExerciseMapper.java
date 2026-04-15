package com.fitness.app.mapper;

import com.fitness.app.entity.TemplateExercise;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TemplateExerciseMapper {

    @Select("SELECT * FROM template_exercises WHERE template_day_id = #{templateDayId} AND is_deleted = 0 ORDER BY sort_order")
    List<TemplateExercise> getTemplateExercisesByTemplateDayId(Long templateDayId);
}
