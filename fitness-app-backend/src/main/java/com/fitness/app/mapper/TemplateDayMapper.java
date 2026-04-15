package com.fitness.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.app.entity.TemplateDay;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TemplateDayMapper extends BaseMapper<TemplateDay> {
    @Select("SELECT * FROM template_days WHERE template_id = #{templateId} AND is_deleted = 0 ORDER BY day_of_week")
    List<TemplateDay> getTemplateDaysByTemplateId(Integer templateId);
}
