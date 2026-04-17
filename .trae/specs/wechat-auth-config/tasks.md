# 微信小程序认证配置 - 实施计划（分解并优先级化的任务列表）

## [x] Task 1: 更新环境变量配置文件
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 更新 .env.example 文件，添加 VITE_APP_MODE 配置项
  - 提供两种模式的示例配置（miniprogram 和 web）
  - 添加详细的配置说明
- **Acceptance Criteria Addressed**: [AC-1]
- **Test Requirements**:
  - `programmatic` TR-1.1: 检查 .env.example 文件是否包含 VITE_APP_MODE 配置项
  - `programmatic` TR-1.2: 检查配置项是否有 miniprogram 和 web 两种可选值
  - `human-judgement` TR-1.3: 检查是否有清晰的配置说明文档
- **Notes**: 默认值建议设为 "web" 以保持向后兼容

## [x] Task 2: 创建配置工具模块
- **Priority**: P0
- **Depends On**: Task 1
- **Description**: 
  - 在 src/utils/ 目录下创建 config.ts 文件
  - 实现从环境变量读取配置的函数
  - 提供类型安全的配置访问接口
  - 包含默认值处理逻辑
- **Acceptance Criteria Addressed**: [AC-1]
- **Test Requirements**:
  - `programmatic` TR-2.1: 检查是否能正确读取 VITE_APP_MODE 环境变量
  - `programmatic` TR-2.2: 检查未设置配置时是否使用默认值 "web"
  - `programmatic` TR-2.3: 检查是否提供类型安全的 TypeScript 接口
- **Notes**: 使用 import.meta.env 读取 Vite 环境变量

## [x] Task 3: 在 API 工具中添加微信登录接口
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 在 src/utils/api.ts 中添加微信登录 API 调用函数
  - 函数名建议为 wechatLogin
  - 接收 openId, nickname, avatarUrl, gender, country, province, city 等参数
  - 调用后端 /auth/wechat 接口
- **Acceptance Criteria Addressed**: [AC-4]
- **Test Requirements**:
  - `programmatic` TR-3.1: 检查 api.ts 是否新增 wechatLogin 函数
  - `programmatic` TR-3.2: 检查函数是否正确调用 /auth/wechat 接口
  - `human-judgement` TR-3.3: 检查函数参数是否与后端接口匹配
- **Notes**: 参考现有的 login 和 adminLogin 函数实现

## [x] Task 4: 修改登录页面支持两种模式
- **Priority**: P0
- **Depends On**: Task 2, Task 3
- **Description**: 
  - 修改 src/pages/auth/login.vue
  - 根据配置项决定显示哪种登录界面
  - 小程序模式：显示微信授权登录按钮
  - 普通前端模式：显示账号密码登录表单
  - 实现微信登录流程
- **Acceptance Criteria Addressed**: [AC-2, AC-3, AC-4, AC-5]
- **Test Requirements**:
  - `human-judgement` TR-4.1: 配置为 miniprogram 时检查是否显示微信登录按钮
  - `human-judgement` TR-4.2: 配置为 web 时检查是否显示账号密码表单
  - `programmatic` TR-4.3: 检查两种模式下 token 是否都能正确保存到 localStorage
- **Notes**: 保持页面现有的视觉风格，微信登录按钮需要美观

## [x] Task 5: 确保登录状态持久化
- **Priority**: P1
- **Depends On**: Task 4
- **Description**: 
  - 验证两种登录方式下的登录状态持久化
  - 确保 token 正确保存和读取
  - 确保用户信息正确缓存
- **Acceptance Criteria Addressed**: [AC-6]
- **Test Requirements**:
  - `human-judgement` TR-5.1: 微信登录后刷新页面检查是否保持登录
  - `human-judgement` TR-5.2: 账号密码登录后刷新页面检查是否保持登录
  - `programmatic` TR-5.3: 检查 localStorage 中 token 和 userInfo 是否正确保存
- **Notes**: 复用现有的登录状态管理逻辑

## [x] Task 6: 端到端测试和验证
- **Priority**: P1
- **Depends On**: Task 5
- **Description**: 
  - 在两种模式下进行完整的端到端测试
  - 验证所有功能正常工作
  - 确保没有回归问题
- **Acceptance Criteria Addressed**: [AC-1, AC-2, AC-3, AC-4, AC-5, AC-6]
- **Test Requirements**:
  - `human-judgement` TR-6.1: 完整测试小程序模式的登录流程
  - `human-judgement` TR-6.2: 完整测试普通前端模式的登录流程
  - `human-judgement` TR-6.3: 检查配置变更后重启是否生效
  - `human-judgement` TR-6.4: 验证其他功能是否受影响
- **Notes**: 重点关注用户体验和功能完整性
