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
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
  overflow-x: hidden;
}

/* 全局动画定义 */
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
    transform: translateY(-10rpx);
  }
  60% {
    transform: translateY(-5rpx);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-10rpx) rotate(1deg);
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

/* 现代化底部导航栏 */
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  display: flex;
  justify-content: space-around;
  padding: 15rpx 0;
  box-shadow: 0 -2rpx 20rpx rgba(0, 0, 0, 0.08);
  z-index: 100;
  border-top-left-radius: 30rpx;
  border-top-right-radius: 30rpx;
  transition: all 0.3s ease;
}

/* 导航栏滚动效果 */
.bottom-nav.scrolled {
  box-shadow: 0 -4rpx 25rpx rgba(0, 0, 0, 0.12);
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-decoration: none;
  color: #999;
  font-size: 20rpx;
  padding: 10rpx 30rpx;
  border-radius: 20rpx;
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
  background: linear-gradient(90deg, transparent, rgba(255, 107, 53, 0.1), transparent);
  transition: left 0.5s;
}

.nav-item:hover::before {
  left: 100%;
}

.nav-item.active {
  color: #FF6B35;
  transform: translateY(-2rpx);
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 40rpx;
  height: 4rpx;
  background: #FF6B35;
  border-radius: 2rpx;
  animation: pulse 2s infinite;
}

.nav-icon {
  font-size: 40rpx;
  margin-bottom: 8rpx;
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
  font-size: 20rpx;
  font-weight: 500;
  transition: font-weight 0.3s ease;
}

.nav-item.active .nav-text {
  font-weight: 600;
}

/* 为内容区域添加底部padding，避免被导航栏遮挡 */
.router-view-container {
  flex: 1;
  padding-bottom: 140rpx;
}

/* 全局按钮样式 */
button {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  font-family: inherit;
}

button:hover {
  transform: translateY(-2rpx);
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

/* 全局卡片样式 */
.card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24rpx;
  box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.card::before {
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

.card:hover {
  transform: translateY(-4rpx);
  box-shadow: 0 12rpx 40rpx rgba(0, 0, 0, 0.12);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .bottom-nav {
    padding: 10rpx 0;
  }
  
  .nav-item {
    padding: 8rpx 20rpx;
  }
  
  .nav-icon {
    font-size: 36rpx;
  }
  
  .nav-text {
    font-size: 18rpx;
  }
  
  .router-view-container {
    padding-bottom: 120rpx;
  }
}

/* 加载动画 */
.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid #f3f3f3;
  border-top: 4rpx solid #FF6B35;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* 微交互效果 */
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
  background: rgba(255, 107, 53, 0.3);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

.tap-effect:active::after {
  width: 300rpx;
  height: 300rpx;
}
</style>