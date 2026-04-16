<template>
  <div class="home-container">
    <div class="particle-bg" id="particleBg"></div>
    <div class="glow-orbs">
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
      <div class="orb orb-3"></div>
    </div>
    
    <div class="header">
      <div class="header-content">
        <div class="header-icon">💪</div>
        <h1 class="title neon-glow">健身计划</h1>
      </div>
      <div class="header-bg"></div>
      <div class="scanline"></div>
    </div>
    
    <div class="content">
      <!-- 计划列表 -->
      <div class="plans-section" :class="['plans-section', 'animate-in']">
        <h2 class="section-title">
          <span class="title-icon">📋</span>
          <span>我的计划</span>
          <span class="title-glow"></span>
        </h2>
        <div class="plan-list" v-if="plans.length > 0">
          <div class="plan-card" v-for="(plan, index) in plans" :key="plan.id" :class="['plan-card', 'animate-in', 'glow-card']" :style="{ animationDelay: `${index * 0.1}s` }">
            <div class="plan-card-inner">
              <div class="plan-header">
                <div class="plan-title-group">
                  <h3 class="plan-name">{{ plan.name }}</h3>
                  <div class="plan-status" :class="plan.status">
                    {{ getStatusText(plan.status) }}
                  </div>
                </div>
                <div class="plan-actions">
                  <button class="plan-action-btn pause" @click="togglePlanStatus(plan, 'pause')" v-if="plan.status === 'active'">
                    <span>⏸</span>
                  </button>
                  <button class="plan-action-btn resume" @click="togglePlanStatus(plan, 'resume')" v-else-if="plan.status === 'paused'">
                    <span>▶</span>
                  </button>
                  <button class="plan-action-btn stop" @click="togglePlanStatus(plan, 'stop')">
                    <span>⏹</span>
                  </button>
                </div>
              </div>
              <p class="plan-description">{{ plan.description }}</p>
              <div class="progress-container">
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: getPlanProgress(plan) + '%' }"></div>
                  <div class="progress-glow" :style="{ left: getPlanProgress(plan) + '%' }"></div>
                </div>
                <div class="progress-info">
                  <p class="progress-text">第 {{ plan.currentDay }} 天 / 共 {{ plan.totalDays }} 天</p>
                  <p class="progress-percent">{{ Math.round(getPlanProgress(plan)) }}%</p>
                </div>
              </div>
              <button 
                v-if="plan.status === 'active'" 
                class="continue-btn glow-button" 
                @click="continueWorkout(plan)"
              >
                <span>继续锻炼</span>
                <span class="btn-glow"></span>
              </button>
            </div>
          </div>
        </div>
        <div class="no-plans" v-else>
          <div class="no-plans-icon">📝</div>
          <p class="no-plans-text">暂无计划</p>
          <button class="create-plan-btn glow-button" @click="navigateToCreatePlan">
            <span>创建计划</span>
            <span class="btn-glow"></span>
          </button>
        </div>
      </div>



      <!-- 训练记录 -->
      <div class="records-section" :class="['records-section', 'animate-in', 'glow-card']">
        <div class="section-header">
          <h2 class="section-title">
            <span class="title-icon">📊</span>
            <span>训练记录</span>
            <span class="title-glow"></span>
          </h2>
          <button class="more-btn" @click="navigateToMoreRecords">
            查看更多
            <span class="arrow">→</span>
          </button>
        </div>
        <div class="record-list" v-if="recentRecords.length > 0">
          <div class="record-item" v-for="(record, index) in recentRecords.slice(0, 3)" :key="record.id" :class="['record-item', 'animate-in']" :style="{ animationDelay: `${index * 0.1}s` }">
            <div class="record-date">{{ formatDate(record.date) }}</div>
            <div class="record-info">
              <h3 class="record-plan">{{ record.plan_name || record.planName }}</h3>
              <p class="record-exercises" v-if="record.exerciseName">
                {{ record.exerciseName }}
              </p>
              <p class="record-duration" v-if="record.duration">
                <span class="duration-icon">⏱</span>
                {{ Math.round((record.duration || 0) / 60) }}分钟
              </p>
              <p class="record-status" :class="{ completed: record.completed }">{{ record.completed ? '已完成' : '未完成' }}</p>
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
import { useRouter } from 'vue-router';
import api, { Exercise as ApiExercise, FitnessPlan, WorkoutRecord } from '../../utils/api';

const router = useRouter();

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

const plans = ref<TrainingPlan[]>([]);
const recentRecords = ref<TrainingRecord[]>([]);

const initParticles = () => {
  const particleBg = document.getElementById('particleBg');
  if (!particleBg) return;
  
  for (let i = 0; i < 25; i++) {
    const particle = document.createElement('div');
    particle.className = 'particle';
    const size = Math.random() * 25 + 8;
    particle.style.width = `${size}px`;
    particle.style.height = `${size}px`;
    particle.style.left = `${Math.random() * 100}%`;
    particle.style.top = `${Math.random() * 100}%`;
    particle.style.animationDelay = `${Math.random() * 15}s`;
    particle.style.animationDuration = `${Math.random() * 12 + 8}s`;
    
    const colors = ['var(--neon-cyan)', 'var(--neon-purple)', 'var(--neon-pink)', 'var(--neon-blue)'];
    const color = colors[Math.floor(Math.random() * colors.length)];
    particle.style.background = `radial-gradient(circle, ${color}60 0%, ${color}00 70%)`;
    
    particleBg.appendChild(particle);
  }
};

const getPlanProgress = (plan: TrainingPlan): number => {
  return (plan.currentDay / plan.totalDays) * 100;
};

const getStatusText = (status: string): string => {
  switch (status) {
    case 'active': return '进行中';
    case 'paused': return '已暂停';
    case 'completed': return '已完成';
    case 'stopped': return '已停止';
    default: return '未知';
  }
};

const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return `${date.getMonth() + 1}月${date.getDate()}日`;
};

const fetchPlans = async () => {
  try {
    const response = await api.plans.getList();
    if (response.code === 200 && response.data) {
      plans.value = response.data;
      plans.value.sort((a, b) => {
        if (a.status === 'active' && b.status !== 'active') return -1;
        if (a.status !== 'active' && b.status === 'active') return 1;
        return b.currentDay - a.currentDay;
      });
    }
  } catch (error) {
    console.error('获取计划列表失败:', error);
    useLocalData();
  }
};



const fetchRecentRecords = async () => {
  try {
    const response = await api.workout.getRecent(10);
    if (response.code === 200 && response.data) {
      recentRecords.value = response.data;
    }
  } catch (error) {
    console.error('获取训练记录失败:', error);
  }
};

const useLocalData = () => {
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

const togglePlanStatus = async (plan: TrainingPlan, action: string) => {
  try {
    const newStatus = action === 'pause' ? 'paused' : action === 'resume' ? 'active' : 'stopped';
    const response = await api.plans.updateStatus(plan.id, newStatus);
    if (response.code === 200) {
      plan.status = newStatus;
      plans.value.sort((a, b) => {
        if (a.status === 'active' && b.status !== 'active') return -1;
        if (a.status !== 'active' && b.status === 'active') return 1;
        return b.currentDay - a.currentDay;
      });
      activePlan.value = plans.value.find(p => p.status === 'active') || null;
    }
  } catch (error) {
    console.error('更新计划状态失败:', error);
  }
};



const navigateToCreatePlan = () => {
  router.push('/pages/template/list');
};

const navigateToMoreRecords = () => {
  router.push('/pages/records/index');
};

const continueWorkout = (plan: TrainingPlan) => {
  router.push({
    path: '/pages/workout/track',
    query: { planId: plan.id }
  });
};

onMounted(() => {
  nextTick(() => {
    initParticles();
  });
  
  useLocalData();
  fetchPlans();
  fetchRecentRecords();
});
</script>

<style scoped>
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(15.0px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-7.5px) rotate(2deg);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.8;
  }
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

@keyframes borderGlow {
  0%, 100% {
    box-shadow: 0 0 10px var(--neon-cyan), 0 0 20px var(--neon-cyan);
  }
  50% {
    box-shadow: 0 0 15px var(--neon-purple), 0 0 30px var(--neon-purple);
  }
}

@keyframes scanlineMove {
  0% {
    transform: translateY(-100%);
  }
  100% {
    transform: translateY(100vh);
  }
}

@keyframes orbFloat {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(15.0px, -15.0px) scale(1.1);
  }
  66% {
    transform: translate(-10.0px, 10.0px) scale(0.9);
  }
}

.animate-in {
  animation: fadeInUp 0.6s ease forwards;
}

.home-container {
  width: 100%;
  min-height: 100vh;
  background: var(--gradient-bg);
  position: relative;
  overflow: hidden;
  color: var(--text-primary);
}

.particle-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.particle {
  position: absolute;
  border-radius: 50%;
  animation: float 15s ease-in-out infinite;
}

.glow-orbs {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.3;
  animation: orbFloat 20s ease-in-out infinite;
}

.orb-1 {
  width: 200.0px;
  height: 200.0px;
  background: var(--neon-cyan);
  top: 10%;
  left: -10%;
  animation-delay: 0s;
}

.orb-2 {
  width: 175.0px;
  height: 175.0px;
  background: var(--neon-purple);
  top: 50%;
  right: -10%;
  animation-delay: -7s;
}

.orb-3 {
  width: 150.0px;
  height: 150.0px;
  background: var(--neon-pink);
  bottom: 10%;
  left: 30%;
  animation-delay: -14s;
}

.header {
  position: relative;
  background: var(--bg-card);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  padding: 25.0px 15.0px 20.0px;
  text-align: center;
  border-bottom-left-radius: 20.0px;
  border-bottom-right-radius: 20.0px;
  box-shadow: 0 4.0px 20.0px rgba(0, 245, 255, 0.15);
  overflow: hidden;
  border-bottom: 1px solid var(--border-color);
  z-index: 1;
}

.header-content {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 7.5px;
}

.header-icon {
  font-size: 22.0px;
  animation: pulse 2s ease-in-out infinite;
}

.header-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 20% 80%, rgba(0, 245, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(139, 92, 246, 0.1) 0%, transparent 50%);
  animation: float 8s ease-in-out infinite;
}

.scanline {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(to bottom, transparent, var(--neon-cyan), transparent);
  animation: scanlineMove 4s linear infinite;
  opacity: 0.3;
  z-index: 3;
}

.title {
  font-size: 20.0px;
  font-weight: bold;
  position: relative;
  z-index: 1;
  background: var(--gradient-neon);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.neon-glow {
  text-shadow: 0 0 20px var(--neon-cyan), 0 0 40px var(--neon-cyan);
  animation: neon-pulse 3s ease-in-out infinite;
}

@keyframes neon-pulse {
  0%, 100% {
    text-shadow: 0 0 10px var(--neon-cyan), 0 0 20px var(--neon-cyan);
  }
  50% {
    text-shadow: 0 0 20px var(--neon-cyan), 0 0 40px var(--neon-cyan), 0 0 60px var(--neon-purple);
  }
}

.content {
  padding: 15.0px 10.0px;
  padding-bottom: 80.0px;
  position: relative;
  z-index: 1;
}

.plan-card,
.records-section,
.plans-section {
  background: var(--bg-card);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border-radius: 15.0px;
  padding: 20.0px;
  margin-bottom: 15.0px;
  box-shadow: 0 4.0px 20.0px rgba(0, 0, 0, 0.4);
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  overflow: hidden;
  border: 1px solid var(--border-color);
}

.plan-card::before,
.records-section::before,
.plans-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2.0px;
  background: var(--gradient-neon);
  border-top-left-radius: 15.0px;
  border-top-right-radius: 15.0px;
}

.plan-card::after,
.records-section::after,
.plans-section::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 15.0px;
  padding: 1px;
  background: var(--gradient-neon);
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0;
  transition: opacity 0.4s ease;
  pointer-events: none;
}

.glow-card:hover::after {
  opacity: 1;
}

.glow-card:hover {
  transform: translateY(-3.0px) scale(1.01);
  box-shadow: 0 8.0px 30.0px rgba(0, 245, 255, 0.25);
}

.section-title {
  font-size: 16.0px;
  font-weight: bold;
  margin-bottom: 12.5px;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 6.0px;
  position: relative;
}

.title-icon {
  font-size: 18.0px;
}

.title-glow {
  position: absolute;
  bottom: -4.0px;
  left: 0;
  width: 30.0px;
  height: 1.5px;
  background: var(--gradient-cyan);
  border-radius: 1.0px;
  box-shadow: 0 0 10px var(--neon-cyan);
}

.plan-list {
  margin-top: 10.0px;
}

.plan-card {
  margin-bottom: 12.5px;
  padding: 0;
}

.plan-card-inner {
  padding: 17.5px;
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10.0px;
  gap: 10.0px;
}

.plan-title-group {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 5.0px;
}

.plan-name {
  font-size: 15.0px;
  font-weight: 700;
  color: var(--text-primary);
  transition: color 0.3s ease;
}

.plan-card:hover .plan-name {
  color: var(--neon-cyan);
}

.plan-status {
  display: inline-block;
  padding: 3.0px 8.0px;
  border-radius: 10.0px;
  font-size: 10.0px;
  font-weight: 600;
  width: fit-content;
}

.plan-status.active {
  background: rgba(0, 245, 255, 0.15);
  color: var(--neon-cyan);
  border: 1px solid var(--neon-cyan);
  box-shadow: 0 0 10px rgba(0, 245, 255, 0.2);
}

.plan-status.paused {
  background: rgba(255, 209, 102, 0.15);
  color: var(--neon-orange);
  border: 1px solid var(--neon-orange);
  box-shadow: 0 0 10px rgba(255, 107, 53, 0.2);
}

.plan-status.completed {
  background: rgba(0, 255, 136, 0.15);
  color: var(--neon-green);
  border: 1px solid var(--neon-green);
  box-shadow: 0 0 10px rgba(0, 255, 136, 0.2);
}

.plan-status.stopped {
  background: rgba(255, 0, 110, 0.15);
  color: var(--neon-pink);
  border: 1px solid var(--neon-pink);
  box-shadow: 0 0 10px rgba(255, 0, 110, 0.2);
}

.plan-actions {
  display: flex;
  gap: 6.0px;
}

.plan-action-btn {
  width: 25.0px;
  height: 25.0px;
  padding: 0;
  border: none;
  border-radius: 50%;
  font-size: 12.0px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  display: flex;
  align-items: center;
  justify-content: center;
}

.plan-action-btn.pause {
  background: linear-gradient(135deg, var(--neon-orange), var(--neon-orange));
  box-shadow: 0 0 15px rgba(255, 107, 53, 0.4);
}

.plan-action-btn.resume {
  background: var(--gradient-cyan);
  box-shadow: 0 0 15px rgba(0, 245, 255, 0.4);
}

.plan-action-btn.stop {
  background: linear-gradient(135deg, var(--neon-pink), var(--neon-pink));
  box-shadow: 0 0 15px rgba(255, 0, 110, 0.4);
}

.plan-action-btn:hover {
  transform: scale(1.15);
}

.plan-action-btn:active {
  transform: scale(0.95);
}

.plan-description {
  font-size: 12.0px;
  color: var(--text-secondary);
  margin-bottom: 12.5px;
  line-height: 1.6;
}

.progress-container {
  margin-top: 12.5px;
}

.progress-bar {
  width: 100%;
  height: 8.0px;
  background: var(--bg-tertiary);
  border-radius: 5.0px;
  overflow: hidden;
  margin-bottom: 6.0px;
  position: relative;
  border: 1px solid rgba(0, 245, 255, 0.1);
}

.progress-fill {
  height: 100%;
  background: var(--gradient-neon);
  border-radius: 5.0px;
  transition: width 0.8s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
}

.progress-fill::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  animation: shimmer 2s infinite;
}

.progress-glow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%) translateX(-50%);
  width: 25.0px;
  height: 25.0px;
  background: radial-gradient(circle, rgba(0, 245, 255, 0.5) 0%, transparent 70%);
  border-radius: 50%;
  animation: pulse 2s infinite;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.progress-text {
  font-size: 11.0px;
  color: var(--text-muted);
  font-weight: 500;
}

.progress-percent {
  font-size: 12.0px;
  font-weight: 700;
  background: var(--gradient-neon);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.continue-btn {
  width: 100%;
  margin-top: 12.5px;
  position: relative;
  overflow: hidden;
  background: var(--gradient-cyan);
  color: white;
  border: none;
  border-radius: 8.0px;
  padding: 10.0px 20.0px;
  font-size: 12.0px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  box-shadow: 0 0 15px rgba(0, 245, 255, 0.3);
}

.continue-btn:hover {
  transform: translateY(-2.0px) scale(1.02);
  box-shadow: 0 0 25px rgba(0, 245, 255, 0.5), 0 0 50px rgba(0, 245, 255, 0.3);
}

.continue-btn:active {
  transform: translateY(0) scale(0.98);
}

.no-plans {
  text-align: center;
  padding: 40.0px 20.0px;
  background: rgba(0, 245, 255, 0.03);
  border-radius: 12.0px;
  margin-top: 7.5px;
  border: 1px dashed var(--border-color);
}

.no-plans-icon {
  font-size: 40.0px;
  margin-bottom: 10.0px;
  animation: float 4s ease-in-out infinite;
}

.no-plans-text {
  font-size: 13.0px;
  color: var(--text-muted);
  margin-bottom: 15.0px;
}

.create-plan-btn,
.complete-button,
.start-button {
  position: relative;
  overflow: hidden;
  background: var(--gradient-cyan);
  color: white;
  border: none;
  border-radius: 8.0px;
  padding: 12.0px 24.0px;
  font-size: 13.0px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.3);
}

.glow-button:hover {
  transform: translateY(-2.0px) scale(1.02);
  box-shadow: 0 0 30px rgba(0, 245, 255, 0.5), 0 0 60px rgba(0, 245, 255, 0.3);
}

.glow-button:active {
  transform: translateY(0) scale(0.98);
}

.btn-glow {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s;
}

.glow-button:hover .btn-glow {
  left: 100%;
}



.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10.0px;
}

.more-btn {
  background: none;
  border: none;
  color: var(--neon-cyan);
  font-size: 11.0px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 4.0px 8.0px;
  border-radius: 6.0px;
  display: flex;
  align-items: center;
  gap: 3.0px;
}

.more-btn:hover {
  background: rgba(0, 245, 255, 0.1);
  transform: translateX(2.0px);
}

.more-btn .arrow {
  transition: transform 0.3s ease;
}

.more-btn:hover .arrow {
  transform: translateX(2.0px);
}

.record-list {
  margin-top: 10.0px;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14.0px 0;
  border-bottom: 1px solid rgba(0, 245, 255, 0.1);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.record-item:last-child {
  border-bottom: none;
}

.record-item:hover {
  background: rgba(0, 245, 255, 0.05);
  border-radius: 8.0px;
  padding-left: 10.0px;
  padding-right: 10.0px;
  margin-left: -10.0px;
  margin-right: -10.0px;
}

.record-date {
  font-size: 11.0px;
  color: var(--text-muted);
  min-width: 60.0px;
  font-weight: 500;
}

.record-info {
  flex: 1;
  margin-left: 10.0px;
}

.record-plan {
  font-size: 13.0px;
  font-weight: 600;
  margin-bottom: 4.0px;
  display: block;
  color: var(--text-primary);
  transition: color 0.3s ease;
}

.record-item:hover .record-plan {
  color: var(--neon-cyan);
}

.record-exercises {
  font-size: 11.0px;
  color: var(--text-secondary);
  margin-top: 2.0px;
  margin-bottom: 2.0px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
}

.record-duration {
  font-size: 10.0px;
  color: var(--text-muted);
  margin-top: 2.0px;
  margin-bottom: 2.0px;
  display: flex;
  align-items: center;
  gap: 3.0px;
}

.duration-icon {
  font-size: 10.0px;
}

.record-status {
  font-size: 10.0px;
  color: var(--text-muted);
  transition: all 0.3s ease;
  margin-top: 2.0px;
}

.record-status.completed {
  color: var(--neon-green);
  font-weight: 600;
}

.record-indicator {
  width: 7.0px;
  height: 7.0px;
  border-radius: 50%;
  background: var(--text-muted);
  transition: all 0.3s ease;
}

.record-indicator.completed {
  background: var(--neon-green);
  box-shadow: 0 0 12px rgba(0, 255, 136, 0.6);
  animation: pulse 2s infinite;
}

.no-records {
  font-size: 12.0px;
  color: var(--text-muted);
  text-align: center;
  padding: 30.0px 0;
  background: rgba(0, 245, 255, 0.03);
  border-radius: 8.0px;
  margin-top: 7.5px;
  border: 1px dashed var(--border-color);
}

@media (max-width: 768px) {
  .content {
    padding: 10.0px 7.5px;
  }
  
  .plan-card,
  .today-section,
  .records-section,
  .plans-section {
    padding: 15.0px;
  }
  
  .plan-card-inner {
    padding: 14.0px;
  }
  
  .title {
    font-size: 17.0px;
  }
  
  .section-title {
    font-size: 14.0px;
  }
  
  .plan-name {
    font-size: 13.0px;
  }
  
  .exercise-name {
    font-size: 12.0px;
  }
  
  .plan-action-btn {
    width: 22.0px;
    height: 22.0px;
    font-size: 10.0px;
  }
}
</style>
