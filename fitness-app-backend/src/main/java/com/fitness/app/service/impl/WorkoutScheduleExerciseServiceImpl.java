package com.fitness.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.app.entity.WorkoutScheduleExercise;
import com.fitness.app.mapper.WorkoutScheduleExerciseMapper;
import com.fitness.app.service.WorkoutScheduleExerciseService;
import org.springframework.stereotype.Service;

@Service
public class WorkoutScheduleExerciseServiceImpl extends ServiceImpl<WorkoutScheduleExerciseMapper, WorkoutScheduleExercise> implements WorkoutScheduleExerciseService {
}