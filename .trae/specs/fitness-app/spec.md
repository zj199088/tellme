# 健身小程序 - 产品需求文档

## Overview
- **Summary**: 健身小程序是一款集模板生成、自定义计划、AI生成计划和训练跟踪于一体的综合健身应用。
- **Purpose**: 为用户提供便捷的健身计划管理和训练跟踪功能，降低健身门槛，提供个性化的健身解决方案。
- **Target Users**: 健身爱好者和希望开始健身的人群。

## Goals
- 提供微信用户登录和管理员登录功能
- 实现模板生成健身计划功能
- 实现自定义健身计划功能
- 实现AI生成健身计划功能
- 实现训练跟踪功能
- 实现音乐播放功能
- 实现管理端功能（运动分类和动作管理）
- 实现超级管理员设置普通管理员功能

## Non-Goals (Out of Scope)
- 社区分享功能
- 成就系统
- 多设备同步训练进度
- 计划统计和数据分析功能

## Background & Context
- 前端使用 uni-app + Vue 3 + Vite 开发
- 后端使用 Spring Boot 3.x + MyBatis Plus 3.5+
- 数据库使用 MySQL 8.0
- AI服务使用阿里百炼大模型API
- 存储使用腾讯云COS
- 认证使用 JWT + Spring Security
- 缓存使用 Redis

## Functional Requirements
- **FR-1**: 用户登录/注册 - 获取并保存微信用户信息
- **FR-2**: 模板生成健身计划 - 从预设模板创建健身计划
- **FR-3**: 自定义健身计划 - 用户自定义创建健身计划
- **FR-4**: AI生成健身计划 - 通过AI对话生成个性化计划
- **FR-5**: 训练跟踪 - 记录每日训练进度
- **FR-6**: 音乐播放 - 训练时播放背景音乐
- **FR-7**: 管理端 - 管理运动分类和动作数据
- **FR-8**: 管理员登录 - 管理员账号密码登录
- **FR-9**: 用户管理 - 超级管理员设置普通管理员

## Non-Functional Requirements
- **NFR-1**: 性能 - 页面加载时间不超过2秒
- **NFR-2**: 安全性 - 敏感数据加密存储，API接口需要认证
- **NFR-3**: 可靠性 - 系统可用性达到99.9%
- **NFR-4**: 可扩展性 - 支持水平扩展以应对用户增长
- **NFR-5**: 兼容性 - 适配不同屏幕尺寸的微信小程序

## Constraints
- **Technical**: 使用指定的技术栈，包括uni-app、Spring Boot、MySQL等
- **Business**: 预算有限，需要控制开发成本
- **Dependencies**: 依赖微信API、阿里百炼AI服务、腾讯云COS

## Assumptions
- 用户已经安装微信并拥有微信账号
- 后端服务部署在可访问互联网的服务器上
- 数据库已经正确配置并可访问
- AI服务API密钥已正确配置

## Acceptance Criteria

### AC-1: 微信用户登录
- **Given**: 用户进入小程序
- **When**: 用户点击授权登录
- **Then**: 系统获取微信用户信息并登录成功
- **Verification**: `programmatic`

### AC-2: 模板生成健身计划
- **Given**: 用户选择一个健身模板
- **When**: 用户配置计划参数并确认
- **Then**: 系统创建健身计划并显示成功
- **Verification**: `programmatic`

### AC-3: 自定义健身计划
- **Given**: 用户进入自定义计划创建页面
- **When**: 用户选择运动分类和动作，配置组数、次数
- **Then**: 系统创建自定义健身计划并显示成功
- **Verification**: `programmatic`

### AC-4: AI生成健身计划
- **Given**: 用户进入AI计划生成页面
- **When**: 用户设定目标，上传体检报告，与AI对话
- **Then**: 系统生成个性化健身计划并显示成功
- **Verification**: `programmatic`

### AC-5: 训练跟踪
- **Given**: 用户进入训练跟踪页面
- **When**: 用户完成训练并记录
- **Then**: 系统保存训练记录并更新计划进度
- **Verification**: `programmatic`

### AC-6: 音乐播放
- **Given**: 用户进入音乐播放器
- **When**: 用户选择音乐并点击播放
- **Then**: 系统播放音乐并显示播放状态
- **Verification**: `programmatic`

### AC-7: 管理员登录
- **Given**: 管理员进入登录页面
- **When**: 管理员输入正确的用户名和密码
- **Then**: 系统验证并登录成功
- **Verification**: `programmatic`

### AC-8: 运动分类管理
- **Given**: 管理员进入运动分类管理页面
- **When**: 管理员增删改查运动分类
- **Then**: 系统更新运动分类数据并显示成功
- **Verification**: `programmatic`

### AC-9: 运动动作管理
- **Given**: 管理员进入运动动作管理页面
- **When**: 管理员增删改查运动动作
- **Then**: 系统更新运动动作数据并显示成功
- **Verification**: `programmatic`

### AC-10: 超级管理员设置普通管理员
- **Given**: 超级管理员进入用户管理页面
- **When**: 超级管理员选择用户并设置为普通管理员
- **Then**: 系统更新用户角色并生成管理员账号密码
- **Verification**: `programmatic`

## Open Questions
- [ ] 阿里百炼大模型API的具体调用方式和参数
- [ ] 腾讯云COS的具体配置和文件上传实现
- [ ] 音乐文件的来源和存储方式
- [ ] 系统部署的具体环境和配置