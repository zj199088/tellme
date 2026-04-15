<template>
  <div class="app-container">
    <router-view v-slot="{ Component }">
      <transition name="slide" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
    <nav class="bottom-nav">
      <router-link to="/pages/home/index" class="nav-item" active-class="active">
        <div class="nav-icon">🏠</div>
        <div class="nav-text">首页</div>
      </router-link>
      <router-link to="/pages/template/list" class="nav-item" active-class="active">
        <div class="nav-icon">📋</div>
        <div class="nav-text">计划</div>
      </router-link>
      <router-link to="/pages/workout/track" class="nav-item" active-class="active">
        <div class="nav-icon">🏃</div>
        <div class="nav-text">跟踪训练</div>
      </router-link>
      <router-link to="/pages/mine/index" class="nav-item" active-class="active">
        <div class="nav-icon">👤</div>
        <div class="nav-text">我的</div>
      </router-link>
    </nav>
  </div>
</template>

<script setup lang="ts">
// App组件
</script>

<style>
/* 科技未来风格 CSS 变量 */
:root {
  /* 深色背景色 */
  --bg-primary: #0a0a0f;
  --bg-secondary: #0f111a;
  --bg-tertiary: #15192d;
  --bg-card: rgba(20, 25, 45, 0.8);
  
  /* 霓虹主色调 - 蓝绿色系 */
  --neon-cyan: #00f5ff;
  --neon-blue: #0084ff;
  --neon-purple: #8b5cf6;
  --neon-pink: #ff006e;
  --neon-orange: #ff6b35;
  --neon-green: #00ff88;
  
  /* 渐变背景 */
  --gradient-bg: linear-gradient(135deg, #0a0a0f 0%, #1a1b2e 50%, #0f111a 100%);
  --gradient-neon: linear-gradient(90deg, #00f5ff, #8b5cf6, #ff006e);
  --gradient-cyan: linear-gradient(135deg, #00f5ff 0%, #0084ff 100%);
  --gradient-purple: linear-gradient(135deg, #8b5cf6 0%, #ff006e 100%);
  
  /* 文字颜色 */
  --text-primary: #ffffff;
  --text-secondary: #a0aec0;
  --text-muted: #718096;
  
  /* 边框和阴影 */
  --border-color: rgba(0, 245, 255, 0.2);
  --glow-cyan: 0 0 20px rgba(0, 245, 255, 0.5), 0 0 40px rgba(0, 245, 255, 0.3);
  --glow-purple: 0 0 20px rgba(139, 92, 246, 0.5), 0 0 40px rgba(139, 92, 246, 0.3);
  --glow-pink: 0 0 20px rgba(255, 0, 110, 0.5), 0 0 40px rgba(255, 0, 110, 0.3);
}

/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

/* 全局平滑滚动 */
html {
  scroll-behavior: smooth;
}

body {
  font-family: 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background: var(--gradient-bg);
  min-height: 100vh;
  overflow-x: hidden;
  color: var(--text-primary);
  position: relative;
}

/* 科技网格背景 */
body::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    linear-gradient(rgba(0, 245, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 245, 255, 0.03) 1px, transparent 1px);
  background-size: 50px 50px;
  pointer-events: none;
  z-index: 0;
}

/* 全局动画定义 */
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

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideInRight {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes slideInLeft {
  from {
    transform: translateX(-100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-5.0px);
  }
  60% {
    transform: translateY(-2.5px);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-5.0px) rotate(1deg);
  }
}

/* 全局类 */
.animate-fade-in-up {
  animation: fadeInUp 0.6s ease forwards;
}

.animate-fade-in {
  animation: fadeIn 0.5s ease forwards;
}

.animate-slide-in-right {
  animation: slideInRight 0.5s ease forwards;
}

.animate-slide-in-left {
  animation: slideInLeft 0.5s ease forwards;
}

.animate-pulse {
  animation: pulse 2s infinite;
}

.animate-bounce {
  animation: bounce 0.6s ease;
}

.animate-float {
  animation: float 6s ease-in-out infinite;
}

/* 主容器 */
.app-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 1;
}

/* 科技发光效果基础类 */
.glow-text {
  text-shadow: var(--glow-cyan);
}

.glow-border {
  border: 1px solid var(--border-color);
  box-shadow: var(--glow-cyan);
}

.glow-button {
  background: var(--gradient-cyan);
  box-shadow: var(--glow-cyan);
  transition: all 0.3s ease;
}

.glow-button:hover {
  box-shadow: 0 0 30px rgba(0, 245, 255, 0.7), 0 0 60px rgba(0, 245, 255, 0.5);
  transform: scale(1.02);
}

/* 更流畅的页面过渡动画 */
.slide-enter-active,
.slide-leave-active {
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.slide-enter-from {
  transform: translateX(100%);
  opacity: 0;
}

.slide-leave-to {
  transform: translateX(-100%);
  opacity: 0;
}

/* 科技未来风格底部导航栏 */
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: var(--bg-card);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  display: flex;
  justify-content: space-around;
  padding: 7.5px 0;
  box-shadow: 0 -2.0px 15.0px rgba(0, 245, 255, 0.15);
  z-index: 100;
  border-top-left-radius: 15.0px;
  border-top-right-radius: 15.0px;
  transition: all 0.3s ease;
  border-top: 1px solid var(--border-color);
}

/* 导航栏滚动效果 */
.bottom-nav.scrolled {
  box-shadow: 0 -3.0px 20.0px rgba(0, 245, 255, 0.25);
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-decoration: none;
  color: var(--text-secondary);
  font-size: 10.0px;
  padding: 5.0px 15.0px;
  border-radius: 10.0px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.nav-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(0, 245, 255, 0.1), transparent);
  transition: left 0.5s;
}

.nav-item:hover::before {
  left: 100%;
}

.nav-item:hover {
  color: var(--neon-cyan);
}

.nav-item.active {
  color: var(--neon-cyan);
  transform: translateY(-1.0px);
  text-shadow: 0 0 10px rgba(0, 245, 255, 0.8);
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 20.0px;
  height: 2.0px;
  background: var(--gradient-cyan);
  border-radius: 1.0px;
  animation: pulse 2s infinite;
  box-shadow: var(--glow-cyan);
}

.nav-icon {
  font-size: 20.0px;
  margin-bottom: 4.0px;
  transition: transform 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-item.active .nav-icon {
  transform: scale(1.1) rotate(5deg);
  animation: bounce 0.6s ease;
}

.nav-text {
  font-size: 10.0px;
  font-weight: 500;
  transition: font-weight 0.3s ease;
}

.nav-item.active .nav-text {
  font-weight: 600;
}

/* 为内容区域添加底部padding，避免被导航栏遮挡 */
.router-view-container {
  flex: 1;
  padding-bottom: 70.0px;
}

/* 全局按钮样式 */
button {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  font-family: inherit;
}

button:hover {
  transform: translateY(-1.0px);
}

button:active {
  transform: translateY(0);
}

/* 全局输入框样式 */
input {
  font-family: inherit;
  transition: all 0.3s ease;
}

input:focus {
  outline: none;
}

/* 科技未来风格全局卡片样式 */
.card {
  background: var(--bg-card);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 12.0px;
  box-shadow: 0 4.0px 15.0px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  border: 1px solid var(--border-color);
}

.card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2.0px;
  background: var(--gradient-neon);
  border-top-left-radius: 12.0px;
  border-top-right-radius: 12.0px;
}

.card::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 12.0px;
  padding: 1px;
  background: var(--gradient-neon);
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.card:hover {
  transform: translateY(-2.0px);
  box-shadow: 0 6.0px 20.0px rgba(0, 245, 255, 0.2);
}

.card:hover::after {
  opacity: 1;
}

/* 响应式设计 */
/* 移动端 */
@media (max-width: 767px) {
  .bottom-nav {
    padding: 5.0px 0;
  }
  
  .nav-item {
    padding: 4.0px 10.0px;
  }
  
  .nav-icon {
    font-size: 18.0px;
  }
  
  .nav-text {
    font-size: 9.0px;
  }
  
  .router-view-container {
    padding-bottom: 60.0px;
  }
}

/* 平板设备 */
@media (min-width: 768px) and (max-width: 1023px) {
  .bottom-nav {
    padding: 10px 0;
    max-width: 600px;
    left: 50%;
    transform: translateX(-50%);
  }
  
  .nav-item {
    padding: 8px 20px;
  }
  
  .nav-icon {
    font-size: 24px;
  }
  
  .nav-text {
    font-size: 12px;
  }
  
  .router-view-container {
    padding-bottom: 80px;
    max-width: 800px;
    margin: 0 auto;
  }
}

/* 桌面设备 */
@media (min-width: 1024px) {
  .bottom-nav {
    padding: 12px 0;
    max-width: 800px;
    left: 50%;
    transform: translateX(-50%);
  }
  
  .nav-item {
    padding: 10px 30px;
  }
  
  .nav-icon {
    font-size: 28px;
  }
  
  .nav-text {
    font-size: 14px;
  }
  
  .router-view-container {
    padding-bottom: 100px;
    max-width: 1000px;
    margin: 0 auto;
  }
}

/* 优化动画性能 - 使用硬件加速 */
.animate-fade-in-up,
.animate-fade-in,
.animate-slide-in-right,
.animate-slide-in-left,
.glow-button:hover,
.card:hover,
.nav-item,
.nav-icon {
  will-change: transform, opacity;
  backface-visibility: hidden;
  perspective: 1000px;
}

/* 科技未来风格加载动画 */
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

/* 科技微交互效果 */
.tap-effect {
  position: relative;
  overflow: hidden;
}

.tap-effect::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(0, 245, 255, 0.3);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

.tap-effect:active::after {
  width: 150.0px;
  height: 150.0px;
}

/* 霓虹脉冲动画 */
@keyframes neon-pulse {
  0%, 100% {
    text-shadow: 0 0 10px var(--neon-cyan),
                 0 0 20px var(--neon-cyan),
                 0 0 30px var(--neon-cyan);
  }
  50% {
    text-shadow: 0 0 20px var(--neon-cyan),
                 0 0 40px var(--neon-cyan),
                 0 0 60px var(--neon-cyan);
  }
}

.neon-pulse {
  animation: neon-pulse 2s infinite;
}

/* 扫描线效果 */
.scanline::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(to bottom, transparent, var(--neon-cyan), transparent);
  animation: scanline 3s linear infinite;
  opacity: 0.3;
}

@keyframes scanline {
  0% {
    top: 0;
  }
  100% {
    top: 100%;
  }
}

/* 霓虹边框动画 */
@keyframes border-glow {
  0%, 100% {
    box-shadow: var(--glow-cyan);
  }
  50% {
    box-shadow: var(--glow-purple);
  }
}

.border-glow {
  animation: border-glow 3s infinite;
}
</style>