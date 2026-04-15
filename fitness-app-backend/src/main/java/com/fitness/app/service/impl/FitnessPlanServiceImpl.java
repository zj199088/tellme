package com.fitness.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.app.entity.FitnessPlan;
import com.fitness.app.mapper.FitnessPlanMapper;
import com.fitness.app.service.FitnessPlanService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class FitnessPlanServiceImpl extends ServiceImpl<FitnessPlanMapper, FitnessPlan> implements FitnessPlanService {

    @Override
    public Integer createPlanFromTemplate(Integer userId, Integer templateId, String name, String goal, String difficulty, Integer durationWeeks) {
        FitnessPlan plan = new FitnessPlan();
        plan.setUser_id(userId);
        plan.setTemplate_id(templateId);
        plan.setName(name);
        plan.setType("template");
        plan.setGoal(goal);
        plan.setDifficulty(difficulty);
        plan.setDuration_weeks(durationWeeks);
        plan.setStart_date(LocalDate.now());
        plan.setEnd_date(LocalDate.now().plusWeeks(durationWeeks));
        plan.setStatus("active");
        plan.setIs_shared(0);
        plan.setIs_deleted(0);
        plan.setCreated_at(LocalDateTime.now());
        plan.setUpdated_at(LocalDateTime.now());

        save(plan);
        return plan.getId();
    }
}