package com.fitness.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.app.entity.MovementExercise;
import com.fitness.app.mapper.MovementExerciseMapper;
import com.fitness.app.service.MovementExerciseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovementExerciseServiceImpl extends ServiceImpl<MovementExerciseMapper, MovementExercise> implements MovementExerciseService {

    @Override
    public List<MovementExercise> getAllExercises() {
        return list();
    }

    @Override
    public List<MovementExercise> getExercisesByCategoryId(Long categoryId) {
        return baseMapper.getByCategoryId(categoryId);
    }
}
