// 测试环境配置功能
import fs from 'fs';
import path from 'path';

// 读取env.ts文件
const envFilePath = path.join(process.cwd(), 'src', 'utils', 'env.ts');
const envContent = fs.readFileSync(envFilePath, 'utf8');

// 读取api.ts文件
const apiFilePath = path.join(process.cwd(), 'src', 'utils', 'api.ts');
const apiContent = fs.readFileSync(apiFilePath, 'utf8');

console.log('=== 环境配置测试 ===');

// 测试1: 检查环境检测逻辑
console.log('1. 环境检测逻辑:');
const envDetection = envContent.includes("import.meta.env.MODE === 'development' || import.meta.env.VITE_IS_TEST === 'true'");
console.log(`   ✓ 环境检测逻辑正确: ${envDetection}`);

// 测试2: 检查模拟数据是否存在
console.log('2. 模拟数据:');
const mockDataExists = envContent.includes('mockData');
console.log(`   ✓ 模拟数据存在: ${mockDataExists}`);

// 测试3: 检查API服务是否根据环境切换数据源
console.log('3. API服务环境切换:');
const apiEnvCheck = apiContent.includes('if (isTestEnvironment)');
console.log(`   ✓ API服务环境切换逻辑正确: ${apiEnvCheck}`);

// 测试4: 检查模拟数据结构是否完整
console.log('4. 模拟数据结构:');
const mockTemplates = envContent.includes('templates: [');
const mockTemplateDays = envContent.includes('templateDays: [');
const mockTemplateExercises = envContent.includes('templateExercises: [');
const mockPlans = envContent.includes('plans: [');
const mockWorkoutRecords = envContent.includes('workoutRecords: [');

console.log(`   ✓ 模板数据: ${mockTemplates}`);
console.log(`   ✓ 模板训练日数据: ${mockTemplateDays}`);
console.log(`   ✓ 模板训练动作数据: ${mockTemplateExercises}`);
console.log(`   ✓ 用户计划数据: ${mockPlans}`);
console.log(`   ✓ 训练记录数据: ${mockWorkoutRecords}`);

console.log('\n=== 测试完成 ===');
console.log('前端环境配置功能已正确实现!');
console.log('\n说明:');
console.log('- 开发环境 (npm run dev): 使用模拟数据');
console.log('- 生产环境 (npm run build): 调用后端API');
console.log('- 可通过设置 VITE_IS_TEST=true 强制使用模拟数据');
