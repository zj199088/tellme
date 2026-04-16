package com.fitness.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.app.entity.TemplateExercise;
import com.fitness.app.mapper.TemplateExerciseMapper;
import com.fitness.app.service.TemplateExerciseService;
import org.springframework.stereotype.Service;

@Service
public class TemplateExerciseServiceImpl extends ServiceImpl<TemplateExerciseMapper, TemplateExercise> implements TemplateExerciseService {
}
