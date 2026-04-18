# 健身应用部署文档

## 1. 前端部署

### 1.1 环境要求
- Node.js 16+
- npm 7+
- 微信开发者工具（可选，用于小程序打包）

### 1.2 环境配置

#### 1.2.1 应用模式配置
应用支持两种运行模式，通过环境变量配置：

1. **创建环境变量文件**
   复制 `.env.example` 文件为 `.env`：
   ```bash
   cd fitness-app-frontend
   cp .env.example .env
   ```

2. **配置应用模式**
   编辑 `.env` 文件，设置运行模式：
   ```env
   # 应用模式配置
   # 可选值: web | miniprogram
   # - web: 标准网页模式（默认），保持现有功能不变
   # - miniprogram: 小程序模式，用于适配微信小程序、支付宝小程序等运行环境
   VITE_APP_MODE=web

   # API 服务地址
   VITE_API_URL=http://localhost:8080
   ```

3. **模式说明**
   - **web 模式**：显示账号密码登录界面，支持用户注册
   - **miniprogram 模式**：显示微信授权登录按钮，集成微信登录流程

#### 1.2.2 开发环境配置
1. **安装依赖**
   ```bash
   cd fitness-app-frontend
   npm install
   ```

2. **配置 API 地址**
   编辑 `.env` 文件，修改 API 基础地址：
   ```env
   VITE_API_URL=http://localhost:8080
   ```

3. **启动开发服务器**
   ```bash
   npm run dev
   ```
   开发服务器默认运行在 `http://localhost:5173`

#### 1.2.3 测试环境配置
1. **配置 API 地址和应用模式**
   编辑 `.env` 文件，修改 API 基础地址和应用模式：
   ```env
   VITE_APP_MODE=web
   VITE_API_URL=http://test-server:8080
   ```

2. **构建测试版本**
   ```bash
   npm run build
   ```
   构建产物将生成在 `dist` 目录

### 1.3 生产环境构建
1. **构建生产版本**
   ```bash
   npm run build
   ```
   构建产物将生成在 `dist` 目录

2. **部署静态文件**
   将 `dist` 目录中的文件部署到静态文件服务器（如 Nginx、Apache 等）

### 1.4 微信开发者工具打包
1. **安装微信小程序编译插件**
   由于项目已更新到 Vite 6+，现在可以使用专门的微信小程序编译插件：
   ```bash
   npm install -D vite-plugin-wechat-mp
   ```

2. **修改 Vite 配置**
   创建或修改 `vite.config.ts` 文件：
   ```typescript
   import { defineConfig } from 'vite'
   import vue from '@vitejs/plugin-vue'
   import VitePluginWechatMp from 'vite-plugin-wechat-mp'

   export default defineConfig({
     plugins: [vue(), VitePluginWechatMp()]
   })
   ```

3. **构建微信小程序版本**
   ```bash
   npm run build
   ```

4. **在微信开发者工具中打开**
   - 打开微信开发者工具
   - 选择「小程序」→「导入项目」
   - 选择 `dist` 目录
   - 填写 AppID（如果没有可以使用测试号）
   - 点击「导入」

5. **（可选）使用微信开发者工具自动打开插件**
   如果你希望构建后自动打开微信开发者工具：
   ```bash
   npm install -D vite-plugin-open-wechat-devtools
   ```

   **修改 Vite 配置**
   ```typescript
   import { defineConfig } from 'vite'
   import vue from '@vitejs/plugin-vue'
   import VitePluginWechatMp from 'vite-plugin-wechat-mp'
   import openWechatDevtools from 'vite-plugin-open-wechat-devtools'

   export default defineConfig({
     plugins: [
       vue(),
       VitePluginWechatMp(),
       openWechatDevtools({
         projectPath: './dist'
       })
     ]
   })
   ```

## 2. 后端部署

### 2.1 环境要求
- JDK 11+
- Maven 3.6+
- MySQL 5.7+ 或 8.0+
- Redis（可选，用于缓存）

### 2.2 配置文件
编辑 `src/main/resources/application.yml` 文件：

```yaml
spring:
  application:
    name: fitness-app
  datasource:
    url: jdbc:mysql://localhost:3306/fitness_app?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: 123456
  redis:
    host: localhost
    port: 6379
    password:
    database: 0
  security:
    jwt:
      secret: your-secret-key
      expiration: 86400

mybatis-plus:
  mapper-locations: classpath:mapper/**/*.xml
  type-aliases-package: com.fitness.app.entity
  configuration:
    map-underscore-to-camel-case: true

server:
  port: 8080
  servlet:
    context-path: /api

# 微信开放平台配置
wechat:
  appId: your_wechat_app_id          # 微信小程序 AppID
  appSecret: your_wechat_app_secret  # 微信小程序 AppSecret
  apiUrl: https://api.weixin.qq.com/sns/jscode2session
```

#### 2.2.1 微信开放平台配置
1. **获取微信小程序 AppID 和 AppSecret**
   - 访问 [微信公众平台](https://mp.weixin.qq.com/)
   - 注册/登录账号
   - 创建小程序或使用已有小程序
   - 在「开发」→「开发管理」→「开发设置」中获取 AppID 和 AppSecret

2. **配置微信参数**
   编辑 `application.yml` 文件，填入获取到的 AppID 和 AppSecret：
   ```yaml
   wechat:
     appId: wx1234567890abcdef        # 替换为实际的 AppID
     appSecret: abcdef1234567890abcdef1234567890  # 替换为实际的 AppSecret
     apiUrl: https://api.weixin.qq.com/sns/jscode2session
   ```

3. **微信登录流程说明**
   - 前端：调用 `wx.login()` 获取 code
   - 后端：使用 code 调用微信接口 `https://api.weixin.qq.com/sns/jscode2session` 获取 openId 和 session_key
   - 后端：使用 openId 创建或更新用户，生成 JWT token 返回给前端

### 2.3 数据库初始化

1. **创建数据库**
   ```bash
   mysql -u root -p
   CREATE DATABASE fitness_app CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **执行初始化脚本**
   初始化脚本位于 `src/main/resources/init.sql` 文件中，执行以下命令：
   ```bash
   mysql -u root -p fitness_app < src/main/resources/init.sql
   ```

**预置用户**：
- **管理员用户**：
  - 用户名：admin
  - 密码：admin123
  - 角色：super_admin

- **普通用户**：
  - 用户名：user
  - 密码：user123
  - 角色：user

### 2.4 构建和启动
1. **构建项目**
   ```bash
   cd fitness-app-backend
   mvn clean package -DskipTests
   ```

2. **启动服务**
   ```bash
   java -jar target/fitness-app-1.0.0.jar
   ```

   服务默认运行在 `http://localhost:8080/api`

3. **访问 MySQL 数据库**
   使用 MySQL 客户端工具（如 MySQL Workbench、Navicat 等）连接数据库：
   -  Host: localhost
   -  Port: 3306
   -  Database: fitness_app
   -  Username: root
   -  Password: 123456

## 3. 完整部署流程

### 3.1 本地开发环境部署
1. **启动后端服务**
   ```bash
   cd fitness-app-backend
   mvn spring-boot:run
   ```

2. **启动前端开发服务器**
   ```bash
   cd fitness-app-frontend
   npm run dev
   ```

3. **访问应用**
   打开浏览器访问 `http://localhost:5173`

### 3.2 测试环境部署
1. **构建后端项目**
   ```bash
   cd fitness-app-backend
   mvn clean package -DskipTests
   ```

2. **部署后端服务**
   ```bash
   java -jar target/fitness-app-1.0.0.jar
   ```

3. **构建前端项目**
   ```bash
   cd fitness-app-frontend
   npm run build
   ```

4. **部署前端静态文件**
   将 `dist` 目录部署到静态文件服务器

### 3.3 微信小程序部署
1. **构建微信小程序版本**
   ```bash
   cd fitness-app-frontend
   npm run build -- --mode wechat
   ```

2. **在微信开发者工具中导入**
   - 打开微信开发者工具
   - 选择「小程序」→「导入项目」
   - 选择 `dist` 目录
   - 填写 AppID
   - 点击「导入」

3. **上传和发布**
   - 在微信开发者工具中点击「上传」
   - 在微信公众平台中审核和发布

## 4. 常见问题和解决方案

### 4.1 前端问题
- **问题**：API 连接失败
  **解决方案**：检查后端服务是否启动，API 地址是否正确配置

- **问题**：构建失败
  **解决方案**：检查 Node.js 版本，删除 `node_modules` 目录后重新安装依赖

### 4.2 后端问题
- **问题**：端口被占用
  **解决方案**：修改 `application.yml` 文件中的 `server.port` 配置

- **问题**：数据库连接失败
  **解决方案**：检查 MySQL 数据库配置，确保 MySQL 服务正在运行，数据库已创建，初始化脚本已执行

### 4.3 微信小程序问题
- **问题**：导入失败
  **解决方案**：确保微信开发者工具版本正确，检查 `dist` 目录是否生成

- **问题**：API 域名未配置
  **解决方案**：在微信公众平台中配置服务器域名

## 5. 部署验证

### 5.1 前端验证
1. **开发环境**：访问 `http://localhost:5173`，检查页面是否正常加载
2. **生产环境**：访问部署的静态文件服务器，检查页面是否正常加载
3. **微信小程序**：在微信开发者工具中预览，检查功能是否正常

### 5.2 后端验证
1. **API 测试**：访问 `http://localhost:8080/api/plans/user`，检查是否返回数据
2. **数据库测试**：使用 MySQL 客户端工具连接数据库，检查数据库表结构和数据
3. **用户登录**：使用预置的用户名和密码登录系统，检查是否登录成功
4. **微信登录接口测试**：
   - 测试微信登录接口 `/api/auth/wechat`
   - 验证微信接口调用是否正常
   - 验证错误处理是否完善

### 5.3 完整功能验证
1. **创建计划**：尝试创建一个健身计划
2. **跟踪训练**：尝试开始训练并记录
3. **AI 智能定制**：尝试使用 AI 生成健身计划
4. **个人中心**：查看个人信息和统计数据
5. **微信登录验证**：
   - 在 web 模式下测试账号密码登录
   - 在 miniprogram 模式下测试微信授权登录
   - 验证登录状态持久化
   - 验证错误提示是否友好

## 6. 安全注意事项

### 6.1 敏感信息处理
- **JWT 密钥**：在生产环境中使用强密钥，并定期更换
- **数据库密码**：不要在配置文件中明文存储密码
- **API 密钥**：保护好第三方服务的 API 密钥
- **微信 AppSecret**：
  - 严格保护微信 AppSecret，不要提交到版本控制系统
  - 生产环境使用环境变量或密钥管理服务存储
  - 定期更换 AppSecret 以提高安全性
  - 不要在前端代码中暴露 AppSecret

### 6.2 部署建议
- **生产环境**：使用 HTTPS 协议
- **数据库**：使用生产级 MySQL 配置，开启密码验证和权限控制
- **服务器**：使用防火墙和安全组保护服务器
- **定期备份**：定期备份 MySQL 数据库和配置文件

## 7. 附录

### 7.1 技术栈
- **前端**：Vue 3, Vite, Vue Router, Axios
- **后端**：Spring Boot, MyBatis-Plus, MySQL, Redis
- **构建工具**：Maven, npm

### 7.2 项目结构
- **前端**：`/workspace/fitness-app-frontend`
- **后端**：`/workspace/fitness-app-backend`

### 7.3 关键文件
- **前端**：
  - `src/utils/api.ts`：API 配置
  - `vite.config.ts`：构建配置
  - `package.json`：依赖配置

- **后端**：
  - `src/main/resources/application.yml`：应用配置
  - `src/main/resources/init.sql`：数据库初始化脚本
  - `pom.xml`：Maven 配置