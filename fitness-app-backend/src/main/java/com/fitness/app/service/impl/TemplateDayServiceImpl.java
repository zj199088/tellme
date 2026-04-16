package com.fitness.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.app.entity.TemplateDay;
import com.fitness.app.mapper.TemplateDayMapper;
import com.fitness.app.service.TemplateDayService;
import org.springframework.stereotype.Service;

@Service
public class TemplateDayServiceImpl extends ServiceImpl<TemplateDayMapper, TemplateDay> implements TemplateDayService {
}
