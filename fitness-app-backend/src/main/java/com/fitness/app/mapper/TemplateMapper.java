package com.fitness.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.app.entity.Template;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TemplateMapper extends BaseMapper<Template> {
    @Select("SELECT * FROM templates WHERE is_public = 1 AND is_deleted = 0")
    List<Template> getPublicTemplates();

    @Select("SELECT * FROM templates WHERE id = #{id} AND is_deleted = 0")
    Template getTemplateById(Integer id);
    
    @Select("SELECT * FROM templates WHERE created_by = #{userId} AND is_deleted = 0")
    List<Template> getUserTemplates(Integer userId);
    
    @Select("SELECT * FROM templates WHERE (created_by = #{userId} OR is_public = 1) AND is_deleted = 0")
    List<Template> getUserAndPublicTemplates(Integer userId);
}
