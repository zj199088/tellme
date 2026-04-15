package com.fitness.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.app.entity.MovementExercise;

import java.util.List;

public interface MovementExerciseService extends IService<MovementExercise> {
    List<MovementExercise> getAllExercises();
    List<MovementExercise> getExercisesByCategoryId(Long categoryId);
}