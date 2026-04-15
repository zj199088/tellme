# 健身小程序管理端使用说明

## 功能说明

本项目实现了以下管理端功能：

1. **运动分类管理**：增删改查运动分类
2. **运动动作管理**：增删改查运动动作
3. **用户管理**：超级管理员设置普通管理员

## 技术栈

- 前端：Vue 3 + Vite + Vue Router + Axios
- 后端：Spring Boot 3 + MyBatis Plus + Spring Security + JWT
- 数据库：SQLite

## 管理员登录

1. 访问管理端登录页面：http://localhost:5175/pages/admin/auth
2. 使用超级管理员账号登录：
   - 用户名：admin
   - 密码：123456

## 功能使用

### 1. 运动分类管理
- 路径：/pages/admin/categories
- 功能：
  - 查看所有运动分类
  - 添加新分类（支持设置父分类）
  - 编辑现有分类
  - 删除分类

### 2. 运动动作管理
- 路径：/pages/admin/exercises
- 功能：
  - 查看所有运动动作
  - 添加新动作（选择所属分类）
  - 编辑现有动作
  - 删除动作

### 3. 用户管理
- 路径：/pages/admin/users
- 功能：
  - 查看所有用户列表
  - 将普通用户设置为管理员
  - 自动生成管理员账号和密码

## 后端API接口

### 认证相关
- `POST /api/auth/admin/login`：管理员登录

### 运动分类管理
- `GET /api/admin/categories`：获取分类列表
- `POST /api/admin/categories`：创建分类
- `PUT /api/admin/categories/{id}`：更新分类
- `DELETE /api/admin/categories/{id}`：删除分类

### 运动动作管理
- `GET /api/admin/exercises`：获取动作列表
- `POST /api/admin/exercises`：创建动作
- `PUT /api/admin/exercises/{id}`：更新动作
- `DELETE /api/admin/exercises/{id}`：删除动作

### 用户管理
- `GET /api/admin/users`：获取用户列表
- `POST /api/admin/users/set-admin`：设置用户为管理员

## 注意事项

1. 超级管理员账号默认已创建，密码为加密存储
2. 普通管理员只能管理运动分类和动作，不能设置其他管理员
3. 所有API接口都需要JWT token认证
4. 数据库使用SQLite，文件名为fitness_app.db

## 启动项目

### 后端
```bash
cd fitness-app-backend
mvn spring-boot:run
```

### 前端
```bash
cd fitness-app-frontend
npm install
npm run dev
```

## 构建项目

### 前端
```bash
cd fitness-app-frontend
npm run build
```

构建后的文件将位于 `dist` 目录中。