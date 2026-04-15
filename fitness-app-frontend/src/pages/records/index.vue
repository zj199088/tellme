<template>
  <div class="records-container">
    <div class="particle-bg" id="particleBg"></div>
    <div class="glow-orb orb-1"></div>
    <div class="glow-orb orb-2"></div>
    <div class="glow-orb orb-3"></div>
    
    <div class="header">
      <div class="scan-line"></div>
      <h1 class="title neon-glow">锻炼记录</h1>
      <div class="header-bg"></div>
    </div>
    
    <div class="content">
      <div class="filter-section glass-card animate-in">
        <div class="filter-grid">
          <div class="filter-item">
            <label class="filter-label">计划</label>
            <select v-model="filters.planId" class="filter-select" @change="fetchRecords">
              <option value="">全部计划</option>
              <option v-for="plan in plans" :key="plan.id" :value="plan.id">
                {{ plan.name }}
              </option>
            </select>
          </div>
          <div class="filter-item">
            <label class="filter-label">日期</label>
            <input type="date" v-model="filters.date" class="filter-input" @change="fetchRecords" />
          </div>
          <div class="filter-item">
            <label class="filter-label">状态</label>
            <select v-model="filters.status" class="filter-select" @change="fetchRecords">
              <option value="">全部状态</option>
              <option value="completed">已完成</option>
              <option value="pending">未完成</option>
            </select>
          </div>
        </div>
      </div>

      <div class="records-section glass-card animate-in">
        <h2 class="section-title">
          <span class="title-icon">📊</span>
          训练记录
        </h2>
        <div class="record-list" v-if="records.length > 0">
          <div class="record-card" v-for="(record, index) in records" :key="record.id" 
               :style="{ animationDelay: `${index * 0.1}s` }">
            <div class="record-header">
              <div class="record-info">
                <h3 class="record-plan">{{ getPlanName(record.planId) }}</h3>
                <p class="record-date">{{ formatDate(record.date) }}</p>
              </div>
              <div class="record-status" :class="record.completed ? 'completed' : 'pending'">
                {{ record.completed ? '已完成' : '未完成' }}
              </div>
            </div>
            <div class="record-details" v-if="record.exercises && record.exercises.length > 0">
              <h4 class="details-title">训练内容</h4>
              <div class="exercise-list">
                <div class="exercise-item" v-for="exercise in record.exercises" :key="exercise.id">
                  <div class="exercise-info">
                    <span class="exercise-name">{{ exercise.name }}</span>
                    <span class="exercise-sets">{{ exercise.sets }}组 × {{ exercise.reps }}次</span>
                  </div>
                  <div class="exercise-status" :class="exercise.completed ? 'completed' : ''">
                    {{ exercise.completed ? '✓' : '○' }}
                  </div>
                </div>
              </div>
            </div>
            <div class="record-footer">
              <span class="record-duration">
                <span class="duration-icon">⏱</span>
                {{ record.duration || 0 }}分钟
              </span>
              <button class="detail-button neon-button" @click="viewRecordDetail(record)">
                详情
              </button>
            </div>
          </div>
        </div>
        <div v-if="loading" class="loading">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>
        <div v-if="!loading && records.length === 0" class="no-records">
          <div class="empty-icon">📝</div>
          <p>暂无训练记录</p>
        </div>

        <div class="pagination" v-if="totalPages > 1">
          <button class="page-button neon-button" @click="changePage(1)" :disabled="currentPage === 1">
            首页
          </button>
          <button class="page-button neon-button" @click="changePage(currentPage - 1)" :disabled="currentPage === 1">
            上一页
          </button>
          <span class="page-info">
            第 {{ currentPage }} / {{ totalPages }} 页
          </span>
          <button class="page-button neon-button" @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages">
            下一页
          </button>
          <button class="page-button neon-button" @click="changePage(totalPages)" :disabled="currentPage === totalPages">
            末页
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue';
import axios from 'axios';

interface Exercise {
  id: number;
  name: string;
  sets: number;
  reps: number;
  completed?: boolean;
}

interface TrainingRecord {
  id: number;
  planId: number;
  date: string;
  completed: boolean;
  exercises: Exercise[];
  duration?: number;
  exercisesJson?: string;
}

interface Plan {
  id: number;
  name: string;
}

const records = ref<TrainingRecord[]>([]);
const plans = ref<Plan[]>([]);
const loading = ref(true);
const currentPage = ref(1);
const totalPages = ref(1);
const pageSize = ref(10);

const filters = ref({
  planId: '',
  date: '',
  status: ''
});

const initParticles = () => {
  const particleBg = document.getElementById('particleBg');
  if (!particleBg) return;

  for (let i = 0; i < 30; i++) {
    const particle = document.createElement('div');
    particle.className = 'particle';
    const size = Math.random() * 4 + 2;
    particle.style.width = `${size}px`;
    particle.style.height = `${size}px`;
    particle.style.left = `${Math.random() * 100}%`;
    particle.style.top = `${Math.random() * 100}%`;
    particle.style.animationDelay = `${Math.random() * 15}s`;
    particle.style.animationDuration = `${Math.random() * 20 + 15}s`;
    particleBg.appendChild(particle);
  }
};

const getToken = () => {
  return localStorage.getItem('token');
};

const apiClient = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json'
  }
});

apiClient.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

const fetchPlans = async () => {
  try {
    const response = await apiClient.get('/api/plan/list');
    if (response.data.code === 200 && response.data.data) {
      plans.value = response.data.data;
    }
  } catch (error) {
    console.error('获取计划列表失败:', error);
  }
};

const fetchRecords = async () => {
  try {
    loading.value = true;
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      planId: filters.value.planId,
      date: filters.value.date,
      status: filters.value.status
    };

    const response = await apiClient.get('/api/workout/records', { params });
    if (response.data.code === 200 && response.data.data) {
      records.value = response.data.data.records.map((record: any) => {
        if (record.exercisesJson) {
          record.exercises = JSON.parse(record.exercisesJson);
        }
        return record;
      });
      totalPages.value = response.data.data.totalPages;
    }
  } catch (error) {
    console.error('获取训练记录失败:', error);
  } finally {
    loading.value = false;
  }
};

const changePage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    fetchRecords();
  }
};

const getPlanName = (planId: number): string => {
  const plan = plans.value.find(p => p.id === planId);
  return plan ? plan.name : '未知计划';
};

const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
};

const viewRecordDetail = (record: TrainingRecord) => {
  console.log('查看记录详情:', record);
};

onMounted(() => {
  nextTick(() => {
    initParticles();
  });
  fetchPlans();
  fetchRecords();
});
</script>

<style scoped>
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
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

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes scanLine {
  0% {
    transform: translateY(-100%);
  }
  100% {
    transform: translateY(100vh);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

.animate-in {
  animation: fadeInUp 0.6s ease forwards;
}

.records-container {
  width: 100%;
  min-height: 100vh;
  background: var(--bg-primary);
  position: relative;
  overflow: hidden;
}

.particle-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.particle {
  position: absolute;
  border-radius: 50%;
  background: var(--neon-cyan);
  box-shadow: 0 0 10px var(--neon-cyan), 0 0 20px var(--neon-cyan);
  animation: float linear infinite;
  opacity: 0.6;
}

.glow-orb {
  position: fixed;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.3;
  pointer-events: none;
  z-index: 1;
}

.orb-1 {
  width: 400px;
  height: 400px;
  background: var(--neon-cyan);
  top: -100px;
  left: -100px;
  animation: float 8s ease-in-out infinite;
}

.orb-2 {
  width: 300px;
  height: 300px;
  background: var(--neon-purple);
  top: 50%;
  right: -50px;
  animation: float 10s ease-in-out infinite reverse;
}

.orb-3 {
  width: 350px;
  height: 350px;
  background: var(--neon-pink);
  bottom: -100px;
  left: 30%;
  animation: float 12s ease-in-out infinite;
}

.header {
  position: relative;
  background: var(--glass-bg);
  backdrop-filter: blur(20px);
  padding: 40px 20px;
  text-align: center;
  border-bottom: 1px solid var(--glass-border);
  overflow: hidden;
  z-index: 10;
}

.scan-line {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--neon-cyan), transparent);
  animation: scanLine 3s linear infinite;
  z-index: 2;
}

.title {
  font-size: 28px;
  font-weight: bold;
  position: relative;
  z-index: 5;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.neon-glow {
  text-shadow: 0 0 10px var(--neon-cyan), 0 0 20px var(--neon-cyan), 0 0 30px var(--neon-cyan);
  animation: pulse 2s ease-in-out infinite;
}

.header-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: radial-gradient(circle at 20% 80%, rgba(0, 245, 255, 0.1) 0%, transparent 50%),
                    radial-gradient(circle at 80% 20%, rgba(139, 92, 246, 0.1) 0%, transparent 50%);
}

.content {
  padding: 30px 20px;
  padding-bottom: 120px;
  position: relative;
  z-index: 10;
}

.filter-section {
  padding: 25px;
  margin-bottom: 25px;
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.filter-select,
.filter-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid var(--glass-border);
  border-radius: 12px;
  font-size: 14px;
  background: var(--glass-bg);
  color: var(--text-primary);
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.filter-select:focus,
.filter-input:focus {
  outline: none;
  border-color: var(--neon-cyan);
  box-shadow: 0 0 0 3px rgba(0, 245, 255, 0.1), 0 0 20px rgba(0, 245, 255, 0.2);
}

.records-section {
  padding: 30px;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: var(--text-primary);
  margin-bottom: 25px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-icon {
  font-size: 24px;
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.record-card {
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: 16px;
  padding: 20px;
  transition: all 0.3s ease;
  animation: fadeInUp 0.6s ease forwards;
  opacity: 0;
}

.record-card:hover {
  transform: translateY(-5px);
  border-color: var(--neon-cyan);
  box-shadow: 0 10px 40px rgba(0, 245, 255, 0.2), 0 0 30px rgba(0, 245, 255, 0.1);
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid var(--glass-border);
}

.record-info {
  flex: 1;
}

.record-plan {
  font-size: 18px;
  font-weight: bold;
  color: var(--text-primary);
  margin: 0 0 5px 0;
}

.record-date {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.record-status {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.record-status.completed {
  background: linear-gradient(135deg, var(--neon-green), var(--neon-green));
  color: var(--bg-primary);
  box-shadow: 0 0 15px var(--neon-green);
}

.record-status.pending {
  background: linear-gradient(135deg, var(--neon-orange), var(--neon-orange));
  color: var(--bg-primary);
  box-shadow: 0 0 15px var(--neon-orange);
}

.record-details {
  margin-bottom: 15px;
}

.details-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-secondary);
  margin-bottom: 10px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.exercise-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.exercise-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 8px;
}

.exercise-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.exercise-name {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 500;
}

.exercise-sets {
  font-size: 12px;
  color: var(--text-secondary);
}

.exercise-status {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 14px;
  font-weight: bold;
}

.exercise-status.completed {
  background: var(--neon-green);
  color: var(--bg-primary);
  box-shadow: 0 0 10px var(--neon-green);
}

.record-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid var(--glass-border);
}

.record-duration {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--text-secondary);
}

.duration-icon {
  font-size: 16px;
}

.detail-button {
  padding: 8px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.glass-card {
  background: var(--glass-bg);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--glass-border);
  border-radius: 16px;
  position: relative;
  overflow: hidden;
}

.glass-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--gradient-primary);
}

.neon-button {
  background: var(--glass-bg);
  color: var(--neon-cyan);
  border: 1px solid var(--neon-cyan);
  box-shadow: 0 0 10px rgba(0, 245, 255, 0.3);
  cursor: pointer;
  transition: all 0.3s ease;
}

.neon-button:hover:not(:disabled) {
  background: var(--neon-cyan);
  color: var(--bg-primary);
  box-shadow: 0 0 20px var(--neon-cyan), 0 0 40px var(--neon-cyan);
  transform: translateY(-2px);
}

.neon-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 20px;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 3px solid var(--glass-border);
  border-top-color: var(--neon-cyan);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  box-shadow: 0 0 20px var(--neon-cyan);
}

.loading p {
  color: var(--text-secondary);
  font-size: 14px;
}

.no-records {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 15px;
}

.empty-icon {
  font-size: 48px;
  opacity: 0.5;
}

.no-records p {
  color: var(--text-secondary);
  font-size: 14px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid var(--glass-border);
  flex-wrap: wrap;
}

.page-button {
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
}

.page-info {
  color: var(--text-secondary);
  font-size: 14px;
  padding: 0 15px;
}

@media (max-width: 768px) {
  .header {
    padding: 30px 15px;
  }

  .title {
    font-size: 24px;
  }

  .content {
    padding: 20px 15px;
    padding-bottom: 100px;
  }

  .filter-grid {
    grid-template-columns: 1fr;
  }

  .filter-section,
  .records-section {
    padding: 20px;
  }

  .pagination {
    gap: 10px;
  }

  .page-button {
    padding: 8px 15px;
    font-size: 12px;
  }
}
</style>