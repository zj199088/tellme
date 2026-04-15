package com.fitness.app.mapper;

import com.fitness.app.entity.Template;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TemplateMapper {

    @Select("SELECT * FROM templates WHERE is_public = 1 AND is_deleted = 0")
    List<Template> getPublicTemplates();

    @Select("SELECT * FROM templates WHERE id = #{id} AND is_deleted = 0")
    Template getTemplateById(Long id);
}
