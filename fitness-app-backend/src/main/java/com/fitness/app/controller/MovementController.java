package com.fitness.app.controller;

import com.fitness.app.entity.MovementCategory;
import com.fitness.app.entity.MovementExercise;
import com.fitness.app.service.MovementCategoryService;
import com.fitness.app.service.MovementExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movement")
public class MovementController {

    @Autowired
    private MovementCategoryService movementCategoryService;

    @Autowired
    private MovementExerciseService movementExerciseService;

    @GetMapping("/categories")
    public Map<String, Object> getCategories() {
        List<MovementCategory> categories = movementCategoryService.getAllCategories();
        
        // 转换为前端期望的格式
        List<Map<String, Object>> transformedCategories = new ArrayList<>();
        for (MovementCategory category : categories) {
            Map<String, Object> transformedCategory = new HashMap<>();
            transformedCategory.put("id", category.getId());
            transformedCategory.put("name", category.getCategoryName());
            transformedCategory.put("icon", category.getIcon());
            transformedCategories.add(transformedCategory);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", transformedCategories);
        return response;
    }

    @GetMapping("/exercises")
    public Map<String, Object> getExercises(
            @RequestParam(required = false) Integer categoryId) {
        List<MovementExercise> exercises;
        if (categoryId != null) {
            exercises = movementExerciseService.getExercisesByCategoryId(categoryId);
        } else {
            exercises = movementExerciseService.getAllExercises();
        }
        
        // 转换为前端期望的格式
        List<Map<String, Object>> transformedExercises = new ArrayList<>();
        for (MovementExercise exercise : exercises) {
            Map<String, Object> transformedExercise = new HashMap<>();
            transformedExercise.put("id", exercise.getId());
            transformedExercise.put("name", exercise.getExerciseName());
            transformedExercise.put("description", exercise.getDescription());
            transformedExercise.put("categoryId", exercise.getCategoryId());
            transformedExercise.put("defaultSets", 3); // 默认3组
            transformedExercise.put("defaultReps", "12"); // 默认12次
            transformedExercises.add(transformedExercise);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", transformedExercises);
        return response;
    }
}
