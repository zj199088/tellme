package com.fitness.app.controller;

import com.fitness.app.entity.Template;
import com.fitness.app.entity.TemplateDay;
import com.fitness.app.entity.TemplateExercise;
import com.fitness.app.service.TemplateService;
import com.fitness.app.service.FitnessPlanService;
import com.fitness.app.dto.CreatePlanRequest;
import com.fitness.app.dto.CreatePlanResponse;
import com.fitness.app.dto.GetTemplatesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private FitnessPlanService fitnessPlanService;

    @GetMapping("/list")
    public GetTemplatesResponse getTemplates(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<Template> templates = templateService.getPublicTemplates();
        return new GetTemplatesResponse(templates);
    }

    @GetMapping("/detail/{id}")
    public Template getTemplateDetail(@PathVariable Long id) {
        return templateService.getTemplateById(id);
    }

    @GetMapping("/{id}/days")
    public List<TemplateDay> getTemplateDays(@PathVariable Long id) {
        return templateService.getTemplateDaysByTemplateId(id);
    }

    @GetMapping("/days/{id}/exercises")
    public List<TemplateExercise> getTemplateExercises(@PathVariable Long id) {
        return templateService.getTemplateExercisesByTemplateDayId(id);
    }

    @PostMapping("/create-plan")
    public CreatePlanResponse createPlan(@RequestBody CreatePlanRequest request, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = Long.parseLong(userDetails.getUsername());
        Long planId = fitnessPlanService.createPlanFromTemplate(userId, request);
        return new CreatePlanResponse(planId, true);
    }
}
