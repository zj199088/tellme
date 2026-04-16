# API URL 修复 - 实现计划

## [x] Task 1: 更新baseURL配置
- **Priority**: P0
- **Depends On**: None
- **Description**: 
  - 修改api.ts文件中的baseURL配置
  - 将baseURL从'http://localhost:8080'更新为'http://localhost:8080/api'
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `programmatic` TR-1.1: 验证baseURL配置已更新为'http://localhost:8080/api'
- **Notes**: 确保修改环境变量配置的默认值

## [x] Task 2: 移除模板相关API路径中的/api前缀
- **Priority**: P0
- **Depends On**: Task 1
- **Description**: 
  - 移除templates.getList接口路径中的/api前缀
  - 移除templates.getUser接口路径中的/api前缀
  - 移除templates.getDetail接口路径中的/api前缀
  - 移除templates.getDays接口路径中的/api前缀
  - 移除templates.getExercises接口路径中的/api前缀
  - 移除templates.createPlan接口路径中的/api前缀
- **Acceptance Criteria Addressed**: AC-2
- **Test Requirements**:
  - `programmatic` TR-2.1: 验证模板相关API路径已移除/api前缀
- **Notes**: 确保路径格式正确，无多余斜杠

## [x] Task 3: 移除计划相关API路径中的/api前缀
- **Priority**: P0
- **Depends On**: Task 1
- **Description**: 
  - 移除plans.getList接口路径中的/api前缀
  - 移除plans.updateStatus接口路径中的/api前缀
  - 移除plans.createCustom接口路径中的/api前缀
- **Acceptance Criteria Addressed**: AC-2
- **Test Requirements**:
  - `programmatic` TR-3.1: 验证计划相关API路径已移除/api前缀
- **Notes**: 注意路径中的查询参数处理

## [x] Task 4: 移除训练计划安排API路径中的/api前缀
- **Priority**: P0
- **Depends On**: Task 1
- **Description**: 
  - 移除workoutSchedules.getByPlan接口路径中的/api前缀
  - 移除workoutSchedules.getExercises接口路径中的/api前缀
  - 移除workoutSchedules.create接口路径中的/api前缀
- **Acceptance Criteria Addressed**: AC-2
- **Test Requirements**:
  - `programmatic` TR-4.1: 验证训练计划安排API路径已移除/api前缀
- **Notes**: 确保路径参数正确传递

## [x] Task 5: 移除训练记录相关API路径中的/api前缀
- **Priority**: P0
- **Depends On**: Task 1
- **Description**: 
  - 移除workout.getToday接口路径中的/api前缀
  - 移除workout.getRecent接口路径中的/api前缀
  - 移除workout.getSessions接口路径中的/api前缀
  - 移除workout.createRecord接口路径中的/api前缀
  - 移除workout.createBatch接口路径中的/api前缀
  - 移除workout.updateRecord接口路径中的/api前缀
- **Acceptance Criteria Addressed**: AC-2
- **Test Requirements**:
  - `programmatic` TR-5.1: 验证训练记录相关API路径已移除/api前缀
- **Notes**: 注意不同类型的请求方法（GET、POST、PUT）

## [x] Task 6: 移除管理员相关API路径中的/api前缀
- **Priority**: P0
- **Depends On**: Task 1
- **Description**: 
  - 移除admin.categories相关接口路径中的/api前缀
  - 移除admin.users相关接口路径中的/api前缀
  - 移除admin.exercises相关接口路径中的/api前缀
- **Acceptance Criteria Addressed**: AC-2
- **Test Requirements**:
  - `programmatic` TR-6.1: 验证管理员相关API路径已移除/api前缀
- **Notes**: 确保所有CRUD操作的路径都正确修改

## [x] Task 7: 更新响应拦截器URL判断逻辑
- **Priority**: P0
- **Depends On**: Task 1
- **Description**: 
  - 更新响应拦截器中的URL判断逻辑
  - 移除判断条件中的/api前缀
- **Acceptance Criteria Addressed**: AC-3
- **Test Requirements**:
  - `programmatic` TR-7.1: 验证响应拦截器URL判断逻辑已更新
- **Notes**: 确保401重定向逻辑正确

## [x] Task 8: 验证API调用正常工作
- **Priority**: P1
- **Depends On**: All previous tasks
- **Description**: 
  - 启动前端应用
  - 测试API调用是否正常工作
  - 验证无404错误
- **Acceptance Criteria Addressed**: AC-4
- **Test Requirements**:
  - `programmatic` TR-8.1: 验证API调用返回正确响应
  - `programmatic` TR-8.2: 验证无404错误
- **Notes**: 测试时确保后端服务已启动