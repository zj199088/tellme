# API URL 修复 - 产品需求文档

## Overview
- **Summary**: 修改前端API配置，将baseURL更新为包含/api路径，并移除所有接口调用中的/api前缀，以匹配后端的context-path配置
- **Purpose**: 确保前端API调用路径与后端服务器配置一致，避免404错误
- **Target Users**: 前端开发人员和系统维护人员

## Goals
- 更新baseURL为http://localhost:8080/api
- 移除所有接口调用中的/api前缀
- 确保所有API调用路径正确映射到后端接口
- 验证修改后的API调用正常工作

## Non-Goals (Out of Scope)
- 修改后端API实现
- 改变API功能逻辑
- 添加新的API接口

## Background & Context
- 后端服务器配置了servlet context-path=/api
- 前端当前baseURL为http://localhost:8080，且接口调用中包含/api前缀
- 这导致实际请求路径为http://localhost:8080/api/api/...，造成404错误

## Functional Requirements
- **FR-1**: 更新baseURL配置为http://localhost:8080/api
- **FR-2**: 移除所有接口调用路径中的/api前缀
- **FR-3**: 更新响应拦截器中的URL判断逻辑

## Non-Functional Requirements
- **NFR-1**: 所有API调用路径必须与后端配置一致
- **NFR-2**: 修改后所有API调用应正常工作
- **NFR-3**: 代码修改应保持清晰可维护

## Constraints
- **Technical**: 必须保持与后端服务器配置一致
- **Dependencies**: 依赖后端服务器的context-path配置

## Assumptions
- 后端服务器已正确配置context-path=/api
- 所有API接口路径在移除/api前缀后能正确映射到后端接口

## Acceptance Criteria

### AC-1: baseURL配置更新
- **Given**: 前端应用启动
- **When**: 检查API客户端配置
- **Then**: baseURL应设置为http://localhost:8080/api
- **Verification**: `programmatic`

### AC-2: 接口路径前缀移除
- **Given**: 查看api.ts文件
- **When**: 检查所有API调用路径
- **Then**: 所有路径不应包含/api前缀
- **Verification**: `programmatic`

### AC-3: 响应拦截器逻辑更新
- **Given**: 查看响应拦截器配置
- **When**: 检查URL判断逻辑
- **Then**: 应使用更新后的路径格式
- **Verification**: `programmatic`

### AC-4: API调用正常工作
- **Given**: 前端应用运行
- **When**: 调用任意API接口
- **Then**: 应返回正确响应，无404错误
- **Verification**: `programmatic`

## Open Questions
- [ ] 后端是否所有接口都已正确配置在/api路径下
- [ ] 是否有其他地方也需要修改API路径配置