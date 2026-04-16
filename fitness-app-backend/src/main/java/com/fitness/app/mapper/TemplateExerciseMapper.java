package com.fitness.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.app.entity.TemplateExercise;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface TemplateExerciseMapper extends BaseMapper<TemplateExercise> {
    @Select("SELECT * FROM template_exercises WHERE template_day_id = #{templateDayId}")
    List<TemplateExercise> getTemplateExercisesByTemplateDayId(Integer templateDayId);
}
