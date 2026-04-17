package com.fitness.app.controller;

import com.fitness.app.common.Result;
import com.fitness.app.entity.User;
import com.fitness.app.service.UserService;
import com.fitness.app.service.WorkoutRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private WorkoutRecordService workoutRecordService;

    @GetMapping("/info")
    public Result<?> getUserInfo(Authentication authentication) {
        Integer userId = Integer.parseInt(authentication.getName());
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 构建用户信息响应
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("nickname", user.getNickname() != null ? user.getNickname() : user.getUsername());
        userInfo.put("avatarUrl", user.getAvatarUrl());
        userInfo.put("gender", user.getGender());
        userInfo.put("country", user.getCountry());
        userInfo.put("province", user.getProvince());
        userInfo.put("city", user.getCity());
        userInfo.put("fitnessGoals", user.getFitnessGoals());
        userInfo.put("fitnessLevel", user.getFitnessLevel());
        userInfo.put("height", user.getHeight());
        userInfo.put("weight", user.getWeight());

        return Result.success(userInfo);
    }

    @GetMapping("/stats")
    public Result<?> getFitnessStats(Authentication authentication) {
        Integer userId = Integer.parseInt(authentication.getName());

        // 计算健身统计数据
        Map<String, Object> stats = new HashMap<>();

        // 1. 已完成的训练次数
        int completedWorkouts = workoutRecordService.countAllRecords(userId);
        stats.put("completedWorkouts", completedWorkouts);

        // 2. 计算卡路里消耗（假设每个训练记录平均消耗100卡路里）
        int caloriesBurned = completedWorkouts * 100;
        stats.put("caloriesBurned", caloriesBurned);

        // 3. 计算训练时长（假设每个训练记录平均持续30分钟）
        double totalDuration = completedWorkouts * 0.5;
        stats.put("totalDuration", totalDuration);

        // 4. 计算完成率（假设总计划数为10）
        int completionRate = completedWorkouts > 0 ? Math.min(100, (completedWorkouts * 100) / 10) : 0;
        stats.put("completionRate", completionRate);

        // 5. 计算用户等级（每完成5次训练升1级）
        int level = (completedWorkouts / 5) + 1;
        stats.put("level", level);

        // 6. 计算等级进度
        int levelProgress = (completedWorkouts % 5) * 20;
        stats.put("levelProgress", levelProgress);

        return Result.success(stats);
    }
}
