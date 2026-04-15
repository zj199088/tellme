# 健身应用 - 实现计划

## [x] Task 1: 创建数据库表结构
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 按照技术架构文档创建所有数据库表
  - 包括用户表、用户设备表、应用配置表、运动分类表、运动动作表、模板表、模板训练日表、模板动作关联表、健身计划表、训练日程表、训练日程动作表、锻炼记录表、AI对话历史表、音乐表
- **Acceptance Criteria Addressed**: 所有数据模型相关需求
- **Test Requirements**:
  - `programmatic` TR-1.1: 所有表结构创建成功
  - `programmatic` TR-1.2: 表之间的关系正确建立
  - `programmatic` TR-1.3: 索引设置合理
- **Notes**: 使用MySQL 8.0，确保表结构符合技术架构文档

## [x] Task 2: 实现后端认证系统
- **Priority**: P0
- **Depends On**: Task 1
- **Description**: 
  - 实现JWT认证机制
  - 实现微信登录和管理员登录接口
  - 配置Spring Security
- **Acceptance Criteria Addressed**: 用户认证需求
- **Test Requirements**:
  - `programmatic` TR-2.1: 微信登录接口返回正确的JWT token
  - `programmatic` TR-2.2: 管理员登录接口返回正确的JWT token
  - `programmatic` TR-2.3: 未认证请求被正确拦截
- **Notes**: 确保密码加密存储

## [x] Task 3: 实现模板相关功能
- **Priority**: P0
- **Depends On**: Task 2
- **Description**: 
  - 实现模板列表、详情、训练日、训练动作接口
  - 实现从模板创建计划的功能，添加fitness_plans表数据
- **Acceptance Criteria Addressed**: AC-2
- **Test Requirements**:
  - `programmatic` TR-3.1: 获取模板列表接口返回正确数据
  - `programmatic` TR-3.2: 从模板创建计划接口生成fitness_plans记录
  - `human-judgment` TR-3.3: 模板详情页面展示正确
- **Notes**: 确保模板数据的完整性

## [x] Task 4: 实现训练记录功能
- **Priority**: P0
- **Depends On**: Task 3
- **Description**: 
  - 实现获取今日训练、最近训练记录接口
  - 实现创建和更新训练记录接口
  - 实现训练日程表和训练日程动作表的管理
  - 实现根据星期几获取训练内容的功能
- **Acceptance Criteria Addressed**: AC-3, AC-8
- **Test Requirements**:
  - `programmatic` TR-4.1: 获取今日训练接口返回正确数据
  - `programmatic` TR-4.2: 创建训练记录接口生成workout_records记录
  - `programmatic` TR-4.3: 训练日程和训练日程动作管理功能正常
  - `programmatic` TR-4.4: 根据星期几获取训练内容功能正常
- **Notes**: 确保训练记录的完整性和一致性

## [x] Task 5: 实现自定义计划功能
- **Priority**: P1
- **Depends On**: Task 2
- **Description**: 
  - 实现获取运动分类和动作的接口
  - 实现创建自定义计划的功能，记录fitness_plans和templates数据
  - 实现模板的分享功能
  - 实现模板的权限控制（私有模板只有创建者可查询）
- **Acceptance Criteria Addressed**: AC-4, AC-5
- **Test Requirements**:
  - `programmatic` TR-5.1: 获取运动分类接口返回正确数据
  - `programmatic` TR-5.2: 创建自定义计划接口生成fitness_plans和templates记录
  - `programmatic` TR-5.3: 私有模板只有创建者可查询
  - `programmatic` TR-5.4: 分享模板后其他用户可查询
- **Notes**: 确保自定义计划的灵活性

## [x] Task 6: 实现AI计划生成功能
- **Priority**: P1
- **Depends On**: Task 5
- **Description**: 
  - 实现AI对话接口
  - 实现AI生成计划接口
  - 集成阿里云百炼大模型
  - 实现用户目标设定和体检报告上传功能
  - 实现AI对话历史管理功能
- **Acceptance Criteria Addressed**: AC-6
- **Test Requirements**:
  - `programmatic` TR-6.1: AI对话接口返回正确响应
  - `programmatic` TR-6.2: AI生成计划接口生成合理的健身计划
  - `human-judgment` TR-6.3: AI生成的计划符合用户需求
  - `programmatic` TR-6.4: 体检报告上传功能正常
  - `programmatic` TR-6.5: AI对话历史记录功能正常
- **Notes**: 确保API调用的稳定性和响应时间

## [x] Task 9: 实现前端首页
- **Priority**: P0
- **Depends On**: Task 4
- **Description**: 
  - 实现当前计划展示（最多3个，可停止/暂停）
  - 实现训练记录展示（最近3条）
  - 实现锻炼进度展示
  - 添加炫酷的动画效果，确保风格轻松活泼动感简洁
- **Acceptance Criteria Addressed**: AC-7, AC-13
- **Test Requirements**:
  - `human-judgment` TR-9.1: 首页布局美观，动画效果炫酷
  - `programmatic` TR-9.2: 数据展示正确
  - `programmatic` TR-9.3: 计划管理功能正常（停止/暂停）
  - `human-judgment` TR-9.4: 页面风格轻松活泼动感简洁
- **Notes**: 确保响应式设计

## [/] Task 10: 实现前端模板和计划页面
- **Priority**: P0
- **Depends On**: Task 3, Task 5, Task 6
- **Description**: 
  - 实现模板列表和详情页面
  - 实现自定义计划创建页面
  - 实现AI计划生成页面
  - 实现锻炼记录详情页面（支持分页和筛选）
- **Acceptance Criteria Addressed**: AC-2, AC-4, AC-6, AC-8
- **Test Requirements**:
  - `human-judgment` TR-10.1: 页面布局美观，交互流畅
  - `programmatic` TR-10.2: 功能逻辑正确
  - `programmatic` TR-10.3: 锻炼记录查询功能正常（分页和筛选）
  - `human-judgment` TR-10.4: 页面风格轻松活泼动感简洁
- **Notes**: 确保用户体验良好

## [ ] Task 7: 实现音乐播放功能
- **Priority**: P2
- **Depends On**: Task 2
- **Description**: 
  - 实现文件上传接口
  - 实现音乐播放器前端页面
  - 集成腾讯云COS存储
  - 实现音乐选择和播放功能
  - 实现音乐管理功能（CRUD操作）
- **Acceptance Criteria Addressed**: AC-9
- **Test Requirements**:
  - `programmatic` TR-7.1: 音乐文件上传成功
  - `programmatic` TR-7.2: 音乐管理接口功能正常
  - `human-judgment` TR-7.3: 音乐播放器功能正常
  - `human-judgment` TR-7.4: 音乐选择界面美观
- **Notes**: 确保文件上传的安全性

## [ ] Task 8: 实现管理后台功能
- **Priority**: P1
- **Depends On**: Task 2
- **Description**: 
  - 实现运动分类管理接口
  - 实现运动动作管理接口
  - 实现用户管理接口
  - 实现用户设备管理接口
  - 实现应用配置管理接口
  - 实现内置记录管理功能
- **Acceptance Criteria Addressed**: AC-11, AC-12
- **Test Requirements**:
  - `programmatic` TR-8.1: 分类管理接口功能正常
  - `programmatic` TR-8.2: 动作管理接口功能正常
  - `programmatic` TR-8.3: 用户管理接口功能正常
  - `programmatic` TR-8.4: 用户设备管理接口功能正常
  - `programmatic` TR-8.5: 应用配置管理接口功能正常
  - `programmatic` TR-8.6: 内置记录管理功能正常
- **Notes**: 确保管理权限的安全性

## [ ] Task 9: 实现前端首页
- **Priority**: P0
- **Depends On**: Task 4
- **Description**: 
  - 实现当前计划展示（最多3个，可停止/暂停）
  - 实现训练记录展示（最近3条）
  - 实现锻炼进度展示
  - 添加炫酷的动画效果，确保风格轻松活泼动感简洁
- **Acceptance Criteria Addressed**: AC-7, AC-13
- **Test Requirements**:
  - `human-judgment` TR-9.1: 首页布局美观，动画效果炫酷
  - `programmatic` TR-9.2: 数据展示正确
  - `programmatic` TR-9.3: 计划管理功能正常（停止/暂停）
  - `human-judgment` TR-9.4: 页面风格轻松活泼动感简洁
- **Notes**: 确保响应式设计

## [ ] Task 10: 实现前端模板和计划页面
- **Priority**: P0
- **Depends On**: Task 3, Task 5, Task 6
- **Description**: 
  - 实现模板列表和详情页面
  - 实现自定义计划创建页面
  - 实现AI计划生成页面
  - 实现锻炼记录详情页面（支持分页和筛选）
- **Acceptance Criteria Addressed**: AC-2, AC-4, AC-6, AC-8
- **Test Requirements**:
  - `human-judgment` TR-10.1: 页面布局美观，交互流畅
  - `programmatic` TR-10.2: 功能逻辑正确
  - `programmatic` TR-10.3: 锻炼记录查询功能正常（分页和筛选）
  - `human-judgment` TR-10.4: 页面风格轻松活泼动感简洁
- **Notes**: 确保用户体验良好

## [x] Task 11: 实现前端环境配置
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 创建环境配置文件，实现环境检测逻辑
  - 实现模拟数据管理
  - 更新API服务，根据环境切换数据来源
  - 确保测试环境使用模拟数据，生产环境调用后端API
- **Acceptance Criteria Addressed**: AC-14
- **Test Requirements**:
  - `programmatic` TR-11.1: 环境检测逻辑正确，能区分测试和生产环境 ✅
  - `programmatic` TR-11.2: 测试环境返回模拟数据 ✅
  - `programmatic` TR-11.3: 生产环境调用后端API ✅
  - `programmatic` TR-11.4: 模拟数据结构与API响应一致 ✅
- **Notes**: 确保模拟数据覆盖所有必要的API场景

### 实现结果
- 创建了 `src/utils/env.ts` 文件，包含环境检测逻辑和完整的模拟数据
- 更新了 `src/utils/api.ts` 文件，实现了环境切换功能
- 测试环境（开发模式）自动使用模拟数据
- 生产环境（构建模式）自动调用后端API
- 可通过设置 `VITE_IS_TEST=true` 强制使用模拟数据
