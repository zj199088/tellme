<template>
  <view class="template-list-container">
    <view class="header">
      <text class="title">健身模板</text>
    </view>
    <view class="content">
      <view v-for="template in templates" :key="template.id" class="template-card" @click="navigateToDetail(template.id)">
        <view class="template-image">
          <image :src="template.image" mode="aspectFill"></image>
        </view>
        <view class="template-info">
          <text class="template-name">{{ template.name }}</text>
          <text class="template-description">{{ template.description }}</text>
          <view class="template-meta">
            <text class="difficulty-tag" :class="template.difficulty">{{ getDifficultyText(template.difficulty) }}</text>
          </view>
        </view>
      </view>
      <view v-if="loading" class="loading">
        <text>加载中...</text>
      </view>
      <view v-if="error" class="error">
        <text>{{ error }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const templates = ref<any[]>([]);
const loading = ref(true);
const error = ref('');

onMounted(() => {
  fetchTemplates();
});

const fetchTemplates = async () => {
  try {
    loading.value = true;
    const response = await fetch('/api/templates/list');
    const data = await response.json();
    templates.value = data.templates;
  } catch (err) {
    error.value = '加载模板失败，请重试';
    console.error(err);
  } finally {
    loading.value = false;
  }
};

const navigateToDetail = (templateId: number) => {
  router.push(`/pages/template/detail?id=${templateId}`);
};

const getDifficultyText = (difficulty: string) => {
  const map = {
    beginner: '新手',
    intermediate: '中级',
    advanced: '高级'
  };
  return map[difficulty as keyof typeof map] || difficulty;
};
</script>

<style scoped>
.template-list-container {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background-color: #FF6B35;
  color: white;
  padding: 20rpx;
  text-align: center;
}

.title {
  font-size: 32rpx;
  font-weight: bold;
}

.content {
  padding: 20rpx;
}

.template-card {
  background-color: white;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.template-image {
  width: 100%;
  height: 300rpx;
  overflow: hidden;
}

.template-image image {
  width: 100%;
  height: 100%;
}

.template-info {
  padding: 20rpx;
}

.template-name {
  font-size: 28rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
  display: block;
}

.template-description {
  font-size: 24rpx;
  color: #666;
  margin-bottom: 15rpx;
  line-height: 1.4;
}

.template-meta {
  display: flex;
  align-items: center;
}

.difficulty-tag {
  padding: 4rpx 12rpx;
  border-radius: 12rpx;
  font-size: 20rpx;
  color: white;
}

.difficulty-tag.beginner {
  background-color: #4ECDC4;
}

.difficulty-tag.intermediate {
  background-color: #FFD166;
  color: #333;
}

.difficulty-tag.advanced {
  background-color: #EF476F;
}

.loading, .error {
  text-align: center;
  padding: 40rpx 0;
  font-size: 24rpx;
  color: #666;
}

.error {
  color: #EF476F;
}
</style>
