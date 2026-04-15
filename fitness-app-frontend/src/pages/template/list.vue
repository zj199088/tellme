<template>
  <div class="template-list-container">
    <div class="header">
      <h1 class="title">健身模板</h1>
      <div class="header-bg"></div>
    </div>
    <div class="content">
      <div v-for="(template, index) in templates" :key="template.id" class="template-card animate-in" :style="{ animationDelay: `${index * 0.1}s` }" @click="navigateToDetail(template.id)">
        <div class="template-image">
          <img :src="template.image || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=fitness%20training%20workout%20template%20background&image_size=landscape_16_9'" alt="{{ template.name }}">
          <div class="image-overlay"></div>
        </div>
        <div class="template-info">
          <h3 class="template-name">{{ template.name }}</h3>
          <p class="template-description">{{ template.description }}</p>
          <div class="template-meta">
            <span class="difficulty-tag" :class="template.difficulty">{{ getDifficultyText(template.difficulty) }}</span>
            <span class="arrow-icon">→</span>
          </div>
        </div>
      </div>
      <div v-if="loading" class="loading">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
      <div v-if="error" class="error">
        <p>{{ error }}</p>
        <button class="retry-button" @click="fetchTemplates">重试</button>
      </div>
    </div>
  </div>
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

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.animate-in {
  animation: fadeInUp 0.6s ease forwards;
}

/* 主容器 */
.template-list-container {
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

/* 模板卡片 */
.template-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24rpx;
  margin-bottom: 25rpx;
  box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  cursor: pointer;
}

.template-card::before {
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

.template-card:hover {
  transform: translateY(-4rpx) scale(1.02);
  box-shadow: 0 12rpx 40rpx rgba(0, 0, 0, 0.12);
  animation: pulse 0.6s ease-in-out;
}

/* 图片区域 */
.template-image {
  width: 100%;
  height: 320rpx;
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
  transform: scale(1.05);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0,0,0,0.1) 0%, rgba(0,0,0,0.3) 100%);
  transition: background 0.3s ease;
}

.template-card:hover .image-overlay {
  background: linear-gradient(to bottom, rgba(0,0,0,0.2) 0%, rgba(0,0,0,0.4) 100%);
}

/* 信息区域 */
.template-info {
  padding: 30rpx;
}

.template-name {
  font-size: 30rpx;
  font-weight: bold;
  margin-bottom: 12rpx;
  color: #333;
  transition: color 0.3s ease;
}

.template-card:hover .template-name {
  color: #FF6B35;
}

.template-description {
  font-size: 24rpx;
  color: #666;
  margin-bottom: 20rpx;
  line-height: 1.5;
}

/* 元信息 */
.template-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.difficulty-tag {
  padding: 6rpx 16rpx;
  border-radius: 16rpx;
  font-size: 20rpx;
  font-weight: 600;
  color: white;
  transition: all 0.3s ease;
}

.difficulty-tag.beginner {
  background: linear-gradient(135deg, #4ECDC4, #45B7AA);
  box-shadow: 0 2rpx 8rpx rgba(78, 205, 196, 0.4);
}

.difficulty-tag.intermediate {
  background: linear-gradient(135deg, #FFD166, #FFC133);
  color: #333;
  box-shadow: 0 2rpx 8rpx rgba(255, 209, 102, 0.4);
}

.difficulty-tag.advanced {
  background: linear-gradient(135deg, #EF476F, #E83C62);
  box-shadow: 0 2rpx 8rpx rgba(239, 71, 111, 0.4);
}

.arrow-icon {
  font-size: 28rpx;
  color: #999;
  transition: all 0.3s ease;
  font-weight: bold;
}

.template-card:hover .arrow-icon {
  color: #FF6B35;
  transform: translateX(5rpx);
}

/* 加载状态 */
.loading {
  text-align: center;
  padding: 80rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20rpx;
}

.loading-spinner {
  width: 60rpx;
  height: 60rpx;
  border: 4rpx solid #f3f3f3;
  border-top: 4rpx solid #FF6B35;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.loading p {
  font-size: 24rpx;
  color: #666;
  font-weight: 500;
}

/* 错误状态 */
.error {
  text-align: center;
  padding: 80rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 25rpx;
}

.error p {
  font-size: 24rpx;
  color: #EF476F;
  font-weight: 500;
  max-width: 80%;
}

.retry-button {
  padding: 15rpx 40rpx;
  background: linear-gradient(135deg, #FF6B35, #FF8E53);
  color: white;
  border: none;
  border-radius: 12rpx;
  font-size: 22rpx;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4rpx 12rpx rgba(255, 107, 53, 0.3);
}

.retry-button:hover {
  transform: translateY(-2rpx);
  box-shadow: 0 8rpx 20rpx rgba(255, 107, 53, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content {
    padding: 20rpx 15rpx;
  }
  
  .template-card {
    margin-bottom: 20rpx;
  }
  
  .template-image {
    height: 280rpx;
  }
  
  .template-info {
    padding: 25rpx;
  }
  
  .title {
    font-size: 32rpx;
  }
  
  .template-name {
    font-size: 28rpx;
  }
}
</style>
