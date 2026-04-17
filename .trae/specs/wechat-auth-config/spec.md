# 微信小程序认证配置 - 产品需求文档

## Overview
- **Summary**: 在前端添加配置功能，支持通过配置切换小程序模式和普通前端模式，小程序模式下需要微信授权登录，普通前端模式使用账号密码登录
- **Purpose**: 解决应用需要同时支持小程序和Web端的场景，通过配置灵活切换登录认证方式，无需修改代码
- **Target Users**: 健身应用的最终用户（健身爱好者）、开发者、运维人员

## Goals
- 支持在前端配置文件中设置应用运行模式（小程序模式/普通前端模式）
- 小程序模式下，使用微信授权登录（对接现有微信登录API）
- 普通前端模式下，使用现有的账号密码登录
- 登录方式与配置项严格对应，无用户混淆
- 配置变更无需修改代码，通过环境变量或配置文件实现

## Non-Goals (Out of Scope)
- 不修改后端现有的登录认证接口
- 不实现新的登录方式（如手机号验证码登录）
- 不修改用户表结构或数据库
- 不实现微信支付或其他小程序特有功能
- 不实现微信小程序开发的完整框架集成

## Background & Context
- 当前项目已经包含完整的账号密码登录系统和后端支持
- 后端已具备微信登录接口（POST /auth/wechat）
- 用户表已包含open_id、nickname、avatar_url等微信相关字段
- 前端使用Vue 3 + Vite技术栈
- 项目已有基本的环境变量配置机制（.env.example文件）

## Functional Requirements
- **FR-1**: 添加运行模式配置项，支持 "miniprogram" 和 "web" 两种值
- **FR-2**: 小程序模式下，登录页面显示微信授权登录按钮
- **FR-3**: 普通前端模式下，登录页面显示账号密码登录表单
- **FR-4**: 小程序模式下，调用微信登录API并处理响应
- **FR-5**: 两种模式都需要将登录token保存到localStorage中
- **FR-6**: 配置变更后重启应用即可生效

## Non-Functional Requirements
- **NFR-1**: 配置项简单直观，易于理解和修改
- **NFR-2**: 页面切换流畅，无明显卡顿
- **NFR-3**: 代码结构清晰，易于维护和扩展
- **NFR-4**: 保持现有功能的稳定性，不影响正常使用

## Constraints
- **Technical**: 必须基于现有的Vue 3 + Vite技术栈实现
- **Business**: 配置变更必须在开发阶段完成，生产环境变更需遵循部署流程
- **Dependencies**: 依赖现有的后端API和用户表结构

## Assumptions
- 微信小程序环境会提供wx.login()等API
- 后端微信登录接口(/auth/wechat)已正常工作
- 用户表结构无需修改即可支持两种登录方式
- 两种模式下登录成功后的用户体验保持一致

## Acceptance Criteria

### AC-1: 配置项功能
- **Given**: 前端项目存在环境配置文件
- **When**: 在配置文件中设置 VITE_APP_MODE=miniprogram 或 VITE_APP_MODE=web
- **Then**: 应用启动后能正确读取并使用该配置
- **Verification**: `programmatic`

### AC-2: 小程序模式登录页面
- **Given**: 配置设置为小程序模式（VITE_APP_MODE=miniprogram）
- **When**: 用户访问登录页面
- **Then**: 显示微信授权登录按钮，不显示账号密码表单
- **Verification**: `human-judgment`

### AC-3: 普通前端模式登录页面
- **Given**: 配置设置为普通前端模式（VITE_APP_MODE=web）
- **When**: 用户访问登录页面
- **Then**: 显示账号密码登录表单，不显示微信登录按钮
- **Verification**: `human-judgment`

### AC-4: 微信登录流程
- **Given**: 配置为小程序模式，用户点击微信登录按钮
- **When**: 完成微信授权并调用后端微信登录接口
- **Then**: 接口返回token并保存到localStorage，跳转至首页
- **Verification**: `programmatic`

### AC-5: 账号密码登录流程
- **Given**: 配置为普通前端模式，用户输入有效账号密码
- **When**: 点击登录按钮并调用后端登录接口
- **Then**: 接口返回token并保存到localStorage，跳转至首页
- **Verification**: `programmatic`

### AC-6: 登录状态持久化
- **Given**: 用户已成功登录（无论是微信还是账号密码）
- **When**: 刷新页面或重新打开应用
- **Then**: 保持登录状态，无需重新登录
- **Verification**: `human-judgment`

## Open Questions
- [ ] 微信小程序环境是否需要特殊的API封装或polyfill？
- [ ] 是否需要同时支持两种登录方式（在小程序模式下也允许账号密码登录）？
- [ ] 是否需要在运行时动态切换模式，还是仅在启动时读取配置？
