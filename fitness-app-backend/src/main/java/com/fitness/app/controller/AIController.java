package com.fitness.app.controller;

import com.fitness.app.service.AIService;
import com.fitness.app.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/chat")
    public Result<?> chatWithAI(@RequestBody ChatRequest request) {
        try {
            String response = aiService.chatWithAI(request.getMessage(), request.getContext());
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("AI对话失败: " + e.getMessage());
        }
    }

    @PostMapping("/generate-plan")
    public Result<?> generateFitnessPlan(@RequestBody GeneratePlanRequest request) {
        try {
            String plan = aiService.generateFitnessPlan(request.getGoal(), request.getHealthReport());
            return Result.success(plan);
        } catch (Exception e) {
            return Result.error("生成健身计划失败: " + e.getMessage());
        }
    }

    @PostMapping("/upload-health-report")
    public Result<?> uploadHealthReport(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            byte[] bytes = file.getBytes();
            String base64File = Base64.getEncoder().encodeToString(bytes);
            return Result.success(base64File);
        } catch (IOException e) {
            return Result.error("上传文件失败: " + e.getMessage());
        }
    }

    public static class ChatRequest {
        private String message;
        private String context;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getContext() {
            return context;
        }

        public void setContext(String context) {
            this.context = context;
        }
    }

    public static class GeneratePlanRequest {
        private String goal;
        private String healthReport;

        public String getGoal() {
            return goal;
        }

        public void setGoal(String goal) {
            this.goal = goal;
        }

        public String getHealthReport() {
            return healthReport;
        }

        public void setHealthReport(String healthReport) {
            this.healthReport = healthReport;
        }
    }
}
