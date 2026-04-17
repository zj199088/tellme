# 微信授权登录完整实现 - 实施计划（分解并优先级化的任务列表）

## [x] Task 1: 配置微信开放平台参数
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 在 application.yml 中添加微信开放平台的配置
  - 添加 appId 和 appSecret 配置项
  - 添加微信接口基础URL配置
- **Acceptance Criteria Addressed**: [AC-2]
- **Test Requirements**:
  - `programmatic` TR-1.1: 检查 application.yml 中是否包含微信配置
  - `programmatic` TR-1.2: 检查配置项是否正确设置
- **Notes**: 微信开放平台需要先注册应用获取appId和appSecret

## [x] Task 2: 创建微信API工具类
- **Priority**: P0
- **Depends On**: Task 1
- **Description**: 
  - 创建 WechatApiUtils 工具类
  - 实现调用微信接口验证code并获取openId的方法
  - 实现错误处理和重试机制
- **Acceptance Criteria Addressed**: [AC-2]
- **Test Requirements**:
  - `programmatic` TR-2.1: 检查 WechatApiUtils 是否存在
  - `programmatic` TR-2.2: 检查是否实现了获取openId的方法
  - `programmatic` TR-2.3: 检查错误处理是否完善
- **Notes**: 参考微信开放平台文档实现接口调用

## [x] Task 3: 修改后端微信登录接口
- **Priority**: P0
- **Depends On**: Task 2
- **Description**: 
  - 修改 AuthController 中的 wechatLogin 方法
  - 接收前端传递的 code 而不是直接接收 openId
  - 调用 WechatApiUtils 获取 openId
  - 然后创建或更新用户并返回token
- **Acceptance Criteria Addressed**: [AC-2, AC-3]
- **Test Requirements**:
  - `programmatic` TR-3.1: 检查 wechatLogin 方法是否接收 code 参数
  - `programmatic` TR-3.2: 检查是否调用 WechatApiUtils 获取 openId
  - `programmatic` TR-3.3: 检查是否正确处理微信接口返回的错误
- **Notes**: 保持向后兼容，同时支持直接传递openId的方式

## [x] Task 4: 修改前端微信登录实现
- **Priority**: P0
- **Depends On**: Task 3
- **Description**: 
  - 修改登录页面的 handleWechatLogin 方法
  - 调用微信登录API获取code
  - 将code传递给后端微信登录接口
  - 处理登录成功和失败的回调
- **Acceptance Criteria Addressed**: [AC-1, AC-3, AC-5]
- **Test Requirements**:
  - `programmatic` TR-4.1: 检查是否调用微信登录API获取code
  - `programmatic` TR-4.2: 检查是否将code传递给后端
  - `human-judgment` TR-4.3: 检查错误提示是否清晰
- **Notes**: 需要在微信小程序环境中测试

## [ ] Task 5: 测试微信登录流程
- **Priority**: P1
- **Depends On**: Task 4
- **Description**: 
  - 在小程序模式下测试完整的微信登录流程
  - 测试成功登录场景
  - 测试失败场景和错误处理
  - 测试登录状态持久化
- **Acceptance Criteria Addressed**: [AC-3, AC-4, AC-5]
- **Test Requirements**:
  - `human-judgment` TR-5.1: 测试完整登录流程是否正常
  - `human-judgment` TR-5.2: 测试错误处理是否完善
  - `human-judgment` TR-5.3: 测试登录状态是否持久
- **Notes**: 需要在微信小程序开发工具中测试