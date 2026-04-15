package com.fitness.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.app.entity.FitnessPlan;

public interface FitnessPlanService extends IService<FitnessPlan> {
    Integer createPlanFromTemplate(Integer userId, Integer templateId, String name, String goal, String difficulty, Integer durationWeeks);
}
