package com.fitness.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.app.entity.Template;
import com.fitness.app.entity.TemplateDay;
import com.fitness.app.entity.TemplateExercise;

import java.util.List;

public interface TemplateService extends IService<Template> {
    List<Template> getPublicTemplates();
    Template getTemplateById(Integer id);
    List<TemplateDay> getTemplateDaysByTemplateId(Integer templateId);
    List<TemplateExercise> getTemplateExercisesByTemplateDayId(Integer templateDayId);
    List<Template> getUserTemplates(Integer userId);
    List<Template> getUserAndPublicTemplates(Integer userId);
}
