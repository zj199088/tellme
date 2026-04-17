
package com.fitness.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.app.entity.WorkoutSchedule;
import com.fitness.app.mapper.WorkoutScheduleMapper;
import com.fitness.app.service.WorkoutScheduleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkoutScheduleServiceImpl extends ServiceImpl<WorkoutScheduleMapper, WorkoutSchedule> implements WorkoutScheduleService {

    @Override
    public List<WorkoutSchedule> getSchedulesByPlanId(Integer planId) {
        return this.lambdaQuery()
                .eq(WorkoutSchedule::getPlanId, planId)
                .eq(WorkoutSchedule::getIsDeleted, 0)
                .orderByAsc(WorkoutSchedule::getWeekNum)
                .orderByAsc(WorkoutSchedule::getDayOfWeek)
                .list();
    }
}

