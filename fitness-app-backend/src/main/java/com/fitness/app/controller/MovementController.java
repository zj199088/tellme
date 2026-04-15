package com.fitness.app.controller;

import com.fitness.app.entity.MovementCategory;
import com.fitness.app.entity.MovementExercise;
import com.fitness.app.service.MovementCategoryService;
import com.fitness.app.service.MovementExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movement")
public class MovementController {

    @Autowired
    private MovementCategoryService movementCategoryService;

    @Autowired
    private MovementExerciseService movementExerciseService;

    @GetMapping("/categories")
    public Map<String, Object> getCategories() {
        List<MovementCategory> categories = movementCategoryService.getAllCategories();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", categories);
        return response;
    }

    @GetMapping("/exercises")
    public Map<String, Object> getExercises(
            @RequestParam(required = false) Long categoryId) {
        List<MovementExercise> exercises;
        if (categoryId != null) {
            exercises = movementExerciseService.getExercisesByCategoryId(categoryId);
        } else {
            exercises = movementExerciseService.getAllExercises();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", exercises);
        return response;
    }
}
