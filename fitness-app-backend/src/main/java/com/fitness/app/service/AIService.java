package com.fitness.app.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AIService {

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    @Value("${ai.service.apiKey}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateFitnessPlan(String goal, String healthReport) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "ep-20260415164055-kx78l");
        requestBody.put("temperature", 0.7);

        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是一个专业的健身教练，根据用户的健身目标和体检报告，生成个性化的健身计划。计划应该包括：目标、每周训练安排、每个训练日的具体内容（包括动作、组数、次数、重量建议）、饮食建议和注意事项。请使用中文回复。");

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", "我的健身目标是：" + goal + "。我的体检报告：" + healthReport);

        requestBody.put("messages", new Object[]{systemMessage, userMessage});

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                aiServiceUrl + "/api/v3/chat/completions",
                HttpMethod.POST,
                entity,
                String.class
        );

        JSONObject jsonResponse = JSON.parseObject(response.getBody());
        return jsonResponse.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }

    public String chatWithAI(String message, String context) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "ep-20260415164055-kx78l");
        requestBody.put("temperature", 0.7);

        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是一个专业的健身教练，回答用户的健身相关问题，提供专业的建议和指导。请使用中文回复。");

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", context + "\n" + message);

        requestBody.put("messages", new Object[]{systemMessage, userMessage});

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                aiServiceUrl + "/api/v3/chat/completions",
                HttpMethod.POST,
                entity,
                String.class
        );

        JSONObject jsonResponse = JSON.parseObject(response.getBody());
        return jsonResponse.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }
}