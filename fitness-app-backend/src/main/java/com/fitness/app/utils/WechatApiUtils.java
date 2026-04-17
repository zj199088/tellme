package com.fitness.app.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class WechatApiUtils {

    @Value("${wechat.appId}")
    private String appId;

    @Value("${wechat.appSecret}")
    private String appSecret;

    @Value("${wechat.apiUrl}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public WechatApiUtils() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 通过code获取微信openId
     * @param code 微信登录code
     * @return openId
     * @throws Exception 异常信息
     */
    public String getOpenIdByCode(String code) throws Exception {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("code cannot be empty");
        }

        // 构建请求参数
        Map<String, String> params = new HashMap<>();
        params.put("appid", appId);
        params.put("secret", appSecret);
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");

        // 构建请求URL
        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?appid=").append(appId);
        urlBuilder.append("&secret=").append(appSecret);
        urlBuilder.append("&js_code=").append(code);
        urlBuilder.append("&grant_type=authorization_code");

        String url = urlBuilder.toString();

        // 重试机制
        int maxRetries = 3;
        int retryCount = 0;
        long retryDelay = 1000; // 1秒

        while (retryCount < maxRetries) {
            try {
                // 调用微信接口
                String response = restTemplate.getForObject(url, String.class);
                
                // 解析响应
                JSONObject jsonObject = JSONObject.parseObject(response);
                
                // 检查是否有错误
                if (jsonObject.containsKey("errcode")) {
                    int errCode = jsonObject.getIntValue("errcode");
                    if (errCode != 0) {
                        String errMsg = jsonObject.getString("errmsg");
                        throw new Exception("Wechat API error: " + errCode + " - " + errMsg);
                    }
                }
                
                // 获取openId
                String openId = jsonObject.getString("openid");
                if (openId == null || openId.isEmpty()) {
                    throw new Exception("Failed to get openId from wechat API");
                }
                
                return openId;
                
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw new Exception("Failed to call wechat API after " + maxRetries + " retries: " + e.getMessage());
                }
                // 等待后重试
                try {
                    TimeUnit.MILLISECONDS.sleep(retryDelay);
                    retryDelay *= 2; // 指数退避
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new Exception("Retry interrupted: " + ie.getMessage());
                }
            } catch (Exception e) {
                throw e;
            }
        }

        throw new Exception("Failed to get openId after max retries");
    }
}
