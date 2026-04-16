package com.fitness.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.app.entity.Template;
import com.fitness.app.entity.TemplateDay;
import com.fitness.app.entity.TemplateExercise;
import com.fitness.app.mapper.TemplateMapper;
import com.fitness.app.mapper.TemplateDayMapper;
import com.fitness.app.mapper.TemplateExerciseMapper;
import com.fitness.app.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, Template> implements TemplateService {

    @Autowired
    private TemplateMapper templateMapper;

    @Autowired
    private TemplateDayMapper templateDayMapper;

    @Autowired
    private TemplateExerciseMapper templateExerciseMapper;

    @Override
    public List<Template> getPublicTemplates() {
        return templateMapper.getPublicTemplates();
    }

    @Override
    public Template getTemplateById(Integer id) {
        return templateMapper.getTemplateById(id);
    }

    @Override
    public List<TemplateDay> getTemplateDaysByTemplateId(Integer templateId) {
        return templateDayMapper.getTemplateDaysByTemplateId(templateId);
    }

    @Override
    public List<TemplateExercise> getTemplateExercisesByTemplateDayId(Integer templateDayId) {
        return templateExerciseMapper.getTemplateExercisesByTemplateDayId(templateDayId);
    }

    @Override
    public List<Template> getUserTemplates(Integer userId) {
        return templateMapper.getUserTemplates(userId);
    }

    @Override
    public List<Template> getUserAndPublicTemplates(Integer userId) {
        return templateMapper.getUserAndPublicTemplates(userId);
    }
}