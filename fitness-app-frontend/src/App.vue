<template>
  <div class="app-container">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
    <nav class="bottom-nav">
      <router-link to="/pages/home/index" class="nav-item" active-class="active">
        <text class="nav-icon">🏠</text>
        <text class="nav-text">首页</text>
      </router-link>
      <router-link to="/pages/template/list" class="nav-item" active-class="active">
        <text class="nav-icon">📋</text>
        <text class="nav-text">模板</text>
      </router-link>
      <router-link to="/pages/ai/index" class="nav-item" active-class="active">
        <text class="nav-icon">🤖</text>
        <text class="nav-text">AI助手</text>
      </router-link>
    </nav>
  </div>
</template>

<script setup lang="ts">
// App组件
</script>

<style>
.app-container {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: white;
  display: flex;
  justify-content: space-around;
  padding: 15rpx 0;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-decoration: none;
  color: #666;
  font-size: 20rpx;
  padding: 10rpx;
}

.nav-item.active {
  color: #FF6B35;
}

.nav-icon {
  font-size: 36rpx;
  margin-bottom: 5rpx;
}

.nav-text {
  font-size: 20rpx;
}

/* 为内容区域添加底部padding，避免被导航栏遮挡 */
.router-view-container {
  flex: 1;
  padding-bottom: 120rpx;
}
</style>