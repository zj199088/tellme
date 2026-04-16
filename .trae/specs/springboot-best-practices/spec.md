# Spring Boot 最佳实践改进 - 产品需求文档

## Overview
- **Summary**: 按照Spring Boot最佳实践重构和改进后端代码，包括统一异常处理、自定义业务异常、改进统一返回结果、代码规范化、DTO/VO规范、日志规范化等。
- **Purpose**: 提高代码质量、可维护性、可扩展性和代码规范性，遵循Spring Boot开发最佳实践。
- **Target Users**: 后端开发人员和系统维护人员

## Goals
- 实现统一的全局异常处理机制
- 添加自定义业务异常类
- 改进统一返回结果类，添加更完善的状态码
- 规范代码使用Lombok简化开发
- 添加完善的日志记录
- 规范DTO/VO使用，减少Map类型参数
- 统一代码风格和依赖注入方式

## Non-Goals (Out of Scope)
- 修改业务逻辑
- 添加新功能接口
- 重构数据库结构
- 大幅改变现有API接口契约

## Background & Context
- 后端项目使用Spring Boot 3.2.5 + MyBatis Plus
- 目前存在代码不够规范、异常处理分散、缺少统一日志等问题
- 项目已引入Lombok但未充分使用
- 需要按照Spring Boot企业级开发最佳实践进行代码优化

## Functional Requirements
- **FR-1**: 实现全局异常处理器
- **FR-2**: 创建自定义业务异常类
- **FR-3**: 改进统一返回结果类，添加HTTP状态码和业务状态码枚举
- **FR-4**: 使用Lombok简化实体类和DTO类
- **FR-5**: 添加日志记录，规范日志输出
- **FR-6**: 创建明确的DTO和VO类替代Map类型参数
- **FR-7**: 统一依赖注入方式（@Resource vs @Autowired）

## Non-Functional Requirements
- **NFR-1**: 代码修改应保持向后兼容，不破坏现有功能
- **NFR-2**: 异常处理应提供清晰的错误信息，便于调试
- **NFR-3**: 日志应包含足够的上下文信息，便于问题定位
- **NFR-4**: 代码风格应统一，符合Java编程规范

## Constraints
- **Technical**: 必须保持Spring Boot 3.2.5版本
- **Dependencies**: 依赖现有的MyBatis Plus、Spring Security等框架
- **Compatibility**: 必须保持前后端API接口兼容性

## Assumptions
- 现有功能逻辑正确，只需重构代码结构
- 前端不会因为后端代码重构而需要修改
- 所有现有测试用例应继续通过

## Acceptance Criteria

### AC-1: 全局异常处理实现
- **Given**: 后端应用运行
- **When**: 发生任何异常
- **Then**: 异常应被统一处理器捕获并返回标准格式的错误响应
- **Verification**: `programmatic`

### AC-2: 自定义业务异常
- **Given**: 业务逻辑中发生错误
- **When**: 抛出业务异常
- **Then**: 应包含错误码和错误信息，并被正确处理
- **Verification**: `programmatic`

### AC-3: 统一返回结果类
- **Given**: 任何API接口调用
- **When**: 接口返回响应
- **Then**: 响应格式统一，包含code、message、data字段
- **Verification**: `programmatic`

### AC-4: Lombok使用
- **Given**: 查看实体类和DTO类
- **When**: 检查代码
- **Then**: 应使用@Data、@Getter、@Setter等Lombok注解简化代码
- **Verification**: `human-judgment`

### AC-5: 日志记录
- **Given**: 应用运行过程
- **When**: 发生关键操作或异常
- **Then**: 应有相应的日志记录
- **Verification**: `human-judgment`

### AC-6: DTO/VO规范
- **Given**: 查看Controller接口
- **When**: 检查请求和响应参数
- **Then**: 应使用明确的DTO/VO类替代Map类型
- **Verification**: `human-judgment`

### AC-7: 代码可正常运行
- **Given**: 代码重构完成
- **When**: 启动应用并测试现有功能
- **Then**: 所有现有功能应正常工作
- **Verification**: `programmatic`

## Open Questions
- [ ] 是否需要修改现有的Controller返回类型（Map转Result）
- [ ] 日志级别和日志输出内容的详细程度
