<template>
  <div class="track-container">
    <div class="particle-bg" id="particleBg"></div>
    <div class="glow-orbs">
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
      <div class="orb orb-3"></div>
    </div>
    
    <div class="header">
      <div class="header-content">
        <div class="back-btn" @click="goBack">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
            <polyline points="15 18 9 12 15 6"></polyline>
          </svg>
        </div>
        <div class="header-icon">🏋️</div>
        <h1 class="title neon-glow">跟踪训练</h1>
      </div>
      <div class="header-bg"></div>
      <div class="scanline"></div>
    </div>
    
    <div class="content">
      <div v-if="loading" class="loading animate-in">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
      
      <div v-else-if="error" class="error animate-in">
        <div class="error-icon">⚠️</div>
        <p>{{ error }}</p>
        <button class="retry-button glow-button" @click="loadWorkoutData">
          <span>重试</span>
          <span class="btn-glow"></span>
        </button>
      </div>
      
      <div v-else>
        <div class="workout-info" :class="['workout-info', 'animate-in', 'glow-card']">
          <h2 class="section-title">
            <span class="title-icon">📅</span>
            <span>训练日信息</span>
            <span class="title-glow"></span>
          </h2>
          <div class="info-content">
            <p class="plan-name">{{ planName }}</p>
            <p class="day-info">第 {{ dayNumber }} 天</p>
            <p class="date-info">{{ formatDate(new Date()) }}</p>
            <p v-if="schedule?.estimatedDuration" class="duration-info">预计时长: {{ schedule.estimatedDuration }} 分钟</p>
          </div>
        </div>

        <div class="exercises-section" :class="['exercises-section', 'animate-in', 'glow-card']">
          <h2 class="section-title">
            <span class="title-icon">💪</span>
            <span>今日动作</span>
            <span class="title-glow"></span>
          </h2>
          <div class="exercise-list">
            <div class="exercise-item" v-for="exercise in exercises" :key="exercise.id">
              <div class="exercise-header">
                <h3 class="exercise-name">{{ exercise.exerciseName }}</h3>
                <div class="exercise-meta">
                  <span class="sets-info">
                    {{ exercise.sets }} 组 × {{ exercise.reps || exercise.duration }}
                  </span>
                  <span v-if="exercise.weight" class="weight-info">{{ exercise.weight }} kg</span>
                </div>
              </div>
              <div class="sets-container">
                <div 
                  class="set-checkbox" 
                  v-for="(checked, index) in exercise.completedSets" 
                  :key="index"
                  :class="{ checked: checked }"
                  @click="toggleSet(exercise, index)"
                >
                  <span class="set-number">{{ index + 1 }}</span>
                  <svg v-if="checked" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                    <polyline points="20 6 9 17 4 12"></polyline>
                  </svg>
                </div>
              </div>
              <div class="exercise-actions" v-if="isExerciseCompleted(exercise)">
                <button 
                  class="complete-exercise-btn glow-button" 
                  :class="{ disabled: isSubmitting }"
                  @click="completeExercise(exercise)"
                >
                  <span>{{ isSubmitting ? '提交中...' : '完成动作' }}</span>
                  <span class="btn-glow"></span>
                </button>
              </div>
            </div>
          </div>
          <button class="complete-button glow-button" :class="{ disabled: !allSetsCompleted || isCompleting }" @click="completeWorkout">
            <span>{{ isCompleting ? '保存中...' : '完成训练' }}</span>
            <span class="btn-glow"></span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import api, { ScheduleExercise, WorkoutSchedule } from '../../utils/api';

interface TrackExercise extends ScheduleExercise {
  completedSets: boolean[];
}

const router = useRouter();
const route = useRoute();

const planId = ref<number>(0);
const planName = ref<string>('');
const dayNumber = ref<number>(1);
const exercises = ref<TrackExercise[]>([]);
const schedule = ref<WorkoutSchedule | null>(null);
const loading = ref(true);
const error = ref('');
const isCompleting = ref(false);
const isSubmitting = ref(false);

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

const formatDate = (date: Date): string => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}年${month}月${day}日`;
};

const loadWorkoutData = async () => {
  try {
    loading.value = true;
    error.value = '';
    
    const id = route.query.planId ? parseInt(route.query.planId as string) : 1;
    planId.value = id;
    
    const todayResponse = await api.workout.getToday(id);
    if (todayResponse.code === 200 && todayResponse.data) {
      schedule.value = todayResponse.data.schedule;
      
      if (todayResponse.data.exercises) {
        exercises.value = todayResponse.data.exercises.map(ex => ({
          ...ex,
          completedSets: new Array(ex.sets).fill(false)
        }));
      }
      
      const plansResponse = await api.plans.getList();
      if (plansResponse.code === 200 && plansResponse.data) {
        const plan = plansResponse.data.find(p => p.id === id);
        planName.value = plan?.name || '我的训练计划';
      }
      
      dayNumber.value = schedule.value?.dayOfWeek || 1;
    }
  } catch (err) {
    console.error('加载训练数据失败:', err);
    error.value = '加载训练数据失败，请重试';
  } finally {
    loading.value = false;
  }
};

const toggleSet = async (exercise: TrackExercise, index: number) => {
  exercise.completedSets[index] = !exercise.completedSets[index];
  
  // 每组完成后立即提交
  try {
    const record = {
      planId: planId.value,
      scheduleId: schedule.value?.id,
      scheduleExerciseId: exercise.id,
      exerciseId: exercise.exerciseId,
      exerciseName: exercise.exerciseName,
      date: new Date().toISOString().split('T')[0],
      setsCompleted: JSON.stringify(exercise.completedSets.map(c => c ? 1 : 0)),
      weight: exercise.weight || 0,
      duration: 0,
      notes: ''
    };
    
    await api.workout.createBatch([record]);
    
    // 显示提交成功反馈
    const setElement = document.querySelector(`.set-checkbox:nth-child(${index + 1})`);
    if (setElement) {
      setElement.classList.add('submitted');
      setTimeout(() => {
        setElement.classList.remove('submitted');
      }, 1000);
    }
  } catch (err) {
    console.error('提交组记录失败:', err);
    // 提交失败时恢复原状态
    exercise.completedSets[index] = !exercise.completedSets[index];
  }
};

const isExerciseCompleted = (exercise: TrackExercise) => {
  return exercise.completedSets.every(completed => completed);
};

const allSetsCompleted = computed(() => {
  if (exercises.value.length === 0) return false;
  return exercises.value.every(exercise => 
    exercise.completedSets.every(completed => completed)
  );
});

const goBack = () => {
  router.push('/pages/home/index');
};

const completeExercise = async (exercise: TrackExercise) => {
  if (!isExerciseCompleted(exercise) || isSubmitting.value) return;
  
  try {
    isSubmitting.value = true;
    
    // 创建训练记录（即使没有schedule也能创建）
    const record = {
      planId: planId.value,
      scheduleId: schedule.value?.id,
      scheduleExerciseId: exercise.id,
      exerciseId: exercise.exerciseId,
      exerciseName: exercise.exerciseName,
      date: new Date().toISOString().split('T')[0],
      setsCompleted: JSON.stringify(exercise.completedSets.map(c => c ? 1 : 0)),
      weight: exercise.weight || 0,
      duration: 0,
      notes: ''
    };
    
    await api.workout.createRecord(record);
    
    // 显示提交成功反馈
    alert('动作完成！记录已保存');
  } catch (err) {
    console.error('提交动作记录失败:', err);
    alert('提交动作记录失败，请重试');
  } finally {
    isSubmitting.value = false;
  }
};

const completeWorkout = async () => {
  if (!allSetsCompleted.value || isCompleting.value) return;
  
  try {
    isCompleting.value = true;
    
    const records = exercises.value.map(exercise => ({
      planId: planId.value,
      scheduleId: schedule.value?.id,
      scheduleExerciseId: exercise.id,
      exerciseId: exercise.exerciseId,
      exerciseName: exercise.exerciseName,
      date: new Date().toISOString().split('T')[0],
      setsCompleted: JSON.stringify(exercise.completedSets.map(c => c ? 1 : 0)),
      weight: exercise.weight || 0,
      duration: 0,
      notes: ''
    }));
    
    await api.workout.createBatch(records);
    
    alert('训练完成！记录已保存');
    router.push('/pages/home/index');
  } catch (err) {
    console.error('保存训练记录失败:', err);
    alert('保存训练记录失败，请重试');
  } finally {
    isCompleting.value = false;
  }
};

onMounted(() => {
  nextTick(() => {
    initParticles();
  });
  loadWorkoutData();
});
</script>

<style scoped>
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.loading,
.error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 20px;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid rgba(0, 245, 255, 0.1);
  border-top: 4px solid var(--neon-cyan);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  box-shadow: 0 0 20px var(--neon-cyan);
}

.loading p,
.error p {
  color: var(--text-secondary);
  font-size: 16px;
}

.error-icon {
  font-size: 60px;
}

.retry-button {
  padding: 12px 32px;
  margin-top: 10px;
  position: relative;
  overflow: hidden;
  background: var(--gradient-cyan);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.3);
}

.duration-info {
  font-size: 13px;
  color: var(--text-muted);
  margin-top: 5px;
}

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

.track-container {
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

.back-btn {
  position: absolute;
  left: 15.0px;
  top: 50%;
  transform: translateY(-50%);
  width: 36.0px;
  height: 36.0px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  background: rgba(0, 245, 255, 0.1);
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: rgba(0, 245, 255, 0.2);
  border-color: var(--neon-cyan);
  box-shadow: 0 0 15px rgba(0, 245, 255, 0.3);
}

.back-btn svg {
  width: 20.0px;
  height: 20.0px;
  color: var(--neon-cyan);
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

.workout-info,
.exercises-section {
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

.workout-info::before,
.exercises-section::before {
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

.workout-info::after,
.exercises-section::after {
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

.info-content {
  text-align: center;
}

.plan-name {
  font-size: 18.0px;
  font-weight: 700;
  color: var(--neon-cyan);
  margin-bottom: 8.0px;
}

.day-info {
  font-size: 14.0px;
  color: var(--text-primary);
  margin-bottom: 4.0px;
}

.date-info {
  font-size: 12.0px;
  color: var(--text-muted);
}

.exercise-list {
  margin-top: 10.0px;
}

.exercise-item {
  padding: 14.0px 0;
  border-bottom: 1px solid rgba(0, 245, 255, 0.1);
  transition: all 0.3s ease;
  position: relative;
}

.exercise-item:last-child {
  border-bottom: none;
}

.exercise-item:hover {
  background: rgba(0, 245, 255, 0.05);
  border-radius: 8.0px;
  padding-left: 10.0px;
  padding-right: 10.0px;
  margin-left: -10.0px;
  margin-right: -10.0px;
}

.exercise-actions {
  margin-top: 15.0px;
  text-align: right;
}

.complete-exercise-btn {
  padding: 8.0px 16.0px;
  position: relative;
  overflow: hidden;
  background: var(--gradient-cyan);
  color: white;
  border: none;
  border-radius: 6.0px;
  font-size: 12.0px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  box-shadow: 0 0 15px rgba(0, 245, 255, 0.3);
}

.complete-exercise-btn.disabled {
  background: var(--bg-tertiary);
  color: var(--text-muted);
  cursor: not-allowed;
  box-shadow: none;
}

.complete-exercise-btn:not(.disabled):hover {
  transform: translateY(-1.0px) scale(1.02);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.5), 0 0 40px rgba(0, 245, 255, 0.3);
}

.complete-exercise-btn:active {
  transform: translateY(0) scale(0.98);
}

.exercise-header {
  margin-bottom: 10.0px;
}

.exercise-name {
  font-size: 15.0px;
  font-weight: 600;
  margin-bottom: 5.0px;
  color: var(--text-primary);
  transition: all 0.3s ease;
}

.exercise-item:hover .exercise-name {
  color: var(--neon-cyan);
}

.exercise-meta {
  display: flex;
  gap: 10.0px;
  font-size: 11.0px;
  color: var(--text-secondary);
}

.sets-container {
  display: flex;
  gap: 8.0px;
  flex-wrap: wrap;
}

.set-checkbox {
  width: 44.0px;
  height: 44.0px;
  border: 2px solid var(--border-color);
  border-radius: 10.0px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 12.0px;
  color: white;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  overflow: hidden;
  background: transparent;
  gap: 2.0px;
}

.set-checkbox::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(0, 245, 255, 0.2), transparent);
  transition: left 0.5s;
}

.set-checkbox:hover::before {
  left: 100%;
}

.set-checkbox:hover {
  border-color: var(--neon-cyan);
  box-shadow: 0 0 15px rgba(0, 245, 255, 0.3);
}

.set-checkbox.checked {
  background: var(--gradient-cyan);
  border-color: var(--neon-cyan);
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.5);
}

.set-checkbox.submitted {
  animation: submit-pulse 1s ease-in-out;
  box-shadow: 0 0 30px rgba(0, 255, 136, 0.8), 0 0 60px rgba(0, 255, 136, 0.5);
  background: linear-gradient(135deg, var(--neon-green), var(--neon-green));
  border-color: var(--neon-green);
}

@keyframes submit-pulse {
  0% {
    transform: scale(1.1) rotate(5deg);
  }
  50% {
    transform: scale(1.2) rotate(5deg);
  }
  100% {
    transform: scale(1.1) rotate(5deg);
  }
}

.set-checkbox svg {
  width: 16.0px;
  height: 16.0px;
  stroke-width: 3;
}

.set-number {
  font-weight: 700;
  font-size: 11.0px;
}

.set-checkbox.checked .set-number {
  display: none;
}

.complete-button {
  width: 100%;
  padding: 14.0px;
  margin-top: 15.0px;
  position: relative;
  overflow: hidden;
  background: var(--gradient-cyan);
  color: white;
  border: none;
  border-radius: 8.0px;
  font-size: 14.0px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.3);
}

.complete-button.disabled {
  background: var(--bg-tertiary);
  color: var(--text-muted);
  cursor: not-allowed;
  box-shadow: none;
}

.complete-button:not(.disabled):hover {
  transform: translateY(-2.0px) scale(1.02);
  box-shadow: 0 0 30px rgba(0, 245, 255, 0.5), 0 0 60px rgba(0, 245, 255, 0.3);
}

.complete-button:active {
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

.complete-button:not(.disabled):hover .btn-glow {
  left: 100%;
}

@media (max-width: 768px) {
  .content {
    padding: 10.0px 7.5px;
  }
  
  .workout-info,
  .exercises-section {
    padding: 15.0px;
  }
  
  .title {
    font-size: 17.0px;
  }
  
  .section-title {
    font-size: 14.0px;
  }
  
  .exercise-name {
    font-size: 13.0px;
  }
  
  .set-checkbox {
    width: 40.0px;
    height: 40.0px;
  }
}
</style>
