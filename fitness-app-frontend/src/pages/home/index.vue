<template>
  <div class="home-container">
    <div class="particle-bg" id="particleBg"></div>
    <div class="header">
      <h1 class="title neon-glow">健身计划</h1>
      <div class="header-bg"></div>
    </div>
    <div class="content">
      <!-- 计划列表 -->
      <div class="plans-section" :class="['plans-section', 'animate-in']">
        <h2 class="section-title">我的计划</h2>
        <div class="plan-list" v-if="plans.length > 0">
          <div class="plan-card" v-for="(plan, index) in plans" :key="plan.id" :class="['plan-card', 'animate-in']" :style="{ animationDelay: `${index * 0.1}s` }">
            <div class="plan-header">
              <h3 class="plan-name">{{ plan.name }}</h3>
              <div class="plan-actions">
                <button class="plan-action-btn pause" @click="togglePlanStatus(plan, 'pause')" v-if="plan.status === 'active'">
                  暂停
                </button>
                <button class="plan-action-btn resume" @click="togglePlanStatus(plan, 'resume')" v-else-if="plan.status === 'paused'">
                  继续
                </button>
                <button class="plan-action-btn stop" @click="togglePlanStatus(plan, 'stop')">
                  停止
                </button>
              </div>
            </div>
            <p class="plan-description">{{ plan.description }}</p>
            <div class="progress-container">
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: getPlanProgress(plan) + '%' }"></div>
                <div class="progress-glow" :style="{ left: getPlanProgress(plan) + '%' }"></div>
              </div>
              <p class="progress-text">第 {{ plan.currentDay }} 天 / 共 {{ plan.totalDays }} 天</p>
            </div>
            <div class="plan-status" :class="plan.status">
              {{ getStatusText(plan.status) }}
            </div>
          </div>
        </div>
        <div class="no-plans" v-else>
          <p class="no-plans-text">暂无计划</p>
          <button class="create-plan-btn" @click="navigateToCreatePlan">创建计划</button>
        </div>
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
        <div class="section-header">
          <h2 class="section-title">训练记录</h2>
          <button class="more-btn" @click="navigateToMoreRecords">查看更多</button>
        </div>
        <div class="record-list" v-if="recentRecords.length > 0">
          <div class="record-item" v-for="(record, index) in recentRecords.slice(0, 3)" :key="record.id" :class="['record-item', 'animate-in']" :style="{ animationDelay: `${index * 0.1}s` }">
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
import { ref, computed, onMounted, nextTick } from 'vue';
import axios from 'axios';

interface Exercise {
  id: number;
  name: string;
  sets: number;
  reps: number;
  weight?: number;
  completed?: boolean;
}

interface TrainingPlan {
  id: number;
  name: string;
  description: string;
  currentDay: number;
  totalDays: number;
  status: string;
}

interface TrainingRecord {
  id: number;
  planId: number;
  date: string;
  dayNumber: number;
  completed: boolean;
  exercises: Exercise[];
  exercisesJson?: string;
  planName?: string;
}

const plans = ref&lt;TrainingPlan[]&gt;([]);
const activePlan = ref&lt;TrainingPlan | null&gt;(null);
const todayRecord = ref&lt;TrainingRecord | null&gt;(null);
const recentRecords = ref&lt;TrainingRecord[]&gt;([]);

const getToken = () =&gt; {
  return localStorage.getItem('token');
};

const apiClient = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json'
  }
});

apiClient.interceptors.request.use((config) =&gt; {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

const initParticles = () =&gt; {
  const particleBg = document.getElementById('particleBg');
  if (!particleBg) return;
  
  for (let i = 0; i &lt; 20; i++) {
    const particle = document.createElement('div');
    particle.className = 'particle';
    const size = Math.random() * 30 + 10;
    particle.style.width = `${size}px`;
    particle.style.height = `${size}px`;
    particle.style.left = `${Math.random() * 100}%`;
    particle.style.top = `${Math.random() * 100}%`;
    particle.style.animationDelay = `${Math.random() * 15}s`;
    particle.style.animationDuration = `${Math.random() * 10 + 10}s`;
    
    const colors = ['#FF6B35', '#4ECDC4', '#FFD166', '#EF476F'];
    const color = colors[Math.floor(Math.random() * colors.length)];
    particle.style.background = `radial-gradient(circle, ${color}40 0%, ${color}00 70%)`;
    
    particleBg.appendChild(particle);
  }
};

const getPlanProgress = (plan: TrainingPlan): number =&gt; {
  return (plan.currentDay / plan.totalDays) * 100;
};

const getStatusText = (status: string): string =&gt; {
  switch (status) {
    case 'active': return '进行中';
    case 'paused': return '已暂停';
    case 'completed': return '已完成';
    case 'stopped': return '已停止';
    default: return '未知';
  }
};

const formatDate = (dateString: string): string =&gt; {
  const date = new Date(dateString);
  return `${date.getMonth() + 1}月${date.getDate()}日`;
};

const fetchPlans = async () =&gt; {
  try {
    const response = await apiClient.get('/api/plan/list');
    if (response.data.code === 200 &amp;&amp; response.data.data) {
      plans.value = response.data.data;
      // 最近运动的计划在最前面
      plans.value.sort((a, b) =&gt; {
        if (a.status === 'active' &amp;&amp; b.status !== 'active') return -1;
        if (a.status !== 'active' &amp;&amp; b.status === 'active') return 1;
        return b.currentDay - a.currentDay;
      });
      // 设置活跃计划
      activePlan.value = plans.value.find(plan =&gt; plan.status === 'active') || null;
    }
  } catch (error) {
    console.error('获取计划列表失败:', error);
    useLocalData();
  }
};

const fetchTodayWorkout = async () =&gt; {
  try {
    const response = await apiClient.get('/api/workout/today');
    if (response.data.code === 200 &amp;&amp; response.data.data) {
      const record = response.data.data;
      if (record.exercisesJson) {
        record.exercises = JSON.parse(record.exercisesJson);
      }
      todayRecord.value = record;
    }
  } catch (error) {
    console.error('获取今日训练失败:', error);
  }
};

const fetchRecentRecords = async () =&gt; {
  try {
    const response = await apiClient.get('/api/workout/recent?limit=10');
    if (response.data.code === 200 &amp;&amp; response.data.data) {
      recentRecords.value = response.data.data.map((record: any) =&gt; {
        if (record.exercisesJson) {
          record.exercises = JSON.parse(record.exercisesJson);
        }
        return record;
      });
    }
  } catch (error) {
    console.error('获取训练记录失败:', error);
  }
};

const useLocalData = () =&gt; {
  plans.value = [
    {
      id: 1,
      name: '新手增肌计划',
      description: '专为新手设计的全身增肌训练计划',
      currentDay: 3,
      totalDays: 28,
      status: 'active'
    },
    {
      id: 2,
      name: '减脂塑形计划',
      description: '通过有氧运动和力量训练相结合的减脂计划',
      currentDay: 5,
      totalDays: 30,
      status: 'paused'
    },
    {
      id: 3,
      name: '耐力提升计划',
      description: '提高心肺功能和耐力的训练计划',
      currentDay: 2,
      totalDays: 21,
      status: 'active'
    }
  ];
  
  activePlan.value = plans.value.find(plan =&gt; plan.status === 'active') || null;
  
  todayRecord.value = {
    id: 1,
    planId: 1,
    date: new Date().toISOString().split('T')[0],
    dayNumber: 3,
    completed: false,
    exercises: [
      { id: 1, name: '俯卧撑', sets: 3, reps: 15, completed: false },
      { id: 2, name: '深蹲', sets: 4, reps: 12, completed: false },
      { id: 3, name: '平板支撑', sets: 3, reps: 60, completed: false }
    ]
  };
  
  recentRecords.value = [
    {
      id: 1,
      planId: 1,
      planName: '新手增肌计划',
      date: new Date(Date.now() - 86400000).toISOString().split('T')[0],
      dayNumber: 2,
      completed: true,
      exercises: []
    },
    {
      id: 2,
      planId: 1,
      planName: '新手增肌计划',
      date: new Date(Date.now() - 172800000).toISOString().split('T')[0],
      dayNumber: 1,
      completed: true,
      exercises: []
    },
    {
      id: 3,
      planId: 2,
      planName: '减脂塑形计划',
      date: new Date(Date.now() - 259200000).toISOString().split('T')[0],
      dayNumber: 5,
      completed: true,
      exercises: []
    }
  ];
};

const togglePlanStatus = async (plan: TrainingPlan, action: string) =&gt; {
  try {
    const newStatus = action === 'pause' ? 'paused' : action === 'resume' ? 'active' : 'stopped';
    const response = await apiClient.put(`/api/plan/status/${plan.id}`, {
      status: newStatus
    });
    if (response.data.code === 200) {
      plan.status = newStatus;
      // 重新排序计划
      plans.value.sort((a, b) =&gt; {
        if (a.status === 'active' &amp;&amp; b.status !== 'active') return -1;
        if (a.status !== 'active' &amp;&amp; b.status === 'active') return 1;
        return b.currentDay - a.currentDay;
      });
      // 重新设置活跃计划
      activePlan.value = plans.value.find(p =&gt; p.status === 'active') || null;
    }
  } catch (error) {
    console.error('更新计划状态失败:', error);
  }
};

const startTraining = async () =&gt; {
  if (!activePlan.value) return;
  
  const newRecord: TrainingRecord = {
    id: 0,
    planId: activePlan.value.id,
    date: new Date().toISOString().split('T')[0],
    dayNumber: activePlan.value.currentDay,
    completed: false,
    exercises: [
      { id: 1, name: '俯卧撑', sets: 3, reps: 15, completed: false },
      { id: 2, name: '深蹲', sets: 4, reps: 12, completed: false },
      { id: 3, name: '平板支撑', sets: 3, reps: 60, completed: false }
    ]
  };
  
  try {
    const response = await apiClient.post('/api/workout/record', {
      ...newRecord,
      exercisesJson: JSON.stringify(newRecord.exercises)
    });
    if (response.data.code === 200) {
      todayRecord.value = { ...response.data.data, exercises: newRecord.exercises };
    }
  } catch (error) {
    console.error('创建训练记录失败:', error);
    todayRecord.value = newRecord;
  }
};

const toggleExercise = async (exercise: Exercise) =&gt; {
  if (!todayRecord.value) return;
  
  const updatedExercises = todayRecord.value.exercises.map(ex =&gt; 
    ex.id === exercise.id ? { ...ex, completed: !ex.completed } : ex
  );
  
  todayRecord.value = {
    ...todayRecord.value,
    exercises: updatedExercises,
    completed: updatedExercises.every(ex =&gt; ex.completed)
  };
  
  try {
    await apiClient.put(`/api/workout/record/${todayRecord.value.id}`, {
      ...todayRecord.value,
      exercisesJson: JSON.stringify(updatedExercises)
    });
  } catch (error) {
    console.error('更新训练记录失败:', error);
  }
};

const completeTraining = async () =&gt; {
  if (!todayRecord.value || !activePlan.value) return;
  
  todayRecord.value = {
    ...todayRecord.value,
    completed: true,
    exercises: todayRecord.value.exercises.map(ex =&gt; ({ ...ex, completed: true }))
  };
  
  try {
    await apiClient.put(`/api/workout/record/${todayRecord.value.id}`, {
      ...todayRecord.value,
      exercisesJson: JSON.stringify(todayRecord.value.exercises)
    });
    
    activePlan.value.currentDay++;
    // 检查是否完成计划
    if (activePlan.value.currentDay &gt; activePlan.value.totalDays) {
      activePlan.value.status = 'completed';
      await apiClient.put(`/api/plan/status/${activePlan.value.id}`, {
        status: 'completed'
      });
    }
  } catch (error) {
    console.error('完成训练失败:', error);
  }
};

const navigateToCreatePlan = () =&gt; {
  // 导航到创建计划页面
  uni.navigateTo({
    url: '/pages/template/list'
  });
};

const navigateToMoreRecords = () =&gt; {
  // 导航到更多训练记录页面
  uni.navigateTo({
    url: '/pages/records/index'
  });
};

onMounted(() =&gt; {
  nextTick(() =&gt; {
    initParticles();
  });
  
  useLocalData();
  fetchPlans();
  fetchTodayWorkout();
  fetchRecentRecords();
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
.records-section,
.plans-section {
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
.records-section::before,
.plans-section::before {
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
.records-section:hover,
.plans-section:hover {
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

/* 计划列表 */
.plan-list {
  margin-top: 15rpx;
}

.plan-card {
  margin-bottom: 20rpx;
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15rpx;
}

.plan-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  transition: color 0.3s ease;
  flex: 1;
}

.plan-card:hover .plan-name {
  color: #FF6B35;
}

.plan-actions {
  display: flex;
  gap: 10rpx;
}

.plan-action-btn {
  padding: 8rpx 16rpx;
  border: none;
  border-radius: 8rpx;
  font-size: 20rpx;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 60rpx;
  text-align: center;
}

.plan-action-btn.pause {
  background: linear-gradient(135deg, #FFD166, #FFE066);
  color: #333;
}

.plan-action-btn.resume {
  background: linear-gradient(135deg, #4ECDC4, #45B7AA);
  color: white;
}

.plan-action-btn.stop {
  background: linear-gradient(135deg, #EF476F, #E74C3C);
  color: white;
}

.plan-action-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);
}

.plan-description {
  font-size: 24rpx;
  color: #666;
  margin-bottom: 20rpx;
  line-height: 1.5;
}

.plan-status {
  position: absolute;
  top: 20rpx;
  right: 20rpx;
  padding: 6rpx 12rpx;
  border-radius: 12rpx;
  font-size: 18rpx;
  font-weight: 500;
}

.plan-status.active {
  background: rgba(78, 205, 196, 0.2);
  color: #4ECDC4;
}

.plan-status.paused {
  background: rgba(255, 209, 102, 0.2);
  color: #FFD166;
}

.plan-status.completed {
  background: rgba(76, 175, 80, 0.2);
  color: #4CAF50;
}

.plan-status.stopped {
  background: rgba(239, 71, 111, 0.2);
  color: #EF476F;
}

/* 无计划状态 */
.no-plans {
  text-align: center;
  padding: 80rpx 0;
  background: rgba(255, 107, 53, 0.03);
  border-radius: 16rpx;
  margin-top: 10rpx;
}

.no-plans-text {
  font-size: 26rpx;
  color: #999;
  margin-bottom: 25rpx;
}

.create-plan-btn {
  background: linear-gradient(135deg, #FF6B35, #FF8E53);
  color: white;
  border: none;
  border-radius: 12rpx;
  padding: 20rpx 40rpx;
  font-size: 24rpx;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.create-plan-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 8rpx 20rpx rgba(255, 107, 53, 0.4);
}

/* 进度条设计 */
.progress-container {
  margin-top: 20rpx;
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
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15rpx;
}

.more-btn {
  background: none;
  border: none;
  color: #FF6B35;
  font-size: 22rpx;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 0;
}

.more-btn:hover {
  text-decoration: underline;
  transform: scale(1.05);
}

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
  .records-section,
  .plans-section {
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
  
  .plan-action-btn {
    font-size: 18rpx;
    padding: 6rpx 12rpx;
  }
}
</style>