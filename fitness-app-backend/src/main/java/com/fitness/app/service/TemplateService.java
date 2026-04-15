package com.fitness.app.service;

import com.fitness.app.entity.Template;
import com.fitness.app.entity.TemplateDay;
import com.fitness.app.entity.TemplateExercise;
import com.fitness.app.mapper.TemplateMapper;
import com.fitness.app.mapper.TemplateDayMapper;
import com.fitness.app.mapper.TemplateExerciseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateService {

    @Autowired
    private TemplateMapper templateMapper;

    @Autowired
    private TemplateDayMapper templateDayMapper;

    @Autowired
    private TemplateExerciseMapper templateExerciseMapper;

    public List<Template> getPublicTemplates() {
        return templateMapper.getPublicTemplates();
    }

    public Template getTemplateById(Long id) {
        return templateMapper.getTemplateById(id);
    }

    public List<TemplateDay> getTemplateDaysByTemplateId(Long templateId) {
        return templateDayMapper.getTemplateDaysByTemplateId(templateId);
    }

    public List<TemplateExercise> getTemplateExercisesByTemplateDayId(Long templateDayId) {
        return templateExerciseMapper.getTemplateExercisesByTemplateDayId(templateDayId);
    }
}
