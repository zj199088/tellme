<template>
  <div class="template-list-container">
    <div class="particle-bg" id="particleBg"></div>
    <div class="glow-orbs">
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
      <div class="orb orb-3"></div>
    </div>
    
    <div class="header">
      <div class="header-content">
        <div class="header-icon">✨</div>
        <h1 class="title neon-glow">定制计划</h1>
      </div>
      <div class="header-bg"></div>
      <div class="scanline"></div>
    </div>
    
    <div class="content">
      <!-- 健身模板 -->
      <div v-if="templates.length > 0" class="section-header">
        <div class="section-title-wrapper">
          <span class="section-icon">📋</span>
          <span class="section-title-text">健身模板</span>
        </div>
      </div>
      <div v-for="(template, index) in templates" :key="template.id" class="template-card glow-card animate-in" :style="{ animationDelay: `${index * 0.1}s` }" @click="navigateToDetail(template.id)">
        <div class="card-inner">
          <div class="template-image">
            <img :src="template.image || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=fitness%20training%20workout%20template%20background&image_size=landscape_16_9'" alt="{{ template.name }}">
            <div class="image-overlay"></div>
            <div class="difficulty-badge" :class="template.difficulty">
              <span class="badge-icon">{{ getDifficultyIcon(template.difficulty) }}</span>
              <span class="badge-text">{{ getDifficultyText(template.difficulty) }}</span>
            </div>
          </div>
          <div class="template-info">
            <h3 class="template-name">{{ template.name }}</h3>
            <p class="template-description">{{ template.description }}</p>
            <div v-if="template.createdBy" class="template-creator">
              <span class="creator-icon">👤</span>
              <span class="creator-text">创建者: 用户{{ template.createdBy }}</span>
            </div>
            <div v-if="template.createdAt" class="template-created-time">
              <span class="time-icon">🕐</span>
              <span class="time-text">{{ formatDate(template.createdAt) }}</span>
            </div>
            <div class="template-footer">
              <div class="template-stats">
                <div class="stat-item">
                  <span class="stat-icon">⚡</span>
                  <span class="stat-text">{{ template.exercises || 8 }} 个动作</span>
                </div>
                <div class="stat-item">
                  <span class="stat-icon">⏱</span>
                  <span class="stat-text">{{ template.duration || '45' }} 分钟</span>
                </div>
              </div>
              <div class="arrow-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="9 18 15 12 9 6"></polyline>
                </svg>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 用户分享的模板（仅登录状态显示） -->
      <div v-if="isLoggedIn && sharedPlans.length > 0" class="section-header">
        <div class="section-title-wrapper">
          <span class="section-icon">👥</span>
          <span class="section-title-text">用户分享的计划</span>
        </div>
      </div>
      <div v-if="isLoggedIn" v-for="(plan, index) in sharedPlans" :key="plan.id" class="template-card glow-card animate-in" :style="{ animationDelay: `${(templates.length + index) * 0.1}s` }" @click="useSharedPlan(plan)">
        <div class="card-inner">
          <div class="template-image">
            <img :src="plan.image || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=shared%20fitness%20workout%20plan%20background&image_size=landscape_16_9'" alt="{{ plan.name }}">
            <div class="image-overlay"></div>
            <div class="difficulty-badge" :class="plan.difficulty">
              <span class="badge-icon">{{ getDifficultyIcon(plan.difficulty) }}</span>
              <span class="badge-text">{{ getDifficultyText(plan.difficulty) }}</span>
            </div>
          </div>
          <div class="template-info">
            <h3 class="template-name">{{ plan.name }}</h3>
            <p class="template-description">{{ plan.description }}</p>
            <div class="template-creator">
              <span class="creator-icon">👤</span>
              <span class="creator-text">分享者: 用户{{ plan.userId }}</span>
            </div>
            <div class="template-footer">
              <div class="template-stats">
                <div class="stat-item">
                  <span class="stat-icon">🎯</span>
                  <span class="stat-text">{{ plan.goal }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-icon">📅</span>
                  <span class="stat-text">{{ plan.durationWeeks || 4 }} 周</span>
                </div>
              </div>
              <div class="arrow-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="9 18 15 12 9 6"></polyline>
                </svg>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p class="loading-text">加载中...</p>
      </div>
      
      <div v-if="error" class="error-state">
        <div class="error-icon">⚠️</div>
        <p class="error-text">{{ error }}</p>
        <button class="retry-button glow-button" @click="fetchData">
          <span>重试</span>
          <span class="btn-glow"></span>
        </button>
      </div>
    </div>
    
    <!-- 底部操作按钮 -->
    <div class="bottom-actions">
      <button class="action-btn custom-plan" @click="navigateToCustomPlan">
        <div class="btn-inner">
          <div class="btn-icon-wrapper">
            <span class="btn-icon">✏️</span>
          </div>
          <span class="btn-text">自定义计划</span>
        </div>
      </button>
      <button class="action-btn ai-plan" @click="navigateToAIPlan">
        <div class="btn-inner">
          <div class="btn-icon-wrapper">
            <span class="btn-icon">🤖</span>
          </div>
          <span class="btn-text">AI智能定制</span>
        </div>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import api from '../../utils/api';

const router = useRouter();
const templates = ref<any[]>([]);
const sharedPlans = ref<any[]>([]);
const loading = ref(true);
const error = ref('');
const isLoggedIn = ref(localStorage.getItem('token') !== null);

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
  fetchData();
});

const fetchData = async () => {
  try {
    loading.value = true;
    error.value = '';
    
    // 加载健身模板
    const templateResponse = await api.templates.getList();
    if (templateResponse.code === 200 && templateResponse.data) {
      templates.value = templateResponse.data;
    } else {
      error.value = '加载模板失败，请重试';
    }
    
    // 如果已登录，加载用户分享的计划
    if (isLoggedIn.value) {
      try {
        const plansResponse = await api.plans.getList();
        if (plansResponse.code === 200 && plansResponse.data) {
          // 过滤出分享的计划
          sharedPlans.value = plansResponse.data.filter((plan: any) => plan.isShared === 1);
        }
      } catch (err) {
        console.error('加载分享计划失败:', err);
      }
    }
  } catch (err) {
    error.value = '加载失败，请重试';
    console.error(err);
  } finally {
    loading.value = false;
  }
};

const navigateToDetail = (templateId: number) => {
  router.push(`/pages/template/detail?id=${templateId}`);
};

const useSharedPlan = (plan: any) => {
  // 使用分享的计划
  router.push(`/pages/home/index`);
};

const getDifficultyText = (difficulty: string) => {
  const map = {
    beginner: '新手',
    intermediate: '中级',
    advanced: '高级'
  };
  return map[difficulty as keyof typeof map] || difficulty;
};

const getDifficultyIcon = (difficulty: string) => {
  const map = {
    beginner: '🌱',
    intermediate: '🔥',
    advanced: '⚔️'
  };
  return map[difficulty as keyof typeof map] || '📋';
};

const navigateToCustomPlan = () => {
  router.push('/pages/custom/create');
};

const navigateToAIPlan = () => {
  router.push('/pages/ai/index');
};

const formatDate = (dateString: string) => {
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch {
    return dateString;
  }
};
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

@keyframes neon-pulse {
  0%, 100% {
    text-shadow: 0 0 10px var(--neon-cyan), 0 0 20px var(--neon-cyan);
  }
  50% {
    text-shadow: 0 0 20px var(--neon-cyan), 0 0 40px var(--neon-cyan), 0 0 60px var(--neon-purple);
  }
}

.animate-in {
  animation: fadeInUp 0.6s ease forwards;
}

.template-list-container {
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

.content {
  padding: 15.0px 10.0px;
  padding-bottom: 140.0px;
  position: relative;
  z-index: 1;
}

.section-header {
  margin-bottom: 12.5px;
  margin-top: 10.0px;
}

.section-title-wrapper {
  display: flex;
  align-items: center;
  gap: 8.0px;
  padding: 10.0px 12.5px;
  background: var(--bg-card);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border-radius: 10.0px;
  border: 1px solid var(--border-color);
}

.section-icon {
  font-size: 18.0px;
}

.section-title-text {
  font-size: 15.0px;
  font-weight: 700;
  color: var(--text-primary);
}

.template-card {
  background: var(--bg-card);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border-radius: 15.0px;
  margin-bottom: 15.0px;
  box-shadow: 0 4.0px 20.0px rgba(0, 0, 0, 0.4);
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  overflow: hidden;
  border: 1px solid var(--border-color);
  cursor: pointer;
}

.template-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2.0px;
  background: var(--gradient-neon);
  border-top-left-radius: 15.0px;
  border-top-right-radius: 15.0px;
  z-index: 2;
}

.template-card::after {
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

.card-inner {
  position: relative;
}

.template-image {
  width: 100%;
  height: 180.0px;
  overflow: hidden;
  position: relative;
}

.template-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.template-card:hover .template-image img {
  transform: scale(1.08);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0,0,0,0.1) 0%, rgba(0,0,0,0.6) 100%);
  transition: background 0.3s ease;
}

.template-card:hover .image-overlay {
  background: linear-gradient(to bottom, rgba(0,0,0,0.2) 0%, rgba(0,0,0,0.7) 100%);
}

.difficulty-badge {
  position: absolute;
  top: 10.0px;
  right: 10.0px;
  display: flex;
  align-items: center;
  gap: 4.0px;
  padding: 5.0px 10.0px;
  border-radius: 10.0px;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  z-index: 2;
  transition: all 0.3s ease;
}

.difficulty-badge.beginner {
  background: rgba(0, 255, 136, 0.2);
  border: 1px solid var(--neon-green);
  box-shadow: 0 0 15px rgba(0, 255, 136, 0.3);
}

.difficulty-badge.intermediate {
  background: rgba(255, 209, 102, 0.2);
  border: 1px solid var(--neon-orange);
  box-shadow: 0 0 15px rgba(255, 107, 53, 0.3);
}

.difficulty-badge.advanced {
  background: rgba(255, 0, 110, 0.2);
  border: 1px solid var(--neon-pink);
  box-shadow: 0 0 15px rgba(255, 0, 110, 0.3);
}

.badge-icon {
  font-size: 12.0px;
}

.badge-text {
  font-size: 10.0px;
  font-weight: 600;
}

.difficulty-badge.beginner .badge-text {
  color: var(--neon-green);
}

.difficulty-badge.intermediate .badge-text {
  color: var(--neon-orange);
}

.difficulty-badge.advanced .badge-text {
  color: var(--neon-pink);
}

.template-info {
  padding: 15.0px;
}

.template-name {
  font-size: 16.0px;
  font-weight: 700;
  margin-bottom: 6.0px;
  color: var(--text-primary);
  transition: color 0.3s ease;
}

.template-card:hover .template-name {
  color: var(--neon-cyan);
}

.template-description {
  font-size: 12.0px;
  color: var(--text-secondary);
  margin-bottom: 12.5px;
  line-height: 1.6;
}

.template-creator,
.template-created-time {
  display: flex;
  align-items: center;
  gap: 4.0px;
  font-size: 11.0px;
  color: var(--text-muted);
  margin-bottom: 6.0px;
}

.creator-icon,
.time-icon {
  font-size: 12.0px;
}

.template-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.template-stats {
  display: flex;
  gap: 12.5px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4.0px;
}

.stat-icon {
  font-size: 12.0px;
}

.stat-text {
  font-size: 11.0px;
  color: var(--text-muted);
  font-weight: 500;
}

.arrow-icon {
  width: 24.0px;
  height: 24.0px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(0, 245, 255, 0.1);
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.arrow-icon svg {
  width: 12.0px;
  height: 12.0px;
  color: var(--neon-cyan);
}

.template-card:hover .arrow-icon {
  background: var(--gradient-cyan);
  border-color: var(--neon-cyan);
  transform: translateX(2.5px);
  box-shadow: 0 0 15px rgba(0, 245, 255, 0.4);
}

.template-card:hover .arrow-icon svg {
  color: white;
}

.loading-state,
.error-state {
  text-align: center;
  padding: 50.0px 20.0px;
  background: rgba(0, 245, 255, 0.03);
  border-radius: 15.0px;
  margin-top: 15.0px;
  border: 1px dashed var(--border-color);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10.0px;
}

.loading-spinner {
  width: 30.0px;
  height: 30.0px;
  border: 2.0px solid rgba(0, 245, 255, 0.1);
  border-top: 2.0px solid var(--neon-cyan);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  box-shadow: var(--glow-cyan);
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.loading-text,
.error-text {
  font-size: 13.0px;
  color: var(--text-muted);
  font-weight: 500;
}

.error-icon {
  font-size: 30.0px;
}

.retry-button {
  position: relative;
  overflow: hidden;
  background: var(--gradient-cyan);
  color: white;
  border: none;
  border-radius: 8.0px;
  padding: 10.0px 24.0px;
  font-size: 12.0px;
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

.bottom-actions {
  position: fixed;
  bottom: 70.0px;
  left: 0;
  right: 0;
  padding: 0 10.0px;
  display: flex;
  gap: 10.0px;
  z-index: 90;
}

.action-btn {
  flex: 1;
  padding: 0;
  border: none;
  border-radius: 12.0px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  overflow: hidden;
  position: relative;
}

.action-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 12.0px;
  padding: 2px;
  background: var(--gradient-neon);
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0;
  transition: opacity 0.4s ease;
  pointer-events: none;
  z-index: 2;
}

.action-btn:hover::before {
  opacity: 1;
}

.action-btn.custom-plan {
  background: var(--bg-card);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border: 1px solid rgba(78, 205, 196, 0.3);
  box-shadow: 0 4.0px 15.0px rgba(0, 0, 0, 0.4);
}

.action-btn.ai-plan {
  background: var(--bg-card);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border: 1px solid rgba(255, 107, 53, 0.3);
  box-shadow: 0 4.0px 15.0px rgba(0, 0, 0, 0.4);
}

.action-btn:hover {
  transform: translateY(-3.0px) scale(1.02);
}

.action-btn.custom-plan:hover {
  box-shadow: 0 8.0px 25.0px rgba(78, 205, 196, 0.3);
  border-color: var(--neon-cyan);
}

.action-btn.ai-plan:hover {
  box-shadow: 0 8.0px 25.0px rgba(255, 107, 53, 0.3);
  border-color: var(--neon-orange);
}

.btn-inner {
  padding: 14.0px 10.0px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 1;
}

.btn-icon-wrapper {
  width: 30.0px;
  height: 30.0px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 6.0px;
  transition: all 0.4s ease;
}

.action-btn.custom-plan .btn-icon-wrapper {
  background: linear-gradient(135deg, rgba(78, 205, 196, 0.2), rgba(0, 245, 255, 0.2));
  border: 1px solid rgba(0, 245, 255, 0.3);
}

.action-btn.ai-plan .btn-icon-wrapper {
  background: linear-gradient(135deg, rgba(255, 107, 53, 0.2), rgba(255, 0, 110, 0.2));
  border: 1px solid rgba(255, 107, 53, 0.3);
}

.action-btn.custom-plan:hover .btn-icon-wrapper {
  background: var(--gradient-cyan);
  border-color: var(--neon-cyan);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.5);
}

.action-btn.ai-plan:hover .btn-icon-wrapper {
  background: linear-gradient(135deg, var(--neon-orange), var(--neon-pink));
  border-color: var(--neon-orange);
  box-shadow: 0 0 20px rgba(255, 107, 53, 0.5);
}

.btn-icon {
  font-size: 16.0px;
  transition: transform 0.4s ease;
}

.action-btn:hover .btn-icon {
  transform: scale(1.15) rotate(5deg);
}

.btn-text {
  font-size: 11.0px;
  font-weight: 600;
  color: var(--text-primary);
  transition: color 0.3s ease;
}

.action-btn.custom-plan:hover .btn-text {
  color: var(--neon-cyan);
}

.action-btn.ai-plan:hover .btn-text {
  color: var(--neon-orange);
}

@media (max-width: 768px) {
  .content {
    padding: 10.0px 7.5px;
    padding-bottom: 130.0px;
  }
  
  .template-card {
    margin-bottom: 12.5px;
  }
  
  .template-image {
    height: 160.0px;
  }
  
  .template-info {
    padding: 12.5px;
  }
  
  .title {
    font-size: 17.0px;
  }
  
  .template-name {
    font-size: 14.0px;
  }
  
  .bottom-actions {
    padding: 0 7.5px;
  }
  
  .btn-inner {
    padding: 12.0px 7.5px;
  }
  
  .btn-icon-wrapper {
    width: 26.0px;
    height: 26.0px;
  }
  
  .btn-icon {
    font-size: 14.0px;
  }
  
  .btn-text {
    font-size: 10.0px;
  }
}
</style>
