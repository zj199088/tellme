package com.fitness.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fitness.app.entity.MovementCategory;
import com.fitness.app.entity.MovementExercise;
import com.fitness.app.entity.User;
import com.fitness.app.service.MovementCategoryService;
import com.fitness.app.service.MovementExerciseService;
import com.fitness.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private MovementCategoryService movementCategoryService;
    
    @Autowired
    private MovementExerciseService movementExerciseService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // 运动分类管理
    @GetMapping("/categories")
    public List<MovementCategory> getCategories() {
        return movementCategoryService.list(new QueryWrapper<MovementCategory>().eq("is_deleted", 0).orderByAsc("sort_order"));
    }
    
    @PostMapping("/categories")
    public MovementCategory createCategory(@RequestBody MovementCategory category) {
        category.setIsDeleted(0);
        category.setIsActive(1);
        movementCategoryService.save(category);
        return category;
    }
    
    @PutMapping("/categories/{id}")
    public MovementCategory updateCategory(@PathVariable Integer id, @RequestBody MovementCategory category) {
        category.setId(id);
        movementCategoryService.updateById(category);
        return category;
    }
    
    @DeleteMapping("/categories/{id}")
    public Map<String, String> deleteCategory(@PathVariable Integer id) {
        movementCategoryService.removeById(id);
        return Map.of("message", "删除成功");
    }
    
    // 运动动作管理
    @GetMapping("/exercises")
    public List<MovementExercise> getExercises() {
        return movementExerciseService.list(new QueryWrapper<MovementExercise>().eq("is_deleted", 0));
    }
    
    @PostMapping("/exercises")
    public MovementExercise createExercise(@RequestBody MovementExercise exercise) {
        exercise.setIsDeleted(0);
        movementExerciseService.save(exercise);
        return exercise;
    }
    
    @PutMapping("/exercises/{id}")
    public MovementExercise updateExercise(@PathVariable Integer id, @RequestBody MovementExercise exercise) {
        exercise.setId(id);
        movementExerciseService.updateById(exercise);
        return exercise;
    }
    
    @DeleteMapping("/exercises/{id}")
    public Map<String, String> deleteExercise(@PathVariable Integer id) {
        movementExerciseService.removeById(id);
        return Map.of("message", "删除成功");
    }
    
    // 用户管理（超级管理员）
    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.list(new QueryWrapper<User>().eq("is_deleted", 0));
    }
    
    @PostMapping("/users/set-admin")
    public Map<String, Object> setAdmin(@RequestBody Map<String, Integer> request) {
        Integer userId = request.get("user_id");
        User user = userService.getById(userId);
        if (user == null) {
            return Map.of("success", false, "message", "用户不存在");
        }
        
        // 生成用户名和密码
        String username = "admin_" + UUID.randomUUID().toString().substring(0, 8);
        String password = UUID.randomUUID().toString().substring(0, 12);
        
        // 更新用户信息
        user.setRole("admin");
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userService.updateById(user);
        
        return Map.of(
            "success", true,
            "username", username,
            "password", password,
            "message", "设置管理员成功"
        );
    }
}