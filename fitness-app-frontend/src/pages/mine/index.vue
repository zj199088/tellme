<template>
  <div class="mine-container">
    <div class="particle-bg" id="particleBg"></div>
    <div class="header">
      <h1 class="title neon-glow">个人中心</h1>
      <div class="header-bg"></div>
    </div>
    <div class="content">
      <!-- 用户信息卡片 -->
      <div class="user-card animate-in">
        <div class="user-avatar">
          <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=fitness%20user%20avatar%20profile%20picture&image_size=square" alt="用户头像">
        </div>
        <div class="user-info">
          <h3 class="user-name">健身爱好者</h3>
          <p class="user-stats">已完成 <span class="stat-number">12</span> 次训练</p>
        </div>
        <div class="user-actions">
          <button class="action-btn settings">
            <span class="btn-icon">⚙️</span>
          </button>
        </div>
      </div>

      <!-- 功能菜单 -->
      <div class="menu-section animate-in" style="animation-delay: 0.1s;">
        <h2 class="section-title">我的功能</h2>
        <div class="menu-grid">
          <div class="menu-item" @click="navigateToAI">
            <div class="menu-icon">🤖</div>
            <div class="menu-text">AI智能定制</div>
            <div class="menu-arrow">→</div>
          </div>
          <div class="menu-item" @click="navigateToRecords">
            <div class="menu-icon">📊</div>
            <div class="menu-text">训练记录</div>
            <div class="menu-arrow">→</div>
          </div>
          <div class="menu-item" @click="navigateToMusic">
            <div class="menu-icon">🎵</div>
            <div class="menu-text">音乐播放器</div>
            <div class="menu-arrow">→</div>
          </div>
          <div class="menu-item" @click="navigateToSettings">
            <div class="menu-icon">⚙️</div>
            <div class="menu-text">设置</div>
            <div class="menu-arrow">→</div>
          </div>
        </div>
      </div>

      <!-- 健身统计 -->
      <div class="stats-section animate-in" style="animation-delay: 0.2s;">
        <h2 class="section-title">健身统计</h2>
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon">🔥</div>
            <div class="stat-content">
              <div class="stat-number">2,543</div>
              <div class="stat-label">消耗卡路里</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">⏱️</div>
            <div class="stat-content">
              <div class="stat-number">36.5h</div>
              <div class="stat-label">训练时长</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">🎯</div>
            <div class="stat-content">
              <div class="stat-number">85%</div>
              <div class="stat-label">完成率</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 快捷操作 -->
      <div class="quick-actions animate-in" style="animation-delay: 0.3s;">
        <h2 class="section-title">快捷操作</h2>
        <div class="action-buttons">
          <button class="quick-btn primary" @click="navigateToCreatePlan">
            <span class="btn-icon">➕</span>
            <span class="btn-text">创建计划</span>
          </button>
          <button class="quick-btn secondary" @click="navigateToCustomPlan">
            <span class="btn-icon">✏️</span>
            <span class="btn-text">自定义计划</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, nextTick } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const initParticles = () => {
  const particleBg = document.getElementById('particleBg');
  if (!particleBg) return;
  
  for (let i = 0; i < 15; i++) {
    const particle = document.createElement('div');
    particle.className = 'particle';
    const size = Math.random() * 25 + 8;
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

onMounted(() => {
  nextTick(() => {
    initParticles();
  });
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
  // 导航到设置页面（如果有的话）
  alert('设置功能开发中');
};

const navigateToCreatePlan = () => {
  router.push('/pages/template/list');
};

const navigateToCustomPlan = () => {
  // 导航到自定义计划页面（如果有的话）
  alert('自定义计划功能开发中');
};
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
.mine-container {
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

/* 用户卡片 */
.user-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24rpx;
  padding: 30rpx;
  margin-bottom: 25rpx;
  box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.user-card::before {
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

.user-card:hover {
  transform: translateY(-4rpx);
  box-shadow: 0 12rpx 40rpx rgba(0, 0, 0, 0.12);
}

.user-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 25rpx;
  box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.1);
  border: 3rpx solid #FF6B35;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 30rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
  color: #333;
}

.user-stats {
  font-size: 24rpx;
  color: #666;
}

.stat-number {
  color: #FF6B35;
  font-weight: bold;
  margin: 0 4rpx;
}

.user-actions {
  display: flex;
  gap: 15rpx;
}

.action-btn {
  width: 60rpx;
  height: 60rpx;
  border: none;
  border-radius: 50%;
  background: linear-gradient(135deg, #f0f0f0, #e0e0e0);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.action-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);
}

/* 区块标题 */
.section-title {
  font-size: 28rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
  color: #333;
  display: flex;
  align-items: center;
}

.section-title::before {
  content: '';
  width: 8rpx;
  height: 24rpx;
  background: #FF6B35;
  border-radius: 4rpx;
  margin-right: 12rpx;
}

/* 菜单网格 */
.menu-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24rpx;
  padding: 30rpx;
  margin-bottom: 25rpx;
  box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;
}

.menu-section::before {
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

.menu-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20rpx;
}

.menu-item {
  background: #f9f9f9;
  border-radius: 16rpx;
  padding: 25rpx;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1rpx solid #e0e0e0;
}

.menu-item:hover {
  background: #f0f0f0;
  transform: translateY(-2rpx);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
}

.menu-icon {
  font-size: 40rpx;
  margin-right: 15rpx;
  min-width: 50rpx;
}

.menu-text {
  flex: 1;
  font-size: 24rpx;
  font-weight: 500;
  color: #333;
}

.menu-arrow {
  font-size: 24rpx;
  color: #999;
  transition: all 0.3s ease;
}

.menu-item:hover .menu-arrow {
  color: #FF6B35;
  transform: translateX(5rpx);
}

/* 统计卡片 */
.stats-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24rpx;
  padding: 30rpx;
  margin-bottom: 25rpx;
  box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;
}

.stats-section::before {
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

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 20rpx;
}

.stat-card {
  background: #f9f9f9;
  border-radius: 16rpx;
  padding: 20rpx;
  text-align: center;
  transition: all 0.3s ease;
  border: 1rpx solid #e0e0e0;
}

.stat-card:hover {
  background: #f0f0f0;
  transform: translateY(-2rpx);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
}

.stat-icon {
  font-size: 40rpx;
  margin-bottom: 15rpx;
}

.stat-number {
  font-size: 28rpx;
  font-weight: bold;
  color: #FF6B35;
  margin-bottom: 5rpx;
}

.stat-label {
  font-size: 20rpx;
  color: #666;
}

/* 快捷操作 */
.quick-actions {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24rpx;
  padding: 30rpx;
  margin-bottom: 25rpx;
  box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;
}

.quick-actions::before {
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

.action-buttons {
  display: flex;
  gap: 20rpx;
}

.quick-btn {
  flex: 1;
  padding: 25rpx;
  border: none;
  border-radius: 16rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.quick-btn.primary {
  background: linear-gradient(135deg, #FF6B35, #FF8E53);
  color: white;
}

.quick-btn.secondary {
  background: linear-gradient(135deg, #4ECDC4, #45B7AA);
  color: white;
}

.quick-btn:hover {
  transform: translateY(-4rpx);
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.15);
}

.btn-icon {
  font-size: 40rpx;
  margin-bottom: 10rpx;
}

.btn-text {
  font-size: 24rpx;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content {
    padding: 20rpx 15rpx;
  }
  
  .user-card,
  .menu-section,
  .stats-section,
  .quick-actions {
    padding: 25rpx;
  }
  
  .menu-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .title {
    font-size: 32rpx;
  }
  
  .user-name {
    font-size: 28rpx;
  }
  
  .section-title {
    font-size: 26rpx;
  }
}
</style>
