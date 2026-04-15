package com.fitness.app.controller;

import com.fitness.app.entity.Template;
import com.fitness.app.entity.TemplateDay;
import com.fitness.app.entity.TemplateExercise;
import com.fitness.app.service.TemplateService;
import com.fitness.app.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @GetMapping("/list")
    public Result<?> getTemplates(Authentication authentication) {
        try {
            List<Template> templates = templateService.getPublicTemplates();
            return Result.success(templates);
        } catch (Exception e) {
            return Result.error("获取模板列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public Result<?> getTemplateDetail(@PathVariable Integer id) {
        try {
            Template template = templateService.getTemplateById(id);
            return Result.success(template);
        } catch (Exception e) {
            return Result.error("获取模板详情失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/days")
    public Result<?> getTemplateDays(@PathVariable Integer id) {
        try {
            List<TemplateDay> days = templateService.getTemplateDaysByTemplateId(id);
            return Result.success(days);
        } catch (Exception e) {
            return Result.error("获取模板训练日失败: " + e.getMessage());
        }
    }

    @GetMapping("/days/{id}/exercises")
    public Result<?> getTemplateExercises(@PathVariable Integer id) {
        try {
            List<TemplateExercise> exercises = templateService.getTemplateExercisesByTemplateDayId(id);
            return Result.success(exercises);
        } catch (Exception e) {
            return Result.error("获取模板训练动作失败: " + e.getMessage());
        }
    }
}
