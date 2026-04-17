<template>
  <div class="mine-container">
    <div class="particle-bg" id="particleBg"></div>
    <div class="glow-orbs">
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
      <div class="orb orb-3"></div>
    </div>
    

    
    <div class="content">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state animate-in">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
      <div v-else-if="networkError" class="error-state animate-in">
        <div class="error-icon">⚠️</div>
        <p class="error-text">网络连接失败，无法获取用户信息和健身统计</p>
        <button class="retry-button glow-button" @click="loadUserData">
          <span>重试</span>
          <span class="btn-glow"></span>
        </button>
      </div>
      <div v-else>
        <!-- 用户信息卡片 -->
        <div class="user-card animate-in glow-card">
          <div class="user-card-inner">
            <div class="user-avatar">
              <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=fitness%20user%20avatar%20profile%20picture&image_size=square" alt="用户头像">
              <div class="avatar-glow"></div>
            </div>
            <div class="user-info">
              <h3 class="user-name">{{ userInfo.name }}</h3>
              <p class="user-stats">已完成 <span class="stat-number">{{ userInfo.completedWorkouts }}</span> 次训练</p>
              <div class="user-level">
                <span class="level-badge">Lv.{{ userInfo.level }}</span>
                <div class="level-progress">
                  <div class="level-fill" :style="{ width: userInfo.levelProgress + '%' }"></div>
                </div>
              </div>
            </div>
            <div class="user-actions">
              <button class="action-btn settings" @click="navigateToSettings">
                <span class="btn-icon">⚙️</span>
              </button>
            </div>
          </div>
        </div>

        <!-- 功能卡片行 -->
        <div class="features-row">
          <!-- 功能菜单 -->
          <div class="menu-section animate-in glow-card" style="animation-delay: 0.1s;">
            <h2 class="section-title">
              <span class="title-icon">✨</span>
              <span>我的功能</span>
              <span class="title-glow"></span>
            </h2>
            <div class="menu-grid">
              <div class="menu-item" @click="navigateToAI">
                <div class="menu-icon-wrapper">
                  <div class="menu-icon">🤖</div>
                </div>
                <div class="menu-text">AI智能定制</div>
                <div class="menu-arrow">→</div>
              </div>
              <div class="menu-item" @click="navigateToRecords">
                <div class="menu-icon-wrapper">
                  <div class="menu-icon">📊</div>
                </div>
                <div class="menu-text">训练记录</div>
                <div class="menu-arrow">→</div>
              </div>
              <div class="menu-item" @click="navigateToMusic">
                <div class="menu-icon-wrapper">
                  <div class="menu-icon">🎵</div>
                </div>
                <div class="menu-text">音乐播放器</div>
                <div class="menu-arrow">→</div>
              </div>
              <div class="menu-item" @click="navigateToSettings">
                <div class="menu-icon-wrapper">
                  <div class="menu-icon">⚙️</div>
                </div>
                <div class="menu-text">设置</div>
                <div class="menu-arrow">→</div>
              </div>
            </div>
          </div>

          <!-- 健身统计 -->
          <div class="stats-section animate-in glow-card" style="animation-delay: 0.2s;">
            <h2 class="section-title">
              <span class="title-icon">🔥</span>
              <span>健身统计</span>
              <span class="title-glow"></span>
            </h2>
            <div class="stats-grid">
              <div class="stat-card">
                <div class="stat-icon-wrapper">
                  <div class="stat-icon">🔥</div>
                </div>
                <div class="stat-content">
                  <div class="stat-number">{{ userStats.caloriesBurned.toLocaleString() }}</div>
                  <div class="stat-label">消耗卡路里</div>
                </div>
                <div class="stat-glow"></div>
              </div>
              <div class="stat-card">
                <div class="stat-icon-wrapper">
                  <div class="stat-icon">⏱️</div>
                </div>
                <div class="stat-content">
                  <div class="stat-number">{{ userStats.totalDuration.toFixed(1) }}h</div>
                  <div class="stat-label">训练时长</div>
                </div>
                <div class="stat-glow"></div>
              </div>
              <div class="stat-card">
                <div class="stat-icon-wrapper">
                  <div class="stat-icon">🎵</div>
                </div>
                <div class="stat-content">
                  <div class="stat-number">{{ userStats.completionRate }}%</div>
                  <div class="stat-label">完成率</div>
                </div>
                <div class="stat-glow"></div>
              </div>
            </div>
          </div>

          <!-- 快捷操作 -->
          <div class="quick-actions animate-in glow-card" style="animation-delay: 0.3s;">
            <h2 class="section-title">
              <span class="title-icon">🚀</span>
              <span>快捷操作</span>
              <span class="title-glow"></span>
            </h2>
            <div class="action-buttons">
              <button class="quick-btn primary glow-button" @click="navigateToCreatePlan">
                <span class="btn-icon">➕</span>
                <span class="btn-text">创建计划</span>
                <span class="btn-glow"></span>
              </button>
              <button class="quick-btn secondary glow-button" @click="navigateToCustomPlan">
                <span class="btn-icon">✏️</span>
                <span class="btn-text">自定义计划</span>
                <span class="btn-glow"></span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import api from '../../utils/api';

const router = useRouter();

// 状态管理
const loading = ref(false);
const networkError = ref(false);

// 用户信息
const userInfo = ref({
  name: '健身爱好者',
  completedWorkouts: 12,
  level: 3,
  levelProgress: 65
});

// 健身统计
const userStats = ref({
  caloriesBurned: 2543,
  totalDuration: 36.5,
  completionRate: 85
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

const loadUserData = async () => {
  loading.value = true;
  networkError.value = false;
  try {
    // 调用API获取用户信息和健身统计数据
    const [userInfoResponse, userStatsResponse] = await Promise.all([
      api.user.getInfo(),
      api.user.getStats()
    ]);

    if (userInfoResponse.code === 200 && userInfoResponse.data) {
      userInfo.value = {
        name: userInfoResponse.data.nickname,
        completedWorkouts: userStatsResponse.data.completedWorkouts,
        level: userStatsResponse.data.level,
        levelProgress: userStatsResponse.data.levelProgress
      };
    }

    if (userStatsResponse.code === 200 && userStatsResponse.data) {
      userStats.value = {
        caloriesBurned: userStatsResponse.data.caloriesBurned,
        totalDuration: userStatsResponse.data.totalDuration,
        completionRate: userStatsResponse.data.completionRate
      };
    }
  } catch (error) {
    console.error('加载用户数据失败:', error);
    networkError.value = true;
    // 不使用模拟数据作为 fallback
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  nextTick(() => {
    initParticles();
  });
  loadUserData();
});

const navigateToAI = () => {
  router.push('/pages/ai/index');
};

const navigateToRecords = () => {
  router.push('/pages/records/index');
};

const navigateToMusic = () => {
  router.push('/pages/music/player');
};

const navigateToSettings = () => {
  router.push('/pages/mine/settings');
};

const navigateToCreatePlan = () => {
  router.push('/pages/template/list');
};

const navigateToCustomPlan = () => {
  router.push('/pages/custom/create');
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

.error-state {
  background: rgba(255, 204, 0, 0.1);
  border: 1px solid var(--neon-yellow, #FFD700);
  border-radius: 15px;
  padding: 30px 20px;
  margin: 20px 0;
  box-shadow: 0 0 20px rgba(255, 204, 0, 0.2);
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.error-state .error-icon {
  font-size: 48px;
  animation: pulse 2s ease-in-out infinite;
}

.error-state .error-text {
  font-size: 16px;
  color: var(--text-primary);
  margin: 0;
  line-height: 1.4;
}

.error-state .retry-button {
  margin-top: 10px;
}

.mine-container {
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
  animation: neon-pulse 3s ease-in-out infinite;
}

.content {
  padding: 15.0px 10.0px;
  padding-bottom: 80.0px;
  position: relative;
  z-index: 1;
}

.features-row {
  display: flex;
  flex-direction: row;
  gap: 10.0px;
  margin-bottom: 15.0px;
}

.features-row .menu-section,
.features-row .stats-section,
.features-row .quick-actions {
  flex: 1;
  margin-bottom: 0;
  min-width: 0;
}

.user-card,
.menu-section,
.stats-section,
.quick-actions {
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

.user-card::before,
.menu-section::before,
.stats-section::before,
.quick-actions::before {
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

.user-card::after,
.menu-section::after,
.stats-section::after,
.quick-actions::after {
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

.user-card-inner {
  display: flex;
  align-items: center;
  position: relative;
  z-index: 1;
}

.user-avatar {
  width: 70.0px;
  height: 70.0px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 12.5px;
  box-shadow: 0 2.0px 7.5px rgba(0, 0, 0, 0.3);
  border: 1.5px solid var(--neon-cyan);
  position: relative;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-glow {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(0, 245, 255, 0.3) 0%, transparent 70%);
  animation: pulse 3s ease-in-out infinite;
  pointer-events: none;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 16.0px;
  font-weight: bold;
  margin-bottom: 5.0px;
  background: var(--gradient-neon);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.user-stats {
  font-size: 12.0px;
  color: var(--text-secondary);
  margin-bottom: 7.5px;
}

.stat-number {
  color: var(--neon-cyan);
  font-weight: bold;
  margin: 0 2.0px;
  text-shadow: 0 0 10px rgba(0, 245, 255, 0.5);
}

.user-level {
  display: flex;
  align-items: center;
  gap: 7.5px;
}

.level-badge {
  display: inline-block;
  padding: 3.0px 8.0px;
  border-radius: 10.0px;
  font-size: 10.0px;
  font-weight: 600;
  background: rgba(0, 245, 255, 0.15);
  color: var(--neon-cyan);
  border: 1px solid var(--neon-cyan);
  box-shadow: 0 0 10px rgba(0, 245, 255, 0.2);
}

.level-progress {
  flex: 1;
  height: 4.0px;
  background: var(--bg-tertiary);
  border-radius: 2.0px;
  overflow: hidden;
  border: 1px solid rgba(0, 245, 255, 0.1);
}

.level-fill {
  height: 100%;
  background: var(--gradient-neon);
  border-radius: 2.0px;
  transition: width 0.8s ease;
}

.user-actions {
  display: flex;
  gap: 7.5px;
}

.action-btn {
  width: 30.0px;
  height: 30.0px;
  border: none;
  border-radius: 50%;
  background: var(--bg-tertiary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15.0px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 1.0px 4.0px rgba(0, 0, 0, 0.3);
  border: 1px solid var(--border-color);
}

.action-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.4);
  border-color: var(--neon-cyan);
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

.menu-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8.0px;
}

.menu-item {
  background: var(--bg-tertiary);
  border-radius: 10.0px;
  padding: 15.0px 10.0px;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid rgba(0, 245, 255, 0.1);
  position: relative;
  overflow: hidden;
}

.menu-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(0, 245, 255, 0.1), transparent);
  transition: left 0.5s;
}

.menu-item:hover::before {
  left: 100%;
}

.menu-item:hover {
  background: rgba(0, 245, 255, 0.08);
  transform: translateY(-2.0px);
  box-shadow: 0 4.0px 25px rgba(0, 245, 255, 0.2);
  border-color: var(--neon-cyan);
}

.menu-icon-wrapper {
  width: 40.0px;
  height: 40.0px;
  border-radius: 50%;
  background: var(--bg-card);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 7.5px;
  border: 2px solid var(--border-color);
  transition: all 0.3s ease;
}

.menu-item:hover .menu-icon-wrapper {
  border-color: var(--neon-cyan);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.3);
  transform: scale(1.1) rotate(5deg);
}

.menu-icon {
  font-size: 20.0px;
}

.menu-text {
  font-size: 12.0px;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 4.0px;
  transition: color 0.3s ease;
}

.menu-item:hover .menu-text {
  color: var(--neon-cyan);
}

.menu-arrow {
  font-size: 12.0px;
  color: var(--text-muted);
  transition: all 0.3s ease;
}

.menu-item:hover .menu-arrow {
  color: var(--neon-cyan);
  transform: translateX(2.5px);
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 10.0px;
}

.stat-card {
  background: var(--bg-tertiary);
  border-radius: 10.0px;
  padding: 12.5px 10.0px;
  text-align: center;
  transition: all 0.3s ease;
  border: 1px solid rgba(0, 245, 255, 0.1);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1.5px;
  background: var(--gradient-neon);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.stat-card:hover::before {
  opacity: 1;
}

.stat-card:hover {
  background: rgba(0, 245, 255, 0.08);
  transform: translateY(-2.0px);
  box-shadow: 0 4.0px 25px rgba(0, 245, 255, 0.2);
  border-color: var(--neon-cyan);
}

.stat-icon-wrapper {
  width: 35.0px;
  height: 35.0px;
  border-radius: 50%;
  background: var(--bg-card);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 7.5px;
  border: 2px solid var(--border-color);
  transition: all 0.3s ease;
}

.stat-card:hover .stat-icon-wrapper {
  border-color: var(--neon-cyan);
  box-shadow: 0 0 15px rgba(0, 245, 255, 0.3);
  transform: scale(1.1);
}

.stat-icon {
  font-size: 18.0px;
}

.stat-number {
  font-size: 16.0px;
  font-weight: bold;
  background: var(--gradient-neon);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 4.0px;
  transition: all 0.3s ease;
}

.stat-card:hover .stat-number {
  transform: scale(1.1);
}

.stat-label {
  font-size: 10.0px;
  color: var(--text-secondary);
}

.stat-glow {
  position: absolute;
  bottom: -50%;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, rgba(0, 245, 255, 0.1) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.stat-card:hover .stat-glow {
  opacity: 1;
}

.action-buttons {
  display: flex;
  gap: 10.0px;
}

.quick-btn {
  flex: 1;
  padding: 15.0px 10.0px;
  border: none;
  border-radius: 10.0px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  overflow: hidden;
}

.quick-btn.primary {
  background: var(--gradient-cyan);
  color: white;
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.3);
}

.quick-btn.secondary {
  background: var(--gradient-purple);
  color: white;
  box-shadow: 0 0 20px rgba(139, 92, 246, 0.3);
}

.glow-button:hover {
  transform: translateY(-3.0px) scale(1.02);
}

.glow-button:active {
  transform: translateY(0) scale(0.98);
}

.quick-btn.primary:hover {
  box-shadow: 0 0 30px rgba(0, 245, 255, 0.5), 0 0 60px rgba(0, 245, 255, 0.3);
}

.quick-btn.secondary:hover {
  box-shadow: 0 0 30px rgba(139, 92, 246, 0.5), 0 0 60px rgba(139, 92, 246, 0.3);
}

.btn-icon {
  font-size: 22.0px;
  margin-bottom: 5.0px;
}

.btn-text {
  font-size: 12.0px;
  font-weight: 600;
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

@media (max-width: 768px) {
  .content {
    padding: 10.0px 7.5px;
  }
  
  .features-row {
    flex-direction: column;
  }
  
  .user-card,
  .menu-section,
  .stats-section,
  .quick-actions {
    padding: 15.0px;
  }
  
  .user-card-inner {
    flex-wrap: wrap;
  }
  
  .user-avatar {
    width: 60.0px;
    height: 60.0px;
  }
  
  .menu-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .action-buttons {
    flex-direction: row;
  }
  
  .title {
    font-size: 17.0px;
  }
  
  .user-name {
    font-size: 14.0px;
  }
  
  .section-title {
    font-size: 14.0px;
  }
}
</style>
