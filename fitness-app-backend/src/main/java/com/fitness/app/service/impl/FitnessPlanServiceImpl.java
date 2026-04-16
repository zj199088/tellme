package com.fitness.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.app.entity.FitnessPlan;
import com.fitness.app.entity.TemplateDay;
import com.fitness.app.entity.TemplateExercise;
import com.fitness.app.entity.WorkoutSchedule;
import com.fitness.app.entity.WorkoutScheduleExercise;
import com.fitness.app.mapper.FitnessPlanMapper;
import com.fitness.app.mapper.TemplateDayMapper;
import com.fitness.app.mapper.TemplateExerciseMapper;
import com.fitness.app.mapper.WorkoutScheduleExerciseMapper;
import com.fitness.app.mapper.WorkoutScheduleMapper;
import com.fitness.app.service.FitnessPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FitnessPlanServiceImpl extends ServiceImpl<FitnessPlanMapper, FitnessPlan> implements FitnessPlanService {

    @Autowired
    private TemplateDayMapper templateDayMapper;

    @Autowired
    private TemplateExerciseMapper templateExerciseMapper;

    @Autowired
    private WorkoutScheduleMapper workoutScheduleMapper;

    @Autowired
    private WorkoutScheduleExerciseMapper workoutScheduleExerciseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer createPlanFromTemplate(Integer userId, Integer templateId, String name, String goal, String difficulty, Integer durationWeeks, String startDate) {
        LocalDate startDateObj = LocalDate.now();
        if (startDate != null && !startDate.isEmpty()) {
            try {
                startDateObj = LocalDate.parse(startDate);
            } catch (Exception e) {
                log.warn("无效的开始日期格式: {}", startDate, e);
            }
        }
        
        FitnessPlan plan = new FitnessPlan();
        plan.setUser_id(userId);
        plan.setTemplate_id(templateId);
        plan.setName(name);
        plan.setType("template");
        plan.setGoal(goal);
        plan.setDifficulty(difficulty);
        plan.setDuration_weeks(durationWeeks);
        plan.setStart_date(startDateObj);
        plan.setEnd_date(startDateObj.plusWeeks(durationWeeks));
        plan.setStatus("active");
        plan.setIs_shared(0);
        plan.setIs_deleted(0);
        plan.setCreated_at(LocalDateTime.now());
        plan.setUpdated_at(LocalDateTime.now());

        save(plan);
        
        // 生成训练安排
        generateWorkoutSchedules(plan.getId(), templateId, plan.getStart_date(), durationWeeks);
        
        return plan.getId();
    }
    
    @Transactional(rollbackFor = Exception.class)
    private void generateWorkoutSchedules(Integer planId, Integer templateId, LocalDate startDate, Integer durationWeeks) {
        // 获取模板训练日
        List<TemplateDay> templateDays = templateDayMapper.getTemplateDaysByTemplateId(templateId);
        
        if (templateDays.isEmpty()) {
            return;
        }
        
        // 生成训练安排
        for (int week = 0; week < durationWeeks; week++) {
            for (TemplateDay templateDay : templateDays) {
                LocalDate scheduleDate = startDate.plusWeeks(week).plusDays(templateDay.getDayOfWeek() - 1);
                
                // 创建训练安排
                WorkoutSchedule schedule = new WorkoutSchedule();
                schedule.setPlan_id(planId);
                schedule.setDay_of_week(templateDay.getDayOfWeek());
                schedule.setIs_rest_day(templateDay.getIsRestDay());
                schedule.setEstimated_duration(templateDay.getEstimatedDuration());
                schedule.setDate(scheduleDate);
                schedule.setIs_deleted(0);
                schedule.setCreated_at(LocalDateTime.now());
                schedule.setUpdated_at(LocalDateTime.now());
                
                workoutScheduleMapper.insert(schedule);
                
                // 如果不是休息日，生成训练动作
                if (templateDay.getIsRestDay() == 0) {
                    List<TemplateExercise> templateExercises = templateExerciseMapper.getTemplateExercisesByTemplateDayId(templateDay.getId());
                    
                    for (TemplateExercise templateExercise : templateExercises) {
                        WorkoutScheduleExercise scheduleExercise = new WorkoutScheduleExercise();
                        scheduleExercise.setSchedule_id(schedule.getId());
                        scheduleExercise.setExercise_id(templateExercise.getExerciseId());
                        scheduleExercise.setExercise_name(templateExercise.getExerciseName());
                        scheduleExercise.setSets(templateExercise.getSets());
                        scheduleExercise.setReps(templateExercise.getReps());
                        scheduleExercise.setSort_order(templateExercise.getSortOrder());
                        scheduleExercise.setIs_deleted(0);
                        scheduleExercise.setCreated_at(LocalDateTime.now());
                        scheduleExercise.setUpdated_at(LocalDateTime.now());
                        
                        workoutScheduleExerciseMapper.insert(scheduleExercise);
                    }
                }
            }
        }
    }
}