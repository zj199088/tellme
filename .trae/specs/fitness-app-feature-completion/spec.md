# 健身应用功能完善 - 产品需求文档

## Overview
- **Summary**: 完善健身应用的核心功能模块，包括跟踪练习功能、首页继续锻炼按钮、自定义计划和AI智能计划功能、训练记录展示优化，以及全面的功能检查
- **Purpose**: 确保健身应用的所有核心功能按设计文档100%完成，提供完整的用户健身体验
- **Target Users**: 所有使用健身应用的用户，包括健身爱好者、初学者和专业健身人士

## Goals
- 实现完整的跟踪练习功能模块
- 在首页添加"继续锻炼"按钮，方便用户快速进入当前计划的训练
- 完成自定义计划功能，允许用户创建个性化健身计划
- 完成AI智能计划功能，提供AI驱动的个性化健身计划生成
- 优化训练记录展示，关联查询workout_records表，显示计划、时间、动作、动作时间信息
- 全面检查前后端代码，确保功能按设计文档100%完成

## Non-Goals (Out of Scope)
- 不修改数据库表结构（除非现有结构完全不支持功能）
- 不添加新的外部依赖库
- 不改变现有的UI设计风格（保持科技未来风格）
- 不修改现有的用户认证系统

## Background & Context
- 前端使用 Vue 3 + Vite + TypeScript 开发
- 后端使用 Spring Boot + MyBatis Plus 开发
- 数据库使用 MySQL
- 已有完整的数据库设计和初始化脚本
- 已有科技未来风格的UI设计
- 部分功能已实现，但核心功能模块需要完善

## Functional Requirements
- **FR-1**: 跟踪练习功能 - 实现完整的跟踪练习功能，包括显示当前训练内容、记录每组完成情况、完成训练后生成记录
- **FR-2**: 首页继续锻炼按钮 - 在首页的"我的练习"区域，为首个计划添加"继续锻炼"按钮，点击后进入跟踪练习页面
- **FR-3**: 自定义计划功能 - 完成自定义计划创建流程，包括选择运动动作、设置训练参数、保存计划
- **FR-4**: AI智能计划功能 - 完成AI生成计划功能，包括目标设定、AI对话、计划生成和保存
- **FR-5**: 训练记录展示优化 - 优化训练记录页面，关联查询workout_records表，显示计划名、时间、动作、动作时间等信息
- **FR-6**: 功能完整性检查 - 全面检查前后端代码，确保所有功能按设计文档100%实现

## Non-Functional Requirements
- **NFR-1**: 性能 - 页面加载时间不超过2秒，操作响应时间不超过500ms
- **NFR-2**: 兼容性 - 兼容主流现代浏览器（Chrome, Firefox, Safari, Edge）
- **NFR-3**: 可访问性 - 保持良好的可访问性，确保文字对比度符合WCAG标准
- **NFR-4**: 可维护性 - 保持代码的可维护性和可扩展性，添加必要的注释
- **NFR-5**: 用户体验 - 操作流程顺畅，反馈及时，错误提示清晰

## Constraints
- **Technical**: 必须使用现有的技术栈（Vue 3, Vite, TypeScript, Spring Boot, MyBatis Plus, MySQL）
- **Business**: 需要在较短时间内完成，不影响现有功能使用
- **Dependencies**: 依赖现有的API接口和数据结构

## Assumptions
- 现有数据库表结构可以支持所有功能需求
- 后端API框架可以正常工作
- 前端路由和组件结构可以支持新功能的添加
- 用户已登录并具有有效的会话
- 模拟数据在测试环境下可用

## Acceptance Criteria

### AC-1: 跟踪练习功能
- **Given**: 用户有一个活跃的健身计划
- **When**: 用户点击"继续锻炼"按钮进入跟踪练习页面
- **Then**: 页面显示今天的训练内容，包括训练日、动作列表、每组要求
- **Verification**: `human-judgment`
- **Notes**: 根据星期几从模板中获取对应训练内容

### AC-2: 记录训练进度
- **Given**: 用户正在进行跟踪练习
- **When**: 用户勾选完成每组练习
- **Then**: 系统记录每组的完成状态，支持多选和取消
- **Verification**: `programmatic`
- **Notes**: 记录应保存在前端状态中，直到完成训练

### AC-3: 完成训练
- **Given**: 用户完成所有组的练习
- **When**: 用户点击"完成训练"按钮
- **Then**: 系统生成workout_schedules记录（每天一条）和workout_records记录（每个动作一条）
- **Verification**: `programmatic`
- **Notes**: 关联查询验证数据是否正确保存

### AC-4: 首页继续锻炼按钮
- **Given**: 用户打开首页，有至少一个活跃计划
- **When**: 页面加载完成
- **Then**: 首个计划卡片显示"继续锻炼"按钮
- **Verification**: `human-judgment`

### AC-5: 点击继续锻炼
- **Given**: 用户在首页看到"继续锻炼"按钮
- **When**: 用户点击"继续锻炼"按钮
- **Then**: 页面跳转到跟踪练习页面，显示对应计划的训练内容
- **Verification**: `programmatic`

### AC-6: 自定义计划页面
- **Given**: 用户点击计划页面的"自定义计划"按钮
- **When**: 页面加载完成
- **Then**: 显示自定义计划创建页面，包含运动分类、动作选择、参数设置
- **Verification**: `human-judgment`

### AC-7: 选择运动动作
- **Given**: 用户在自定义计划页面
- **When**: 用户选择运动分类，浏览并选择动作
- **Then**: 选中的动作添加到计划中，可以设置组数、次数、重量
- **Verification**: `programmatic`

### AC-8: 保存自定义计划
- **Given**: 用户已选择好动作并设置好参数
- **When**: 用户点击"保存计划"按钮
- **Then**: 系统创建fitness_plans记录和templates记录（用户私有），并创建对应的训练日程
- **Verification**: `programmatic`

### AC-9: AI计划页面
- **Given**: 用户点击计划页面的"AI智能定制"按钮
- **When**: 页面加载完成
- **Then**: 显示AI计划生成页面，包含目标设定、对话框界面
- **Verification**: `human-judgment`

### AC-10: AI生成计划
- **Given**: 用户在AI计划页面设定好目标并进行对话
- **When**: AI生成计划后用户确认保存
- **Then**: 系统创建fitness_plans记录和templates记录，并创建对应的训练日程
- **Verification**: `programmatic`

### AC-11: 训练记录展示
- **Given**: 用户打开训练记录页面
- **When**: 页面加载完成
- **Then**: 显示训练记录列表，每条记录包含计划名、时间、动作、动作时间信息
- **Verification**: `human-judgment`

### AC-12: 训练记录关联查询
- **Given**: 用户查看训练记录
- **When**: 系统查询workout_records表
- **Then**: 通过关联查询获取计划名、动作名等信息，展示完整的记录详情
- **Verification**: `programmatic`

### AC-13: 功能完整性检查
- **Given**: 所有功能开发完成
- **When**: 进行全面功能检查
- **Then**: 所有功能按设计文档100%实现，无遗漏功能
- **Verification**: `human-judgment`

### AC-14: 前后端一致性
- **Given**: 功能检查进行中
- **When**: 对比前后端实现与设计文档
- **Then**: 前后端实现一致，API接口完整，数据结构正确
- **Verification**: `programmatic`

## Open Questions
- [ ] AI功能是否需要调用真实的AI服务API，还是使用模拟数据？
- [ ] 自定义计划的模板分享功能是否需要实现？
- [ ] 训练记录的筛选和分页功能是否需要完善？
- [ ] 是否需要添加训练提醒功能？
