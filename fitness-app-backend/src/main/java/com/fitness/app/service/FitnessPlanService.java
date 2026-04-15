package com.fitness.app.service;

import com.fitness.app.entity.FitnessPlan;
import com.fitness.app.dto.CreatePlanRequest;
import com.fitness.app.mapper.FitnessPlanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FitnessPlanService {

    @Autowired
    private FitnessPlanMapper fitnessPlanMapper;

    public Long createPlanFromTemplate(Long userId, CreatePlanRequest request) {
        FitnessPlan plan = new FitnessPlan();
        plan.setUser_id(userId);
        plan.setTemplate_id(request.getTemplate_id());
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setStart_date(LocalDateTime.now());
        plan.setCurrent_day(1);
        plan.setTotal_days(request.getDuration_weeks() * 7);
        plan.setStatus("active");
        plan.setCreated_at(LocalDateTime.now());
        plan.setUpdated_at(LocalDateTime.now());

        fitnessPlanMapper.insert(plan);
        return plan.getId();
    }
}
