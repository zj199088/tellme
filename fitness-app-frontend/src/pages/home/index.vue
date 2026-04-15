<template>
  <view class="home-container">
    <view class="header">
      <text class="title">健身计划</text>
    </view>
    <view class="content">
      <!-- 当前计划卡片 -->
      <view class="plan-card" v-if="activePlan">
        <text class="plan-title">当前计划</text>
        <text class="plan-name">{{ activePlan.name }}</text>
        <text class="plan-description">{{ activePlan.description }}</text>
        <view class="progress-container">
          <view class="progress-bar">
            <view class="progress-fill" :style="{ width: progressPercentage + '%' }"></view>
          </view>
          <text class="progress-text">第 {{ activePlan.currentDay }} 天 / 共 {{ activePlan.totalDays }} 天</text>
        </view>
      </view>
      <view class="plan-card" v-else>
        <text class="plan-title">当前计划</text>
        <text class="plan-description">暂无活跃计划</text>
      </view>

      <!-- 今日训练 -->
      <view class="today-section" v-if="activePlan">
        <text class="section-title">今日训练</text>
        <view class="exercise-list" v-if="todayRecord">
          <view class="exercise-item" v-for="exercise in todayRecord.exercises" :key="exercise.id">
            <view class="exercise-info">
              <text class="exercise-name">{{ exercise.name }}</text>
              <text class="exercise-sets">{{ exercise.sets }} 组 × {{ exercise.reps }} {{ exercise.weight ? '次' : '秒' }}</text>
              <text class="exercise-weight" v-if="exercise.weight">{{ exercise.weight }} kg</text>
            </view>
            <view class="exercise-checkbox" :class="{ checked: exercise.completed }" @click="toggleExercise(exercise)">
              <text v-if="exercise.completed">✓</text>
            </view>
          </view>
          <button class="complete-button" :class="{ completed: todayRecord.completed }" @click="completeTraining">
            {{ todayRecord.completed ? '已完成' : '完成训练' }}
          </button>
        </view>
        <view class="no-training" v-else>
          <text class="no-training-text">开始今日训练</text>
          <button class="start-button" @click="startTraining">开始训练</button>
        </view>
      </view>

      <!-- 训练记录 -->
      <view class="records-section">
        <text class="section-title">训练记录</text>
        <view class="record-list" v-if="recentRecords.length > 0">
          <view class="record-item" v-for="record in recentRecords" :key="record.id">
            <view class="record-date">{{ formatDate(record.date) }}</view>
            <view class="record-info">
              <text class="record-plan">{{ record.planName }}</text>
              <text class="record-status" :class="{ completed: record.completed }">
                {{ record.completed ? '已完成' : '未完成' }}
              </text>
            </view>
          </view>
        </view>
        <text class="no-records" v-else>暂无训练记录</text>
      </view>
    </view>
  </view>
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
.home-container {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background-color: #FF6B35;
  color: white;
  padding: 20rpx;
  text-align: center;
}

.title {
  font-size: 32rpx;
  font-weight: bold;
}

.content {
  padding: 20rpx;
}

.plan-card {
  background-color: white;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
}

.plan-title {
  font-size: 28rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.plan-name {
  font-size: 26rpx;
  font-weight: 600;
  margin-bottom: 8rpx;
  color: #333;
}

.plan-description {
  font-size: 24rpx;
  color: #666;
  margin-bottom: 20rpx;
}

.progress-container {
  margin-top: 20rpx;
}

.progress-bar {
  width: 100%;
  height: 8rpx;
  background-color: #f0f0f0;
  border-radius: 4rpx;
  overflow: hidden;
  margin-bottom: 10rpx;
}

.progress-fill {
  height: 100%;
  background-color: #FF6B35;
  border-radius: 4rpx;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 22rpx;
  color: #999;
  text-align: right;
}

.today-section {
  background-color: white;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
}

.records-section {
  background-color: white;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 28rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
}

.exercise-list {
  margin-bottom: 20rpx;
}

.exercise-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.exercise-item:last-child {
  border-bottom: none;
}

.exercise-info {
  flex: 1;
}

.exercise-name {
  font-size: 24rpx;
  font-weight: 600;
  margin-bottom: 4rpx;
}

.exercise-sets {
  font-size: 22rpx;
  color: #666;
  margin-bottom: 2rpx;
}

.exercise-weight {
  font-size: 20rpx;
  color: #999;
}

.exercise-checkbox {
  width: 40rpx;
  height: 40rpx;
  border: 2rpx solid #ddd;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.exercise-checkbox.checked {
  background-color: #FF6B35;
  border-color: #FF6B35;
}

.complete-button {
  width: 100%;
  padding: 20rpx;
  background-color: #FF6B35;
  color: white;
  border: none;
  border-radius: 8rpx;
  font-size: 24rpx;
  font-weight: 600;
  margin-top: 20rpx;
  cursor: pointer;
  transition: all 0.3s ease;
}

.complete-button.completed {
  background-color: #4CAF50;
}

.no-training {
  text-align: center;
  padding: 60rpx 0;
}

.no-training-text {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 20rpx;
}

.start-button {
  padding: 20rpx 40rpx;
  background-color: #FF6B35;
  color: white;
  border: none;
  border-radius: 8rpx;
  font-size: 24rpx;
  font-weight: 600;
  cursor: pointer;
}

.record-list {
  margin-top: 10rpx;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.record-item:last-child {
  border-bottom: none;
}

.record-date {
  font-size: 22rpx;
  color: #999;
  min-width: 120rpx;
}

.record-info {
  flex: 1;
  margin-left: 20rpx;
}

.record-plan {
  font-size: 24rpx;
  font-weight: 600;
  margin-bottom: 4rpx;
  display: block;
}

.record-status {
  font-size: 20rpx;
  color: #999;
}

.record-status.completed {
  color: #4CAF50;
}

.no-records {
  font-size: 24rpx;
  color: #999;
  text-align: center;
  padding: 40rpx 0;
}
</style>