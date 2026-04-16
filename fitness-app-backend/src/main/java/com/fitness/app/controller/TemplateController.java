package com.fitness.app.controller;

import com.fitness.app.entity.Template;
import com.fitness.app.entity.TemplateDay;
import com.fitness.app.entity.TemplateExercise;
import com.fitness.app.service.TemplateService;
import com.fitness.app.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/templates")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @GetMapping("/list")
    public Result<?> getTemplates(Authentication authentication) {
        log.info("获取公开模板列表");
        List<Template> templates = templateService.getPublicTemplates();
        log.info("获取公开模板列表成功，数量: {}", templates.size());
        return Result.success(templates);
    }

    @GetMapping("/user")
    public Result<?> getUserTemplates(Authentication authentication) {
        Integer userId = Integer.parseInt(authentication.getName());
        log.info("获取用户模板列表: userId={}", userId);
        List<Template> templates = templateService.getUserAndPublicTemplates(userId);
        log.info("获取用户模板列表成功: userId={}, 数量: {}", userId, templates.size());
        return Result.success(templates);
    }

    @GetMapping("/detail/{id}")
    public Result<?> getTemplateDetail(@PathVariable Integer id) {
        log.info("获取模板详情: templateId={}", id);
        Template template = templateService.getTemplateById(id);
        log.info("获取模板详情成功: templateId={}", id);
        return Result.success(template);
    }

    @GetMapping("/{id}/days")
    public Result<?> getTemplateDays(@PathVariable Integer id) {
        log.info("获取模板训练日: templateId={}", id);
        List<TemplateDay> days = templateService.getTemplateDaysByTemplateId(id);
        log.info("获取模板训练日成功: templateId={}, 数量: {}", id, days.size());
        return Result.success(days);
    }

    @GetMapping("/days/{id}/exercises")
    public Result<?> getTemplateExercises(@PathVariable Integer id) {
        log.info("获取模板训练动作: templateDayId={}", id);
        List<TemplateExercise> exercises = templateService.getTemplateExercisesByTemplateDayId(id);
        log.info("获取模板训练动作成功: templateDayId={}, 数量: {}", id, exercises.size());
        return Result.success(exercises);
    }
}
