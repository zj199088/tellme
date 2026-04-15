<template>
  <view class="template-detail-container">
    <view class="header">
      <text class="title">{{ template?.name || '模板详情' }}</text>
      <view class="back-button" @click="goBack">
        <text class="back-icon">←</text>
      </view>
    </view>
    <view class="content">
      <view v-if="loading" class="loading">
        <text>加载中...</text>
      </view>
      <view v-else-if="error" class="error">
        <text>{{ error }}</text>
      </view>
      <view v-else-if="template" class="template-detail">
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
        
        <view class="training-days">
          <text class="section-title">训练计划</text>
          <view v-for="day in templateDays" :key="day.id" class="day-card">
            <view class="day-header">
              <text class="day-name">{{ getDayName(day.day_of_week) }}</text>
              <text v-if="day.is_rest_day" class="rest-day-tag">休息日</text>
              <text v-else class="duration">{{ day.estimated_duration }}分钟</text>
            </view>
            <view v-if="day.is_rest_day" class="rest-note">
              <text>{{ day.rest_note || '好好休息，让肌肉恢复' }}</text>
            </view>
            <view v-else class="exercises">
              <view v-for="exercise in getExercisesByDayId(day.id)" :key="exercise.id" class="exercise-item">
                <text class="exercise-name">{{ exercise.exercise_name }}</text>
                <text class="exercise-detail">
                  {{ exercise.sets }}组
                  <text v-if="exercise.reps"> × {{ exercise.reps }}</text>
                  <text v-else-if="exercise.duration"> × {{ exercise.duration }}</text>
                </text>
                <text v-if="exercise.note" class="exercise-note">{{ exercise.note }}</text>
              </view>
            </view>
          </view>
        </view>
        
        <view class="create-plan-section">
          <text class="section-title">创建计划</text>
          <view class="form-item">
            <text class="label">计划名称</text>
            <input type="text" v-model="planForm.name" placeholder="输入计划名称" class="input" />
          </view>
          <view class="form-item">
            <text class="label">健身目标</text>
            <input type="text" v-model="planForm.goal" placeholder="输入健身目标" class="input" />
          </view>
          <view class="form-item">
            <text class="label">训练周期</text>
            <input type="number" v-model="planForm.duration_weeks" placeholder="输入训练周期(周)" class="input" />
          </view>
          <view class="form-item">
            <text class="label">开始日期</text>
            <input type="date" v-model="planForm.start_date" class="input" />
          </view>
          <button class="create-button" @click="createPlan" :disabled="isCreating">
            {{ isCreating ? '创建中...' : '从模板创建计划' }}
          </button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();
const templateId = computed(() => route.query.id as string);

const template = ref<any>(null);
const templateDays = ref<any[]>([]);
const templateExercises = ref<any[]>([]);
const loading = ref(true);
const error = ref('');
const isCreating = ref(false);

const planForm = ref({
  name: '',
  goal: '',
  duration_weeks: 4,
  start_date: new Date().toISOString().split('T')[0]
});

onMounted(() => {
  fetchTemplateDetail();
  fetchTemplateDays();
});

const fetchTemplateDetail = async () => {
  try {
    loading.value = true;
    const response = await fetch(`/api/templates/detail/${templateId.value}`);
    const data = await response.json();
    template.value = data;
    planForm.value.name = `我的${data.name}`;
  } catch (err) {
    error.value = '加载模板详情失败，请重试';
    console.error(err);
  } finally {
    loading.value = false;
  }
};

const fetchTemplateDays = async () => {
  try {
    const response = await fetch(`/api/templates/${templateId.value}/days`);
    const data = await response.json();
    templateDays.value = data;
    
    // 为每个训练日获取训练动作
    for (const day of templateDays.value) {
      if (!day.is_rest_day) {
        await fetchTemplateExercises(day.id);
      }
    }
  } catch (err) {
    console.error('加载训练日失败', err);
  }
};

const fetchTemplateExercises = async (templateDayId: number) => {
  try {
    const response = await fetch(`/api/templates/days/${templateDayId}/exercises`);
    const data = await response.json();
    templateExercises.value = [...templateExercises.value, ...data];
  } catch (err) {
    console.error('加载训练动作失败', err);
  }
};

const getExercisesByDayId = (dayId: number) => {
  return templateExercises.value.filter(exercise => exercise.template_day_id === dayId);
};

const getDayName = (dayOfWeek: number) => {
  const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
  return days[dayOfWeek - 1] || `第${dayOfWeek}天`;
};

const getDifficultyText = (difficulty: string) => {
  const map = {
    beginner: '新手',
    intermediate: '中级',
    advanced: '高级'
  };
  return map[difficulty as keyof typeof map] || difficulty;
};

const createPlan = async () => {
  if (!planForm.value.name || !planForm.value.goal || !planForm.value.duration_weeks || !planForm.value.start_date) {
    alert('请填写所有必填项');
    return;
  }

  try {
    isCreating.value = true;
    const response = await fetch('/api/templates/create-plan', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        template_id: templateId.value,
        name: planForm.value.name,
        type: 'template',
        goal: planForm.value.goal,
        difficulty: template.value.difficulty,
        duration_weeks: planForm.value.duration_weeks,
        start_date: planForm.value.start_date
      })
    });

    const data = await response.json();
    if (data.success) {
      alert('计划创建成功');
      // 跳转到首页或计划详情页
      router.push('/pages/home/index');
    } else {
      alert('计划创建失败，请重试');
    }
  } catch (err) {
    alert('网络错误，请重试');
    console.error(err);
  } finally {
    isCreating.value = false;
  }
};

const goBack = () => {
  router.back();
};
</script>

<style scoped>
.template-detail-container {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background-color: #FF6B35;
  color: white;
  padding: 20rpx;
  position: relative;
  text-align: center;
}

.title {
  font-size: 32rpx;
  font-weight: bold;
}

.back-button {
  position: absolute;
  left: 20rpx;
  top: 50%;
  transform: translateY(-50%);
  padding: 10rpx;
}

.back-icon {
  font-size: 36rpx;
  font-weight: bold;
}

.content {
  padding: 20rpx;
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

.template-detail {
  background-color: white;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
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
  border-bottom: 1rpx solid #f0f0f0;
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

.training-days {
  padding: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.section-title {
  font-size: 28rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
  display: block;
}

.day-card {
  background-color: #f9f9f9;
  border-radius: 12rpx;
  padding: 15rpx;
  margin-bottom: 15rpx;
}

.day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10rpx;
}

.day-name {
  font-size: 24rpx;
  font-weight: bold;
}

.rest-day-tag {
  font-size: 20rpx;
  color: #EF476F;
  font-weight: bold;
}

.duration {
  font-size: 20rpx;
  color: #666;
}

.rest-note {
  font-size: 22rpx;
  color: #666;
  padding: 10rpx 0;
}

.exercises {
  margin-top: 10rpx;
}

.exercise-item {
  padding: 10rpx 0;
  border-bottom: 1rpx solid #e0e0e0;
}

.exercise-item:last-child {
  border-bottom: none;
}

.exercise-name {
  font-size: 24rpx;
  font-weight: bold;
  margin-bottom: 5rpx;
  display: block;
}

.exercise-detail {
  font-size: 22rpx;
  color: #666;
  margin-bottom: 5rpx;
  display: block;
}

.exercise-note {
  font-size: 20rpx;
  color: #999;
  display: block;
}

.create-plan-section {
  padding: 20rpx;
}

.form-item {
  margin-bottom: 20rpx;
}

.label {
  font-size: 24rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
  display: block;
}

.input {
  width: 100%;
  padding: 15rpx;
  border: 1rpx solid #e0e0e0;
  border-radius: 8rpx;
  font-size: 24rpx;
}

.create-button {
  width: 100%;
  padding: 20rpx;
  background-color: #FF6B35;
  color: white;
  font-size: 28rpx;
  font-weight: bold;
  border-radius: 8rpx;
  margin-top: 20rpx;
}

.create-button:disabled {
  background-color: #ccc;
}
</style>
