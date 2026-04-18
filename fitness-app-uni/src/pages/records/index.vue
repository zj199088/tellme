<template>
  <div class="records-container">
    <div class="particle-bg" id="particleBg"></div>
    <div class="glow-orbs">
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
      <div class="orb orb-3"></div>
    </div>
    

    
    <div class="content">
      <div class="filter-section glow-card animate-in">
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
        </div>
      </div>

      <div class="records-section glow-card animate-in">
        <div class="section-header">
          <h2 class="section-title">
            <span class="title-icon">📋</span>
            训练记录
          </h2>
          <span class="total-count">共 {{ totalRecords }} 条</span>
        </div>
        <div class="record-list" v-if="records.length > 0">
          <div class="record-card" v-for="(record, index) in records" :key="record.id" 
               :style="{ animationDelay: `${index * 0.1}s` }">
            <div class="record-header">
              <h3 class="record-plan">{{ record.plan_name || record.planName }}</h3>
            </div>
            <div class="record-details glow-card">
              <div class="record-details-left">
                <span class="record-exercise">{{ record.exerciseName }}</span>
                <span class="record-sets" v-if="record.setsCompleted">
                  完成组数: {{ JSON.parse(record.setsCompleted).length }}
                </span>
                <span class="record-duration">
                  <span class="duration-icon">⏱</span>
                  {{ Math.round((record.duration || 0) / 60) }}分钟
                </span>
                <span class="record-weight" v-if="record.weight !== null && record.weight !== undefined">
                  <span class="weight-icon">🏋️</span>
                  {{ record.weight }}kg
                </span>
              </div>
              <div class="record-date">{{ formatDateTime(record.date) }}</div>
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
          <button class="page-button glow-button" @click="changePage(1)" :disabled="currentPage === 1">
            首页
          </button>
          <button class="page-button glow-button" @click="changePage(currentPage - 1)" :disabled="currentPage === 1">
            上一页
          </button>
          <span class="page-info">
            第 {{ currentPage }} / {{ totalPages }} 页
          </span>
          <button class="page-button glow-button" @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages">
            下一页
          </button>
          <button class="page-button glow-button" @click="changePage(totalPages)" :disabled="currentPage === totalPages">
            末页
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue';
import api from '../../utils/api';
import type { WorkoutRecord, FitnessPlan } from '../../utils/api';

const records = ref<Array<WorkoutRecord & { plan_name?: string; planName?: string }>>([]);
const plans = ref<FitnessPlan[]>([]);
const loading = ref(false);
const currentPage = ref(1);
const totalPages = ref(1);
const totalRecords = ref(0);
const pageSize = ref(10);

const filters = ref({
  planId: '',
  date: ''
});

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

const fetchPlans = async () => {
  try {
    const response = await api.plans.getList();
    if (response.code === 200 && response.data) {
      plans.value = response.data;
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
      planId: filters.value.planId ? parseInt(filters.value.planId) : undefined,
      date: filters.value.date || undefined
    };

    const response = await api.workout.getSessions(params);
    if (response.code === 200 && response.data) {
      records.value = response.data.records;
      totalPages.value = response.data.totalPages;
      totalRecords.value = response.data.total;
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

const formatDateTime = (dateString: string): string => {
  const date = new Date(dateString);
  return `${date.getFullYear()}:${String(date.getMonth() + 1).padStart(2, '0')}:${String(date.getDate()).padStart(2, '0')} - ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`;
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

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
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

.records-container {
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
  text-align: center;
  border-bottom-left-radius: 20.0px;
  border-bottom-right-radius: 20.0px;
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
  justify-content: center;
  gap: 7.5px;
}

.back-btn {
  position: absolute;
  left: 15.0px;
  top: 50%;
  transform: translateY(-50%);
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
}

.back-btn:hover {
  background: rgba(0, 245, 255, 0.2);
  border-color: var(--neon-cyan);
  box-shadow: 0 0 15px rgba(0, 245, 255, 0.3);
}

.back-btn svg {
  width: 20.0px;
  height: 20.0px;
  color: var(--neon-cyan);
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

.filter-section,
.records-section {
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

.filter-section::before,
.records-section::before {
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

.filter-section::after,
.records-section::after {
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

.filter-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.filter-select,
.filter-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid var(--border-color);
  border-radius: 10px;
  font-size: 14px;
  background: var(--bg-tertiary);
  color: var(--text-primary);
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.filter-select:focus,
.filter-input:focus {
  outline: none;
  border-color: var(--neon-cyan);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.2);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 10px;
  position: relative;
}

.title-icon {
  font-size: 22px;
}

.total-count {
  font-size: 12px;
  color: var(--neon-cyan);
  font-weight: 600;
  background: rgba(0, 245, 255, 0.1);
  padding: 4px 10px;
  border-radius: 12px;
  border: 1px solid rgba(0, 245, 255, 0.2);
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.record-card {
  background: rgba(0, 0, 0, 0.3);
  border-radius: 10px;
  padding: 12px;
  border: 1px solid rgba(0, 245, 255, 0.1);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.record-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--gradient-cyan);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.record-card:hover {
  background: rgba(0, 245, 255, 0.05);
  border-color: rgba(0, 245, 255, 0.3);
  transform: translateX(2px);
}

.record-card:hover::before {
  opacity: 1;
}

.record-header {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.record-plan {
  font-size: 13px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.record-date {
  font-size: 10px;
  color: var(--text-muted);
  margin: 0;
  font-weight: 500;
}

.record-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  background: rgba(0, 245, 255, 0.05);
  border-radius: 8px;
  border: 1px solid rgba(0, 245, 255, 0.1);
}

.record-details-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.record-exercise {
  font-size: 11px;
  color: var(--text-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 140px;
  font-weight: 500;
}

.record-sets {
  font-size: 10px;
  color: var(--text-muted);
  white-space: nowrap;
}

.record-duration {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 10px;
  color: var(--text-muted);
  white-space: nowrap;
}

.duration-icon {
  font-size: 10px;
}

.record-weight {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 10px;
  color: var(--text-muted);
  white-space: nowrap;
}

.weight-icon {
  font-size: 10px;
}

.record-date {
  font-size: 10px;
  color: var(--text-muted);
  font-weight: 500;
  line-height: 1.2;
  text-align: right;
  white-space: nowrap;
}

.loading,
.no-records {
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
  border: 4px solid rgba(0, 245, 255, 0.1);
  border-top: 4px solid var(--neon-cyan);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  box-shadow: 0 0 20px var(--neon-cyan);
}

.loading p,
.no-records p {
  color: var(--text-secondary);
  font-size: 16px;
}

.empty-icon {
  font-size: 60px;
  opacity: 0.5;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid rgba(0, 245, 255, 0.1);
  flex-wrap: wrap;
}

.page-button {
  padding: 10px 18px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  position: relative;
  overflow: hidden;
}

.page-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: var(--text-secondary);
  font-size: 14px;
  padding: 0 15px;
}

@media (max-width: 768px) {
  .content {
    padding: 10.0px 7.5px;
    padding-bottom: 80px;
  }
  
  .filter-section,
  .records-section {
    padding: 15.0px;
  }
  
  .filter-grid {
    grid-template-columns: 1fr;
  }
  
  .title {
    font-size: 17.0px;
  }
  
  .section-title {
    font-size: 15px;
  }
  
  .record-plan {
    font-size: 14px;
  }
}
</style>
