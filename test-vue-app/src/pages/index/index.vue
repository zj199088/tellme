<template>
  <view class="container">
    <text class="title">Hello Uni-App</text>
    <text class="subtitle">这是一个测试应用</text>
  </view>
</template>

<script setup lang="ts">
// 页面逻辑
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 16px;
}

.subtitle {
  font-size: 16px;
  color: #666;
}
</style>