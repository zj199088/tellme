<template>
  <div class="home-container">
    <div class="header">
      <h1 class="title">健身计划</h1>
      <div class="header-bg"></div>
    </div>
    <div class="content">
      <!-- 当前计划卡片 -->
      <div class="plan-card" v-if="activePlan" :class="['plan-card', 'animate-in']">
        <h2 class="plan-title">当前计划</h2>
        <h3 class="plan-name">{{ activePlan.name }}</h3>
        <p class="plan-description">{{ activePlan.description }}</p>
        <div class="progress-container">
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: progressPercentage + '%' }"></div>
            <div class="progress-glow" :style="{ left: progressPercentage + '%' }"></div>
          </div>
          <p class="progress-text">第 {{ activePlan.currentDay }} 天 / 共 {{ activePlan.totalDays }} 天</p>
        </div>
      </div>
      <div class="plan-card" v-else :class="['plan-card', 'animate-in']">
        <h2 class="plan-title">当前计划</h2>
        <p class="plan-description">暂无活跃计划</p>
      </div>

      <!-- 今日训练 -->
      <div class="today-section" v-if="activePlan" :class="['today-section', 'animate-in']">
        <h2 class="section-title">今日训练</h2>
        <div class="exercise-list" v-if="todayRecord">
          <div class="exercise-item" v-for="exercise in todayRecord.exercises" :key="exercise.id" :class="['exercise-item', exercise.completed ? 'completed' : '']">
            <div class="exercise-info">
              <h3 class="exercise-name">{{ exercise.name }}</h3>
              <p class="exercise-sets">{{ exercise.sets }} 组 × {{ exercise.reps }} {{ exercise.weight ? '次' : '秒' }}</p>
              <p class="exercise-weight" v-if="exercise.weight">{{ exercise.weight }} kg</p>
            </div>
            <div class="exercise-checkbox" :class="{ checked: exercise.completed }" @click="toggleExercise(exercise)">
              <span v-if="exercise.completed">✓</span>
            </div>
          </div>
          <button class="complete-button" :class="{ completed: todayRecord.completed }" @click="completeTraining">
            {{ todayRecord.completed ? '已完成' : '完成训练' }}
          </button>
        </div>
        <div class="no-training" v-else>
          <p class="no-training-text">开始今日训练</p>
          <button class="start-button" @click="startTraining">开始训练</button>
        </div>
      </div>

      <!-- 训练记录 -->
      <div class="records-section" :class="['records-section', 'animate-in']">
        <h2 class="section-title">训练记录</h2>
        <div class="record-list" v-if="recentRecords.length > 0">
          <div class="record-item" v-for="(record, index) in recentRecords" :key="record.id" :class="['record-item', 'animate-in']" :style="{ animationDelay: `${index * 0.1}s` }">
            <div class="record-date">{{ formatDate(record.date) }}</div>
            <div class="record-info">
              <h3 class="record-plan">{{ record.planName }}</h3>
              <p class="record-status" :class="{ completed: record.completed }">
                {{ record.completed ? '已完成' : '未完成' }}
              </p>
            </div>
            <div class="record-indicator" :class="{ completed: record.completed }"></div>
          </div>
        </div>
        <p class="no-records" v-else>暂无训练记录</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import {
  Exercise,
  TrainingPlan,
  TrainingRecord,
  getActivePlan,
  getTodayRecord,
  getRecords,
  createRecord,
  updateRecord,
  updatePlanProgress,
  initializeSampleData
} from '../../utils/trainingData';

// 响应式数据
const activePlan = ref<TrainingPlan | null>(null);
const todayRecord = ref<TrainingRecord | null>(null);
const recentRecords = ref<TrainingRecord[]>([]);

// 计算属性
const progressPercentage = computed(() => {
  if (!activePlan.value) return 0;
  return (activePlan.value.currentDay / activePlan.value.totalDays) * 100;
});

// 方法
const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return `${date.getMonth() + 1}月${date.getDate()}日`;
};

const startTraining = () => {
  if (activePlan.value) {
    const record = createRecord(activePlan.value);
    todayRecord.value = record;
  }
};

const toggleExercise = (exercise: Exercise) => {
  if (todayRecord.value) {
    const updatedExercises = todayRecord.value.exercises.map(ex => 
      ex.id === exercise.id ? { ...ex, completed: !ex.completed } : ex
    );
    todayRecord.value = {
      ...todayRecord.value,
      exercises: updatedExercises,
      completed: updatedExercises.every(ex => ex.completed)
    };
    updateRecord(todayRecord.value);
  }
};

const completeTraining = () => {
  if (todayRecord.value && activePlan.value) {
    todayRecord.value = {
      ...todayRecord.value,
      completed: true,
      exercises: todayRecord.value.exercises.map(ex => ({ ...ex, completed: true }))
    };
    updateRecord(todayRecord.value);
    updatePlanProgress(activePlan.value.id);
    // 重新获取计划数据以更新进度
    activePlan.value = getActivePlan();
  }
};

// 生命周期
onMounted(() => {
  // 初始化示例数据
  initializeSampleData();
  // 获取活跃计划
  activePlan.value = getActivePlan();
  // 获取今日记录
  todayRecord.value = getTodayRecord();
  // 获取最近的训练记录
  const records = getRecords();
  recentRecords.value = records
    .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
    .slice(0, 5);
});
</script>

<style scoped>
/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-in {
  animation: fadeInUp 0.6s ease forwards;
}

/* 主容器 */
.home-container {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  position: relative;
  overflow: hidden;
}

/* 头部设计 */
.header {
  position: relative;
  background: linear-gradient(135deg, #FF6B35 0%, #FF8E53 100%);
  color: white;
  padding: 40rpx 20rpx;
  text-align: center;
  border-bottom-left-radius: 30rpx;
  border-bottom-right-radius: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(255, 107, 53, 0.3);
  overflow: hidden;
}

.header-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: radial-gradient(circle at 20% 80%, rgba(255, 255, 255, 0.2) 0%, transparent 50%),
                    radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-10rpx) rotate(1deg);
  }
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  position: relative;
  z-index: 1;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
}

/* 内容区域 */
.content {
  padding: 30rpx 20rpx;
  padding-bottom: 160rpx;
}

/* 卡片设计 */
.plan-card,
.today-section,
.records-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24rpx;
  padding: 35rpx;
  margin-bottom: 25rpx;
  box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.plan-card::before,
.today-section::before,
.records-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4rpx;
  background: linear-gradient(90deg, #FF6B35, #FF8E53);
  border-top-left-radius: 24rpx;
  border-top-right-radius: 24rpx;
}

.plan-card:hover,
.today-section:hover,
.records-section:hover {
  transform: translateY(-4rpx);
  box-shadow: 0 12rpx 40rpx rgba(0, 0, 0, 0.12);
}

/* 标题样式 */
.plan-title,
.section-title {
  font-size: 30rpx;
  font-weight: bold;
  margin-bottom: 15rpx;
  color: #333;
  display: flex;
  align-items: center;
}

.plan-title::before,
.section-title::before {
  content: '';
  width: 8rpx;
  height: 24rpx;
  background: #FF6B35;
  border-radius: 4rpx;
  margin-right: 12rpx;
}

.plan-name {
  font-size: 28rpx;
  font-weight: 600;
  margin-bottom: 12rpx;
  color: #333;
  transition: color 0.3s ease;
}

.plan-card:hover .plan-name {
  color: #FF6B35;
}

.plan-description {
  font-size: 24rpx;
  color: #666;
  margin-bottom: 25rpx;
  line-height: 1.5;
}

/* 进度条设计 */
.progress-container {
  margin-top: 25rpx;
}

.progress-bar {
  width: 100%;
  height: 12rpx;
  background-color: #f0f0f0;
  border-radius: 6rpx;
  overflow: hidden;
  margin-bottom: 15rpx;
  position: relative;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #FF6B35, #FF8E53);
  border-radius: 6rpx;
  transition: width 0.6s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
}

.progress-fill::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

.progress-glow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%) translateX(-50%);
  width: 40rpx;
  height: 40rpx;
  background: radial-gradient(circle, rgba(255, 107, 53, 0.3) 0%, transparent 70%);
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.progress-text {
  font-size: 22rpx;
  color: #999;
  text-align: right;
  font-weight: 500;
}

/* 训练项目 */
.exercise-list {
  margin-bottom: 25rpx;
}

.exercise-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 25rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
  transition: all 0.3s ease;
  position: relative;
}

.exercise-item:last-child {
  border-bottom: none;
}

.exercise-item:hover {
  background: rgba(255, 107, 53, 0.03);
  border-radius: 12rpx;
  padding-left: 15rpx;
  padding-right: 15rpx;
}

.exercise-item.completed {
  opacity: 0.8;
}

.exercise-item.completed .exercise-name {
  text-decoration: line-through;
  color: #999;
}

.exercise-info {
  flex: 1;
}

.exercise-name {
  font-size: 26rpx;
  font-weight: 600;
  margin-bottom: 8rpx;
  color: #333;
  transition: all 0.3s ease;
}

.exercise-sets {
  font-size: 22rpx;
  color: #666;
  margin-bottom: 4rpx;
}

.exercise-weight {
  font-size: 20rpx;
  color: #999;
}

/* 复选框设计 */
.exercise-checkbox {
  width: 48rpx;
  height: 48rpx;
  border: 2rpx solid #ddd;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  color: white;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  overflow: hidden;
}

.exercise-checkbox::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s;
}

.exercise-checkbox:hover::before {
  left: 100%;
}

.exercise-checkbox.checked {
  background: linear-gradient(135deg, #4CAF50, #66BB6A);
  border-color: #4CAF50;
  transform: scale(1.1) rotate(10deg);
  box-shadow: 0 4rpx 12rpx rgba(76, 175, 80, 0.4);
}

/* 按钮设计 */
.complete-button,
.start-button {
  width: 100%;
  padding: 25rpx;
  border: none;
  border-radius: 12rpx;
  font-size: 26rpx;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  overflow: hidden;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.complete-button {
  background: linear-gradient(135deg, #FF6B35, #FF8E53);
  color: white;
  margin-top: 25rpx;
}

.complete-button:hover {
  transform: translateY(-2rpx);
  box-shadow: 0 8rpx 20rpx rgba(255, 107, 53, 0.4);
}

.complete-button.completed {
  background: linear-gradient(135deg, #4CAF50, #66BB6A);
  animation: pulse 1s ease-in-out;
}

.start-button {
  background: linear-gradient(135deg, #FF6B35, #FF8E53);
  color: white;
  padding: 25rpx 50rpx;
  font-size: 26rpx;
}

.start-button:hover {
  transform: translateY(-2rpx) scale(1.05);
  box-shadow: 0 8rpx 20rpx rgba(255, 107, 53, 0.4);
}

/* 无训练状态 */
.no-training {
  text-align: center;
  padding: 80rpx 0;
  background: rgba(255, 107, 53, 0.03);
  border-radius: 16rpx;
  margin: 20rpx 0;
}

.no-training-text {
  font-size: 26rpx;
  color: #999;
  margin-bottom: 25rpx;
}

/* 训练记录 */
.record-list {
  margin-top: 15rpx;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 25rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.record-item:last-child {
  border-bottom: none;
}

.record-item:hover {
  background: rgba(255, 107, 53, 0.03);
  border-radius: 12rpx;
  padding-left: 15rpx;
  padding-right: 15rpx;
}

.record-date {
  font-size: 22rpx;
  color: #999;
  min-width: 120rpx;
  font-weight: 500;
}

.record-info {
  flex: 1;
  margin-left: 20rpx;
}

.record-plan {
  font-size: 24rpx;
  font-weight: 600;
  margin-bottom: 6rpx;
  display: block;
  color: #333;
  transition: color 0.3s ease;
}

.record-item:hover .record-plan {
  color: #FF6B35;
}

.record-status {
  font-size: 20rpx;
  color: #999;
  transition: all 0.3s ease;
}

.record-status.completed {
  color: #4CAF50;
  font-weight: 600;
}

.record-indicator {
  width: 12rpx;
  height: 12rpx;
  border-radius: 50%;
  background: #ddd;
  transition: all 0.3s ease;
}

.record-indicator.completed {
  background: #4CAF50;
  box-shadow: 0 0 10rpx rgba(76, 175, 80, 0.5);
  animation: pulse 2s infinite;
}

.no-records {
  font-size: 24rpx;
  color: #999;
  text-align: center;
  padding: 60rpx 0;
  background: rgba(0, 0, 0, 0.02);
  border-radius: 12rpx;
  margin-top: 10rpx;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content {
    padding: 20rpx 15rpx;
  }
  
  .plan-card,
  .today-section,
  .records-section {
    padding: 25rpx;
  }
  
  .title {
    font-size: 32rpx;
  }
  
  .plan-title,
  .section-title {
    font-size: 28rpx;
  }
  
  .plan-name {
    font-size: 26rpx;
  }
  
  .exercise-name {
    font-size: 24rpx;
  }
}
</style>