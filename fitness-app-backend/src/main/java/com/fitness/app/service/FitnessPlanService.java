package com.fitness.app.service;

import com.fitness.app.entity.FitnessPlan;
import com.fitness.app.dto.CreatePlanRequest;
import com.fitness.app.mapper.FitnessPlanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FitnessPlanService {

    @Autowired
    private FitnessPlanMapper fitnessPlanMapper;

    public Long createPlanFromTemplate(Long userId, CreatePlanRequest request) {
        FitnessPlan plan = new FitnessPlan();
        plan.setUser_id(userId);
        plan.setTemplate_id(request.getTemplate_id());
        plan.setName(request.getName());
        plan.setType(request.getType());
        plan.setGoal(request.getGoal());
        plan.setDifficulty(request.getDifficulty());
        plan.setDuration_weeks(request.getDuration_weeks());
        plan.setStart_date(request.getStart_date());
        plan.setStatus("active");
        plan.setIs_deleted(false);
        plan.setCreated_at(new Date());
        plan.setUpdated_at(new Date());

        fitnessPlanMapper.insert(plan);
        return plan.getId();
    }
}
