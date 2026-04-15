package com.fitness.app.mapper;

import com.fitness.app.entity.TemplateDay;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TemplateDayMapper {

    @Select("SELECT * FROM template_days WHERE template_id = #{templateId} AND is_deleted = 0 ORDER BY day_of_week")
    List<TemplateDay> getTemplateDaysByTemplateId(Long templateId);
}
