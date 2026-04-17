<template>
  <div class="settings-container">
    <div class="particle-bg" id="particleBg"></div>
    <div class="glow-orbs">
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
      <div class="orb orb-3"></div>
    </div>
    

    
    <div class="content">
      <!-- 主题设置 -->
      <div class="settings-card animate-in glow-card">
        <h2 class="section-title">
          <span class="title-icon">🎨</span>
          <span>主题设置</span>
          <span class="title-glow"></span>
        </h2>
        <div class="theme-section">
          <div class="theme-item" :class="{ active: currentTheme === 'tech-future' }" @click="changeTheme('tech-future')">
            <div class="theme-preview tech-future">
              <div class="preview-dot dot-1"></div>
              <div class="preview-dot dot-2"></div>
              <div class="preview-dot dot-3"></div>
            </div>
            <div class="theme-info">
              <div class="theme-name">科技未来</div>
              <div class="theme-desc">蓝绿色霓虹风格</div>
            </div>
            <div class="theme-check" v-if="currentTheme === 'tech-future'">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                <polyline points="20 6 9 17 4 12"></polyline>
              </svg>
            </div>
          </div>
          
          <div class="theme-item" :class="{ active: currentTheme === 'passionate-fire' }" @click="changeTheme('passionate-fire')">
            <div class="theme-preview passionate-fire">
              <div class="preview-dot dot-1"></div>
              <div class="preview-dot dot-2"></div>
              <div class="preview-dot dot-3"></div>
            </div>
            <div class="theme-info">
              <div class="theme-name">热情似火</div>
              <div class="theme-desc">红橙色热情风格</div>
            </div>
            <div class="theme-check" v-if="currentTheme === 'passionate-fire'">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                <polyline points="20 6 9 17 4 12"></polyline>
              </svg>
            </div>
          </div>
          
          <div class="theme-item" :class="{ active: currentTheme === 'green-breeze' }" @click="changeTheme('green-breeze')">
            <div class="theme-preview green-breeze">
              <div class="preview-dot dot-1"></div>
              <div class="preview-dot dot-2"></div>
              <div class="preview-dot dot-3"></div>
            </div>
            <div class="theme-info">
              <div class="theme-name">绿野清风</div>
              <div class="theme-desc">绿色清新风格</div>
            </div>
            <div class="theme-check" v-if="currentTheme === 'green-breeze'">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                <polyline points="20 6 9 17 4 12"></polyline>
              </svg>
            </div>
          </div>
          
          <div class="theme-item" :class="{ active: currentTheme === 'chinese-ink' }" @click="changeTheme('chinese-ink')">
            <div class="theme-preview chinese-ink">
              <div class="preview-dot dot-1"></div>
              <div class="preview-dot dot-2"></div>
              <div class="preview-dot dot-3"></div>
            </div>
            <div class="theme-info">
              <div class="theme-name">中国风水墨画</div>
              <div class="theme-desc">红衣女侠水墨风格</div>
            </div>
            <div class="theme-check" v-if="currentTheme === 'chinese-ink'">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                <polyline points="20 6 9 17 4 12"></polyline>
              </svg>
            </div>
          </div>
          
          <div class="theme-item" :class="{ active: currentTheme === 'light-blue' }" @click="changeTheme('light-blue')">
            <div class="theme-preview light-blue">
              <div class="preview-dot dot-1"></div>
              <div class="preview-dot dot-2"></div>
              <div class="preview-dot dot-3"></div>
            </div>
            <div class="theme-info">
              <div class="theme-name">淡蓝世界</div>
              <div class="theme-desc">海蓝天白云风格</div>
            </div>
            <div class="theme-check" v-if="currentTheme === 'light-blue'">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                <polyline points="20 6 9 17 4 12"></polyline>
              </svg>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 其他设置 -->
      <div class="settings-card animate-in glow-card" style="animation-delay: 0.1s;">
        <h2 class="section-title">
          <span class="title-icon">⚙️</span>
          <span>其他设置</span>
          <span class="title-glow"></span>
        </h2>
        <div class="settings-list">
          <div class="setting-item">
            <div class="setting-info">
              <div class="setting-name">通知设置</div>
              <div class="setting-desc">管理应用通知</div>
            </div>
            <div class="setting-arrow">→</div>
          </div>
          <div class="setting-item">
            <div class="setting-info">
              <div class="setting-name">账户安全</div>
              <div class="setting-desc">修改密码和安全设置</div>
            </div>
            <div class="setting-arrow">→</div>
          </div>
          <div class="setting-item">
            <div class="setting-info">
              <div class="setting-name">关于我们</div>
              <div class="setting-desc">版本信息和联系方式</div>
            </div>
            <div class="setting-arrow">→</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const currentTheme = ref('tech-future');

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
  
  // 从localStorage加载主题
  const savedTheme = localStorage.getItem('theme');
  if (savedTheme) {
    currentTheme.value = savedTheme;
    applyTheme(savedTheme);
  }
});

const goBack = () => {
  router.push('/pages/mine/index');
};

const changeTheme = (theme: string) => {
  currentTheme.value = theme;
  localStorage.setItem('theme', theme);
  applyTheme(theme);
};

const applyTheme = (theme: string) => {
  // 移除所有主题类
  document.body.classList.remove('theme-tech-future', 'theme-passionate-fire', 'theme-green-breeze', 'theme-chinese-ink', 'theme-light-blue');
  // 添加当前主题类
  document.body.classList.add(`theme-${theme}`);
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

.settings-container {
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
  display: flex;
  align-items: center;
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
  width: 100%;
}

.back-button {
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
  margin-right: 15.0px;
}

.back-button:hover {
  background: rgba(0, 245, 255, 0.2);
  border-color: var(--neon-cyan);
  box-shadow: 0 0 15px rgba(0, 245, 255, 0.3);
}

.back-button svg {
  width: 20.0px;
  height: 20.0px;
  color: var(--neon-cyan);
}

.header-spacer {
  flex: 1;
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

.settings-card {
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

.settings-card::before {
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

.settings-card::after {
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

.theme-section {
  display: flex;
  flex-direction: column;
  gap: 10.0px;
}

.theme-item {
  display: flex;
  align-items: center;
  padding: 12.5px;
  border-radius: 10.0px;
  background: var(--bg-tertiary);
  border: 1px solid rgba(0, 245, 255, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.theme-item:hover {
  background: rgba(0, 245, 255, 0.08);
  border-color: var(--neon-cyan);
  transform: translateX(4px);
  box-shadow: 0 4.0px 25px rgba(0, 245, 255, 0.2);
}

.theme-item.active {
  background: rgba(0, 245, 255, 0.15);
  border-color: var(--neon-cyan);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.3);
}

.theme-preview {
  width: 60.0px;
  height: 60.0px;
  border-radius: 10.0px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6.0px;
  margin-right: 12.5px;
  position: relative;
  overflow: hidden;
}

.theme-preview.tech-future {
  background: linear-gradient(135deg, #0a0a0f 0%, #15192d 100%);
}

.theme-preview.passionate-fire {
  background: linear-gradient(135deg, #1a0a0a 0%, #3d1a1a 100%);
}

.theme-preview.green-breeze {
  background: linear-gradient(135deg, #0a1a0a 0%, #1a3d1a 100%);
}

.theme-preview.chinese-ink {
  background: linear-gradient(135deg, #0a0a0a 0%, #2a2a2a 100%);
}

.theme-preview.light-blue {
  background: linear-gradient(135deg, #87ceeb 0%, #e0f7fa 100%);
}

.preview-dot {
  width: 12.0px;
  height: 12.0px;
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

.theme-preview.tech-future .dot-1 {
  background: var(--neon-cyan);
  animation-delay: 0s;
}

.theme-preview.tech-future .dot-2 {
  background: var(--neon-purple);
  animation-delay: 0.3s;
}

.theme-preview.tech-future .dot-3 {
  background: var(--neon-pink);
  animation-delay: 0.6s;
}

.theme-preview.passionate-fire .dot-1 {
  background: #ff4757;
  animation-delay: 0s;
}

.theme-preview.passionate-fire .dot-2 {
  background: #ff9f43;
  animation-delay: 0.3s;
}

.theme-preview.passionate-fire .dot-3 {
  background: #ff6b81;
  animation-delay: 0.6s;
}

.theme-preview.green-breeze .dot-1 {
  background: #2ed573;
  animation-delay: 0s;
}

.theme-preview.green-breeze .dot-2 {
  background: #7bed9f;
  animation-delay: 0.3s;
}

.theme-preview.green-breeze .dot-3 {
  background: #1e90ff;
  animation-delay: 0.6s;
}

.theme-preview.chinese-ink .dot-1 {
  background: #c62828;
  animation-delay: 0s;
}

.theme-preview.chinese-ink .dot-2 {
  background: #424242;
  animation-delay: 0.3s;
}

.theme-preview.chinese-ink .dot-3 {
  background: #bdbdbd;
  animation-delay: 0.6s;
}

.theme-preview.light-blue .dot-1 {
  background: #2196f3;
  animation-delay: 0s;
}

.theme-preview.light-blue .dot-2 {
  background: #81d4fa;
  animation-delay: 0.3s;
}

.theme-preview.light-blue .dot-3 {
  background: #ffffff;
  animation-delay: 0.6s;
}

.theme-info {
  flex: 1;
}

.theme-name {
  font-size: 14.0px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 2.5px;
}

.theme-desc {
  font-size: 12.0px;
  color: var(--text-secondary);
}

.theme-check {
  width: 24.0px;
  height: 24.0px;
  border-radius: 50%;
  background: var(--neon-cyan);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 0 15px var(--neon-cyan);
}

.theme-check svg {
  width: 14.0px;
  height: 14.0px;
}

.settings-list {
  display: flex;
  flex-direction: column;
  gap: 7.5px;
}

.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12.5px;
  border-radius: 10.0px;
  background: var(--bg-tertiary);
  border: 1px solid rgba(0, 245, 255, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.setting-item:hover {
  background: rgba(0, 245, 255, 0.08);
  border-color: var(--neon-cyan);
  transform: translateX(4px);
  box-shadow: 0 4.0px 25px rgba(0, 245, 255, 0.2);
}

.setting-info {
  flex: 1;
}

.setting-name {
  font-size: 14.0px;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 2.5px;
}

.setting-desc {
  font-size: 12.0px;
  color: var(--text-secondary);
}

.setting-arrow {
  font-size: 16.0px;
  color: var(--text-muted);
  transition: color 0.3s ease;
}

.setting-item:hover .setting-arrow {
  color: var(--neon-cyan);
  transform: translateX(2.5px);
}

@media (max-width: 768px) {
  .content {
    padding: 10.0px 7.5px;
  }
  
  .settings-card {
    padding: 15.0px;
  }
  
  .title {
    font-size: 17.0px;
  }
  
  .section-title {
    font-size: 14.0px;
  }
  
  .theme-item {
    padding: 10.0px;
  }
  
  .theme-preview {
    width: 50.0px;
    height: 50.0px;
  }
  
  .setting-item {
    padding: 10.0px;
  }
}
</style>