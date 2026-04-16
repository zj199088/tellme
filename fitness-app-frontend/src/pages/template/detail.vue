<template>
  <div class="template-detail-container">
    <div class="particle-bg" id="particleBg"></div>
    <div class="glow-orbs">
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
      <div class="orb orb-3"></div>
    </div>
    
    <div class="header">
      <div class="header-content">
        <div class="back-button" @click="goBack">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="15 18 9 12 15 6"></polyline>
          </svg>
        </div>
        <h1 class="title neon-glow">{{ template?.name || '模板详情' }}</h1>
        <div class="header-spacer"></div>
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
      </div>
      <div v-else-if="template" class="template-detail">
        <div class="template-card glow-card animate-in">
          <div class="template-image-container">
            <img :src="template.image" class="template-image" alt="模板图片" />
            <div class="image-overlay"></div>
          </div>
          
          <div class="template-info">
            <h2 class="template-name">{{ template.name }}</h2>
            <p class="template-description">{{ template.description }}</p>
            <div class="template-meta">
              <span class="difficulty-tag" :class="template.difficulty">
                {{ getDifficultyText(template.difficulty) }}
              </span>
            </div>
          </div>
        </div>
        
        <div class="training-days glow-card animate-in" style="animation-delay: 0.1s">
          <h2 class="section-title">
            <span class="title-icon">📋</span>
            <span>训练计划</span>
            <span class="title-glow"></span>
          </h2>
          <div class="days-list">
            <div v-for="(day, index) in templateDays" :key="day.id" 
                 class="day-card animate-in" 
                 :class="{ expanded: expandedDayId === day.id }"
                 :style="{ animationDelay: `${index * 0.1}s` }"
                 @click="toggleDay(day.id)">
              <div class="day-header">
                <div class="day-info">
                  <h3 class="day-name">{{ getDayName(day.dayOfWeek) }}</h3>
                  <span v-if="day.isRestDay" class="rest-day-tag">休息日</span>
                  <span v-else class="duration">{{ day.estimatedDuration }}分钟</span>
                </div>
                <div class="expand-icon" :class="{ expanded: expandedDayId === day.id }">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="6 9 12 15 18 9"></polyline>
                  </svg>
                </div>
              </div>
              
              <div v-if="day.isRestDay && expandedDayId === day.id" class="rest-note">
                <span class="rest-icon">💤</span>
                <p>{{ day.restNote || '好好休息，让肌肉恢复' }}</p>
              </div>
              
              <div v-else-if="!day.isRestDay && expandedDayId === day.id" class="exercises">
                <div v-for="exercise in getExercisesByDayId(day.id)" 
                     :key="exercise.id" 
                     class="exercise-item">
                  <div class="exercise-info">
                    <h4 class="exercise-name">{{ exercise.exerciseName }}</h4>
                    <p class="exercise-detail">
                      {{ exercise.sets }}组
                      <span v-if="exercise.reps"> × {{ exercise.reps }}</span>
                      <span v-else-if="exercise.duration"> × {{ exercise.duration }}</span>
                    </p>
                    <p v-if="exercise.note" class="exercise-note">{{ exercise.note }}</p>
                  </div>
                  <div class="exercise-icon">⚡</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="create-plan-section glow-card animate-in" style="animation-delay: 0.2s">
          <h2 class="section-title">
            <span class="title-icon">✨</span>
            <span>创建计划</span>
            <span class="title-glow"></span>
          </h2>
          
          <div class="form">
            <div class="form-item">
              <label class="label">计划名称</label>
              <input type="text" v-model="planForm.name" 
                     placeholder="输入计划名称" class="input" />
            </div>
            
            <div class="form-item">
              <label class="label">健身目标</label>
              <select v-model="planForm.goal" class="input">
                <option value="">请选择健身目标</option>
                <option value="增肌">增肌</option>
                <option value="减脂">减脂</option>
                <option value="塑形">塑形</option>
                <option value="增强耐力">增强耐力</option>
                <option value="提高力量">提高力量</option>
                <option value="改善体态">改善体态</option>
              </select>
            </div>
            
            <div class="form-item">
              <label class="label">训练周期</label>
              <input type="number" v-model="planForm.duration_weeks" 
                     placeholder="输入训练周期(周)" class="input" />
            </div>
            
            <div class="form-item">
              <label class="label">开始日期</label>
              <input type="date" v-model="planForm.start_date" class="input" />
            </div>
            
            <button class="create-button glow-button" @click="createPlan" :disabled="isCreating">
              <span>{{ isCreating ? '创建中...' : '从模板创建计划' }}</span>
              <span class="btn-glow"></span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import api from '../../utils/api';
import toast from '../../utils/toast';

const router = useRouter();
const route = useRoute();
const templateId = computed(() => parseInt(route.query.id as string) || 0);

const template = ref<any>(null);
const templateDays = ref<any[]>([]);
const templateExercises = ref<any[]>([]);
const loading = ref(true);
const error = ref('');
const isCreating = ref(false);
const expandedDayId = ref<number | null>(null);

const planForm = ref({
  name: '',
  goal: '',
  duration_weeks: 4,
  start_date: new Date().toISOString().split('T')[0]
});

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

onMounted(() => {
  nextTick(() => {
    initParticles();
  });
  
  fetchTemplateDetail();
  fetchTemplateDays();
});

const fetchTemplateDetail = async () => {
  try {
    loading.value = true;
    const response = await api.templates.getDetail(templateId.value);
    if (response.code === 200 && response.data) {
      template.value = response.data;
      planForm.value.name = `我的${response.data.name}`;
    } else {
      error.value = '加载模板详情失败，请重试';
    }
  } catch (err) {
    error.value = '加载模板详情失败，请重试';
    console.error(err);
  } finally {
    loading.value = false;
  }
};

const fetchTemplateDays = async () => {
  try {
    const response = await api.templates.getDays(templateId.value);
    if (response.code === 200 && response.data) {
      templateDays.value = response.data;
      
      for (const day of templateDays.value) {
        if (!day.isRestDay) {
          await fetchTemplateExercises(day.id);
        }
      }
      
      const sunday = templateDays.value.find((d: any) => d.dayOfWeek === 1);
      if (sunday) {
        expandedDayId.value = sunday.id;
      }
    }
  } catch (err) {
    console.error('加载训练日失败', err);
  }
};

const toggleDay = (dayId: number) => {
  if (expandedDayId.value === dayId) {
    expandedDayId.value = null;
  } else {
    expandedDayId.value = dayId;
  }
};

const fetchTemplateExercises = async (templateDayId: number) => {
  try {
    const response = await api.templates.getExercises(templateDayId);
    if (response.code === 200 && response.data) {
      templateExercises.value = [...templateExercises.value, ...response.data];
    }
  } catch (err) {
    console.error('加载训练动作失败', err);
  }
};

const getExercisesByDayId = (dayId: number) => {
  return templateExercises.value.filter(exercise => exercise.templateDayId === dayId);
};

const getDayName = (dayOfWeek: number) => {
  const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
  return days[dayOfWeek - 1] || `第${dayOfWeek}天`;
};

const getDifficultyText = (difficulty: string) => {
  const map = {
    beginner: '新手',
    intermediate: '中级',
    advanced: '高级'
  };
  return map[difficulty as keyof typeof map] || difficulty;
};

const createPlan = async () => {
  if (!planForm.value.name || !planForm.value.goal || !planForm.value.duration_weeks || !planForm.value.start_date) {
    toast.warning('请填写所有必填项');
    return;
  }

  try {
    isCreating.value = true;
    const response = await api.templates.createPlan({
      templateId: templateId.value,
      name: planForm.value.name,
      goal: planForm.value.goal,
      difficulty: template.value.difficulty,
      durationWeeks: planForm.value.duration_weeks
    });

    if (response.code === 200) {
      toast.success('计划创建成功');
      router.push('/pages/home/index');
    } else {
      toast.error('计划创建失败，请重试');
    }
  } catch (err) {
    toast.error('网络错误，请重试');
    console.error(err);
  } finally {
    isCreating.value = false;
  }
};

const goBack = () => {
  router.back();
};
</script>

<style scoped>
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
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
    transform: translateY(-15px) rotate(2deg);
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
    transform: translate(30px, -30px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.9);
  }
}

@keyframes neon-pulse {
  0%, 100% {
    text-shadow: 0 0 10px var(--neon-cyan), 0 0 20px var(--neon-cyan);
  }
  50% {
    text-shadow: 0 0 20px var(--neon-cyan), 0 0 40px var(--neon-cyan), 0 0 60px var(--neon-purple);
  }
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.animate-in {
  animation: fadeInUp 0.6s ease forwards;
}

.template-detail-container {
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
  width: 400px;
  height: 400px;
  background: var(--neon-cyan);
  top: 10%;
  left: -10%;
  animation-delay: 0s;
}

.orb-2 {
  width: 350px;
  height: 350px;
  background: var(--neon-purple);
  top: 50%;
  right: -10%;
  animation-delay: -7s;
}

.orb-3 {
  width: 300px;
  height: 300px;
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
  padding: 20px 20px 25px;
  text-align: center;
  border-bottom-left-radius: 40px;
  border-bottom-right-radius: 40px;
  box-shadow: 0 8px 40px rgba(0, 245, 255, 0.15);
  overflow: hidden;
  border-bottom: 1px solid var(--border-color);
  z-index: 1;
}

.header-content {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 15px;
}

.back-button {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: rgba(0, 245, 255, 0.1);
  border: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  color: var(--neon-cyan);
}

.back-button:hover {
  background: rgba(0, 245, 255, 0.2);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.3);
  transform: translateX(-4px);
}

.back-button svg {
  width: 24px;
  height: 24px;
}

.header-spacer {
  width: 44px;
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
  font-size: 28px;
  font-weight: bold;
  position: relative;
  z-index: 1;
  background: var(--gradient-neon);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.neon-glow {
  animation: neon-pulse 3s ease-in-out infinite;
}

.content {
  padding: 24px 16px;
  padding-bottom: 120px;
  position: relative;
  z-index: 1;
}

.loading,
.error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  gap: 20px;
}

.loading-spinner {
  width: 60px;
  height: 60px;
  border: 4px solid rgba(0, 245, 255, 0.1);
  border-top: 4px solid var(--neon-cyan);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  box-shadow: var(--glow-cyan);
}

.error-icon {
  font-size: 60px;
}

.loading p,
.error p {
  font-size: 18px;
  color: var(--text-secondary);
}

.template-card,
.training-days,
.create-plan-section {
  background: var(--bg-card);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border-radius: 30px;
  padding: 30px;
  margin-bottom: 24px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.4);
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  overflow: hidden;
  border: 1px solid var(--border-color);
}

.template-card::before,
.training-days::before,
.create-plan-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--gradient-neon);
  border-top-left-radius: 30px;
  border-top-right-radius: 30px;
}

.template-card::after,
.training-days::after,
.create-plan-section::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 30px;
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
  transform: translateY(-6px) scale(1.01);
  box-shadow: 0 16px 60px rgba(0, 245, 255, 0.25);
}

.template-image-container {
  position: relative;
  width: 100%;
  height: 220px;
  border-radius: 24px;
  overflow: hidden;
  margin-bottom: 24px;
  border: 1px solid var(--border-color);
}

.template-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s ease;
}

.template-card:hover .template-image {
  transform: scale(1.05);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, transparent 0%, rgba(10, 10, 15, 0.8) 100%);
}

.template-info {
  position: relative;
  z-index: 1;
}

.template-name {
  font-size: 26px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 12px;
  background: var(--gradient-neon);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.template-description {
  font-size: 16px;
  color: var(--text-secondary);
  margin-bottom: 16px;
  line-height: 1.6;
}

.template-meta {
  display: flex;
  align-items: center;
}

.difficulty-tag {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  width: fit-content;
}

.difficulty-tag.beginner {
  background: rgba(0, 255, 136, 0.15);
  color: var(--neon-green);
  border: 1px solid var(--neon-green);
  box-shadow: 0 0 10px rgba(0, 255, 136, 0.2);
}

.difficulty-tag.intermediate {
  background: rgba(255, 209, 102, 0.15);
  color: var(--neon-orange);
  border: 1px solid var(--neon-orange);
  box-shadow: 0 0 10px rgba(255, 107, 53, 0.2);
}

.difficulty-tag.advanced {
  background: rgba(255, 0, 110, 0.15);
  color: var(--neon-pink);
  border: 1px solid var(--neon-pink);
  box-shadow: 0 0 10px rgba(255, 0, 110, 0.2);
}

.section-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 24px;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 12px;
  position: relative;
}

.title-icon {
  font-size: 28px;
}

.title-glow {
  position: absolute;
  bottom: -8px;
  left: 0;
  width: 60px;
  height: 3px;
  background: var(--gradient-cyan);
  border-radius: 2px;
  box-shadow: 0 0 10px var(--neon-cyan);
}

.days-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.day-card {
  background: rgba(0, 0, 0, 0.3);
  border-radius: 20px;
  padding: 20px;
  border: 1px solid rgba(0, 245, 255, 0.1);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.day-card.expanded {
  border-color: rgba(0, 245, 255, 0.4);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.15);
}

.day-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--gradient-cyan);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.day-card:hover {
  background: rgba(0, 245, 255, 0.05);
  border-color: rgba(0, 245, 255, 0.3);
}

.day-card:hover::before {
  opacity: 1;
}

.day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.day-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.day-name {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}

.rest-day-tag {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 12px;
  background: rgba(255, 0, 110, 0.15);
  color: var(--neon-pink);
  border: 1px solid rgba(255, 0, 110, 0.3);
}

.duration {
  font-size: 14px;
  color: var(--text-muted);
}

.expand-icon {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--neon-cyan);
  transition: transform 0.3s ease;
}

.expand-icon svg {
  width: 20px;
  height: 20px;
}

.expand-icon.expanded {
  transform: rotate(180deg);
}

.rest-note {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  animation: slideDown 0.3s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.rest-icon {
  font-size: 28px;
}

.rest-note p {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.exercises {
  margin-top: 8px;
  animation: slideDown 0.3s ease;
}

.exercise-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px 0;
  border-bottom: 1px solid rgba(0, 245, 255, 0.1);
  transition: all 0.3s ease;
}

.exercise-item:last-child {
  border-bottom: none;
}

.exercise-item:hover {
  padding-left: 12px;
  background: rgba(0, 245, 255, 0.03);
  border-radius: 12px;
  margin-left: -12px;
  margin-right: -12px;
  padding-right: 12px;
}

.exercise-info {
  flex: 1;
}

.exercise-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 6px;
  transition: color 0.3s ease;
}

.exercise-item:hover .exercise-name {
  color: var(--neon-cyan);
}

.exercise-detail {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 4px;
}

.exercise-note {
  font-size: 12px;
  color: var(--text-muted);
}

.exercise-icon {
  font-size: 20px;
  opacity: 0.7;
  transition: all 0.3s ease;
}

.exercise-item:hover .exercise-icon {
  opacity: 1;
  transform: scale(1.2);
}

.form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.label {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}

.input {
  width: 100%;
  padding: 14px 18px;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid var(--border-color);
  border-radius: 14px;
  font-size: 15px;
  color: var(--text-primary);
  transition: all 0.3s ease;
}

.input::placeholder {
  color: var(--text-muted);
}

.input:focus {
  outline: none;
  border-color: var(--neon-cyan);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.2);
  background: rgba(0, 245, 255, 0.05);
}

/* 下拉框样式 */
select.input {
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2300f5ff' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 16px center;
  background-size: 20px;
  padding-right: 48px;
}

select.input option {
  background: var(--bg-tertiary);
  color: var(--text-primary);
  padding: 10px;
}

.create-button {
  position: relative;
  overflow: hidden;
  background: var(--gradient-cyan);
  color: white;
  border: none;
  border-radius: 16px;
  padding: 18px 32px;
  font-size: 17px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.3);
  margin-top: 10px;
}

.create-button:disabled {
  background: var(--bg-tertiary);
  cursor: not-allowed;
  box-shadow: none;
}

.create-button:not(:disabled):hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 0 30px rgba(0, 245, 255, 0.5), 0 0 60px rgba(0, 245, 255, 0.3);
}

.create-button:not(:disabled):active {
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

.create-button:not(:disabled):hover .btn-glow {
  left: 100%;
}

@media (max-width: 768px) {
  .content {
    padding: 20px 12px;
    padding-bottom: 100px;
  }
  
  .template-card,
  .training-days,
  .create-plan-section {
    padding: 24px;
  }
  
  .title {
    font-size: 24px;
  }
  
  .section-title {
    font-size: 20px;
  }
  
  .template-name {
    font-size: 22px;
  }
}
</style>
