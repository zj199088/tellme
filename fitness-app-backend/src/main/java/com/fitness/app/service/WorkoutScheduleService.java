package com.fitness.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.app.entity.WorkoutSchedule;

import java.util.List;

public interface WorkoutScheduleService extends IService<WorkoutSchedule> {
    List<WorkoutSchedule> getSchedulesByPlanId(Integer planId);
}
