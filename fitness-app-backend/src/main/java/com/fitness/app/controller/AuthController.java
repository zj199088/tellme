package com.fitness.app.controller;

import com.fitness.app.common.Result;
import com.fitness.app.entity.User;
import com.fitness.app.service.UserService;
import com.fitness.app.utils.JwtUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private UserService userService;

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private AuthenticationManager authenticationManager;

    @PostMapping("/wechat")
    public Result<Map<String, Object>> wechatLogin(@RequestBody Map<String, Object> request) {
        log.info("微信登录请求: openId={}", request.get("openId"));
        
        String openId = (String) request.get("openId");
        String nickname = (String) request.get("nickname");
        String avatarUrl = (String) request.get("avatarUrl");
        Integer gender = (Integer) request.get("gender");
        String country = (String) request.get("country");
        String province = (String) request.get("province");
        String city = (String) request.get("city");

        User user = new User();
        user.setOpenId(openId);
        user.setNickname(nickname);
        user.setAvatarUrl(avatarUrl);
        user.setGender(gender);
        user.setCountry(country);
        user.setProvince(province);
        user.setCity(city);

        user = userService.createOrUpdateWechatUser(user);
        String token = jwtUtils.generateToken(user.getId(), user.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("open_id", user.getOpenId());
        response.put("nickname", user.getNickname());
        response.put("avatar_url", user.getAvatarUrl());
        response.put("token", token);
        response.put("role", user.getRole());

        log.info("微信登录成功: userId={}", user.getId());
        return Result.success(response);
    }

    @PostMapping("/admin/login")
    public Result<Map<String, Object>> adminLogin(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        log.info("管理员登录请求: username={}", username);
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.get("password"))
        );

        User user = userService.findByUsername(username);
        String token = jwtUtils.generateToken(user.getId(), user.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("token", token);
        response.put("role", user.getRole());
        response.put("message", "登录成功");

        log.info("管理员登录成功: username={}", username);
        return Result.success(response);
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        log.info("用户登录请求: username={}", username);
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.get("password"))
        );

        User user = userService.findByUsername(username);
        String token = jwtUtils.generateToken(user.getId(), user.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("token", token);
        response.put("role", user.getRole());
        response.put("message", "登录成功");

        log.info("用户登录成功: username={}", username);
        return Result.success(response);
    }

    @PostMapping("/logout")
    public Result<Map<String, Object>> logout() {
        log.info("用户登出请求");
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logout successful");
        return Result.success(response);
    }
}