package com.fitness.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuthTest {

    @Test
    public void testWechatLogin() {
        // 测试微信登录功能
        System.out.println("Testing wechat login...");
    }

    @Test
    public void testAdminLogin() {
        // 测试管理员登录功能
        System.out.println("Testing admin login...");
    }

}
