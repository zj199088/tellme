<template>
  <view class="container">
    <text class="title">欢迎使用健身应用</text>
    <text class="subtitle">这是首页</text>
  </view>
</template>

<script setup lang="ts">
// 首页组件
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 20px;
  background-color: #f5f5f5;
}

.title {
  font-size: 24px;
  font-weight: bold;
  color: #007aff;
  margin-bottom: 16px;
}

.subtitle {
  font-size: 16px;
  color: #666;
}
</style>