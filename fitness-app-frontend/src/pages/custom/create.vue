<template>
  <div class="custom-plan-container">
    <div class="particle-bg" id="particleBg"></div>
    <div class="glow-orbs">
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
      <div class="orb orb-3"></div>
    </div>
    
    <div class="header">
      <div class="header-content">
        <div class="back-btn" @click="goBack">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
            <polyline points="15 18 9 12 15 6"></polyline>
          </svg>
        </div>
        <div class="header-icon">✨</div>
        <h1 class="title neon-glow">定制计划</h1>
      </div>
      <div class="header-bg"></div>
      <div class="scanline"></div>
    </div>
    
    <div class="content">
      <!-- 未登录状态：显示模板列表 -->
      <div v-if="!isLoggedIn" class="templates-section">
        <div class="templates-header glow-card">
          <h2 class="section-title">
            <span class="title-icon">📋</span>
            选择模板创建计划
          </h2>
          <p class="section-desc">登录后可以查看用户分享的锻炼计划</p>
        </div>
        <div class="templates-list">
          <div v-for="template in templates" :key="template.id" class="template-card glow-card animate-in">
            <div class="template-header">
              <h3 class="template-name">{{ template.name }}</h3>
              <div class="template-difficulty" :class="template.difficulty">
                {{ getDifficultyText(template.difficulty) }}
              </div>
            </div>
            <p class="template-description">{{ template.description }}</p>
            <button class="template-button glow-button" @click="useTemplate(template)">
              <span>使用模板</span>
              <span class="btn-glow"></span>
            </button>
          </div>
        </div>
      </div>
      
      <!-- 已登录状态：显示模板列表和用户分享的计划 -->
      <div v-else class="templates-section">
        <div class="templates-header glow-card">
          <h2 class="section-title">
            <span class="title-icon">📋</span>
            选择模板创建计划
          </h2>
        </div>
        <div class="templates-list">
          <div v-for="template in templates" :key="template.id" class="template-card glow-card animate-in">
            <div class="template-header">
              <h3 class="template-name">{{ template.name }}</h3>
              <div class="template-difficulty" :class="template.difficulty">
                {{ getDifficultyText(template.difficulty) }}
              </div>
            </div>
            <p class="template-description">{{ template.description }}</p>
            <button class="template-button glow-button" @click="useTemplate(template)">
              <span>使用模板</span>
              <span class="btn-glow"></span>
            </button>
          </div>
        </div>
        
        <!-- 用户分享的计划 -->
        <div class="shared-plans-section" v-if="sharedPlans.length > 0">
          <div class="shared-plans-header glow-card">
            <h2 class="section-title">
              <span class="title-icon">👥</span>
              用户分享的计划
            </h2>
          </div>
          <div class="shared-plans-list">
            <div v-for="plan in sharedPlans" :key="plan.id" class="shared-plan-card glow-card animate-in">
              <div class="shared-plan-header">
                <h3 class="shared-plan-name">{{ plan.name }}</h3>
                <div class="shared-plan-author">
                  分享者: {{ plan.author }}
                </div>
              </div>
              <p class="shared-plan-description">{{ plan.description }}</p>
              <button class="shared-plan-button glow-button" @click="useSharedPlan(plan)">
                <span>使用此计划</span>
                <span class="btn-glow"></span>
              </button>
            </div>
          </div>
        </div>
        
        <!-- 创建自定义计划按钮 -->
        <div class="create-custom-section">
          <button class="create-custom-button glow-button" @click="startCreateCustomPlan">
            <span>创建自定义计划</span>
            <span class="btn-glow"></span>
          </button>
        </div>
      </div>
      
      <!-- 自定义计划创建流程 -->
      <div v-if="isCreatingCustomPlan" class="custom-plan-creation">
        <div class="steps-indicator">
          <div class="step-item" :class="{ active: currentStep === 1, completed: currentStep > 1 }" @click="goToStep(1)">
            <div class="step-number">1</div>
            <div class="step-label">基本信息</div>
          </div>
          <div class="step-item" :class="{ active: currentStep === 2, completed: currentStep > 2 }" @click="goToStep(2)">
            <div class="step-number">2</div>
            <div class="step-label">选择动作</div>
          </div>
          <div class="step-item" :class="{ active: currentStep === 3, completed: currentStep > 3 }" @click="goToStep(3)">
            <div class="step-number">3</div>
            <div class="step-label">安排日期</div>
          </div>
          <div class="step-item" :class="{ active: currentStep === 4 }" @click="goToStep(4)">
            <div class="step-number">4</div>
            <div class="step-label">确认</div>
          </div>
        </div>

        <div class="step-content">
          <div v-if="currentStep === 1" class="step-1 animate-in">
            <div class="form-section glow-card">
              <h2 class="section-title">
                <span class="title-icon">📝</span>
                基本信息
              </h2>
              <div class="form-item">
                <label class="form-label">计划名称</label>
                <input type="text" v-model="planForm.name" class="form-input" placeholder="请输入计划名称" />
              </div>
              <div class="form-item">
                <label class="form-label">健身目标</label>
                <select v-model="planForm.goal" class="form-select">
                  <option value="">请选择目标</option>
                  <option value="增肌">增肌</option>
                  <option value="减脂">减脂</option>
                  <option value="塑形">塑形</option>
                  <option value="力量">力量提升</option>
                  <option value="耐力">耐力提升</option>
                </select>
              </div>
              <div class="form-item">
                <label class="form-label">难度</label>
                <select v-model="planForm.difficulty" class="form-select">
                  <option value="">请选择难度</option>
                  <option value="beginner">新手</option>
                  <option value="intermediate">中级</option>
                  <option value="advanced">高级</option>
                </select>
              </div>
              <div class="form-item">
                <label class="form-label">训练周期（周）</label>
                <input type="number" v-model.number="planForm.durationWeeks" class="form-input" min="1" />
              </div>
              <div class="form-item">
                <label class="form-label">开始日期</label>
                <input type="date" v-model="planForm.startDate" class="form-input" />
              </div>
            </div>
          </div>

          <div v-if="currentStep === 2" class="step-2 animate-in">
            <div class="categories-section glow-card">
              <h2 class="section-title">
                <span class="title-icon">📋</span>
                选择动作
              </h2>
              <div class="category-list">
                <div v-for="category in categories" :key="category.id" class="category-item" :class="{ active: selectedCategory === category.id }" @click="selectedCategory = category.id">
                  <div class="category-icon">{{ category.icon }}</div>
                  <div class="category-name">{{ category.name }}</div>
                </div>
              </div>
            </div>
            <div class="exercises-section glow-card">
              <div class="exercises-list">
                <div v-for="exercise in filteredExercises" :key="exercise.id" class="exercise-item" :class="{ selected: isExerciseSelected(exercise.id) }" @click="toggleExercise(exercise)">
                  <div class="exercise-info">
                    <h3 class="exercise-name">{{ exercise.name }}</h3>
                    <p class="exercise-desc">{{ exercise.description }}</p>
                  </div>
                  <div class="exercise-check">
                    <svg v-if="isExerciseSelected(exercise.id)" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                      <polyline points="20 6 9 17 4 12"></polyline>
                    </svg>
                  </div>
                </div>
              </div>
            </div>
            <div class="selected-exercises-section glow-card" v-if="selectedExercises.length > 0">
              <h2 class="section-title">
                <span class="title-icon">✓</span>
                已选动作 ({{ selectedExercises.length }})
              </h2>
              <div class="selected-exercises-list">
                <div v-for="item in selectedExercises" :key="item.id" class="selected-exercise-item">
                  <div class="selected-exercise-info">
                    <h3 class="selected-exercise-name">{{ item.name }}</h3>
                    <div class="exercise-params">
                      <div class="param-item">
                        <label class="param-label">组数</label>
                        <input type="number" v-model.number="item.sets" class="param-input" min="1" />
                      </div>
                      <div class="param-item">
                        <label class="param-label">次数</label>
                        <input type="text" v-model="item.reps" class="param-input" />
                      </div>
                      <div class="param-item">
                        <label class="param-label">重量(kg)</label>
                        <input type="number" v-model.number="item.weight" class="param-input" min="0" />
                      </div>
                      <button class="remove-btn" @click="removeExercise(item)">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <line x1="18" y1="6" x2="6" y2="18"></line>
                          <line x1="6" y1="6" x2="18" y2="18"></line>
                        </svg>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div v-if="currentStep === 3" class="step-3 animate-in">
            <div class="schedule-section glow-card">
              <h2 class="section-title">
                <span class="title-icon">📅</span>
                安排训练日
              </h2>
              <p class="section-desc">选择训练日，并为每个训练日安排动作</p>
              <div class="days-grid">
                <div v-for="day in weekDays" :key="day.dayOfWeek" class="day-card" :class="{ selected: day.isSelected, 'rest-day': day.isRestDay }" @click="toggleDaySelection(day)">
                  <div class="day-header">
                    <h3 class="day-name">{{ day.name }}</h3>
                    <div class="day-toggle">
                      <div class="toggle-switch" :class="{ active: !day.isRestDay }" @click.stop="toggleRestDay(day, $event)">
                        <div class="toggle-handle"></div>
                      </div>
                      <span class="toggle-label">{{ day.isRestDay ? '休息' : '训练' }}</span>
                    </div>
                  </div>
                  <div v-if="day.isSelected && !day.isRestDay" class="rest-note">
                    <input type="text" v-model="day.restNote" class="rest-input" placeholder="休息提示（可选）" />
                  </div>
                  <div v-if="day.isSelected && !day.isRestDay" class="day-exercises">
                    <div v-for="item in day.exercises" :key="item.exerciseId" class="day-exercise-item">
                      <span class="day-exercise-name">{{ item.exerciseName }}</span>
                    </div>
                    <button class="add-exercise-btn" @click.stop="openDayExerciseModal(day)">
                      <span class="btn-icon">+</span>
                      添加动作
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div v-if="currentStep === 4" class="step-4 animate-in">
            <div class="preview-section glow-card">
              <h2 class="section-title">
                <span class="title-icon">👀</span>
                计划预览
              </h2>
              <div class="plan-info">
                <h3 class="plan-name">{{ planForm.name }}</h3>
                <div class="plan-meta">
                  <span class="plan-meta-item">
                    <span class="meta-icon">🎯</span>
                    <span class="meta-text">{{ planForm.goal }}</span>
                  </span>
                  <span class="plan-meta-item">
                    <span class="meta-icon">📊</span>
                    <span class="meta-text">{{ getDifficultyText(planForm.difficulty) }}</span>
                  </span>
                  <span class="plan-meta-item">
                    <span class="meta-icon">⏱</span>
                    <span class="meta-text">{{ planForm.durationWeeks }} 周</span>
                  </span>
                </div>
              </div>
              <div class="plan-days-preview">
                <div v-for="day in selectedDays" :key="day.dayOfWeek" class="day-preview-item">
                  <div class="day-preview-header">
                    <h4 class="day-preview-name">{{ day.name }}</h4>
                    <span v-if="day.isRestDay" class="rest-badge">休息</span>
                  </div>
                  <div v-if="day.isRestDay" class="day-preview-rest">{{ day.restNote || '好好休息' }}</div>
                  <div v-else class="day-preview-exercises">
                    <div v-for="item in day.exercises" :key="item.exerciseId" class="exercise-preview-item">
                      <span class="exercise-preview-name">{{ item.exerciseName }}</span>
                      <span class="exercise-preview-detail">{{ item.sets }}组 × {{ item.reps }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="step-navigation">
          <button v-if="currentStep > 1" class="nav-button prev" @click="prevStep">
            <span>上一步</span>
          </button>
          <button v-if="currentStep < 4" class="nav-button next glow-button" :class="{ disabled: !canGoNext }" @click="nextStep" :disabled="!canGoNext">
            <span>下一步</span>
            <span class="btn-glow"></span>
          </button>
          <button v-if="currentStep === 4" class="nav-button save glow-button" :class="{ disabled: isSaving }" @click="savePlan" :disabled="isSaving">
            <span>{{ isSaving ? '保存中...' : '保存计划' }}</span>
            <span class="btn-glow"></span>
          </button>
        </div>
      </div>
    </div>

    <div v-if="showDayExerciseModal" class="modal-overlay" @click="closeDayExerciseModal">
      <div class="modal-content glow-card animate-in" @click.stop>
        <div class="modal-header">
          <h2 class="modal-title">为 {{ currentEditingDay?.name }} 添加动作</h2>
          <button class="modal-close" @click="closeDayExerciseModal">×</button>
        </div>
        <div class="modal-body">
          <div class="modal-exercise-list">
            <div v-for="exercise in selectedExercises" :key="exercise.id" class="modal-exercise-item" :class="{ selected: isExerciseInDay(exercise.id) }" @click="toggleExerciseInDay(exercise)">
              <div class="modal-exercise-info">
                <h3 class="modal-exercise-name">{{ exercise.name }}</h3>
                <p class="modal-exercise-detail">{{ exercise.sets }}组 × {{ exercise.reps }}</p>
              </div>
              <div class="modal-exercise-check">
                <svg v-if="isExerciseInDay(exercise.id)" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                  <polyline points="20 6 9 17 4 12"></polyline>
                </svg>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="modal-button cancel" @click="closeDayExerciseModal">取消</button>
          <button class="modal-button confirm glow-button" @click="confirmDayExercises">
            <span>确认</span>
            <span class="btn-glow"></span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import api, { Category, ExerciseItem, Template } from '../../utils/api';
import toast from '../../utils/toast';

const router = useRouter();

// 登录状态
const isLoggedIn = ref(localStorage.getItem('token') !== null);

// 模板相关
const templates = ref<Template[]>([]);
const sharedPlans = ref<any[]>([]);

// 自定义计划创建
const isCreatingCustomPlan = ref(false);
const currentStep = ref(1);
const isSaving = ref(false);
const showDayExerciseModal = ref(false);
const currentEditingDay = ref<any>(null);

const planForm = ref({
  name: '',
  goal: '',
  difficulty: '',
  durationWeeks: 4,
  startDate: new Date().toISOString().split('T')[0]
});

const loadTemplates = async () => {
  try {
    const response = await api.templates.getList();
    if (response.code === 200 && response.data) {
      templates.value = response.data;
    }
  } catch (error) {
    console.error('加载模板失败:', error);
  }
};

const loadSharedPlans = async () => {
  try {
    const response = await api.plans.getList();
    if (response.code === 200 && response.data) {
      // 过滤出分享的计划
      sharedPlans.value = response.data.filter((plan: any) => plan.isShared === 1);
    }
  } catch (error) {
    console.error('加载分享计划失败:', error);
  }
};

const useTemplate = (template: Template) => {
  // 使用模板创建计划
  router.push(`/pages/template/detail?id=${template.id}`);
};

const useSharedPlan = (plan: any) => {
  // 使用分享的计划
  router.push(`/pages/home/index`);
};

const startCreateCustomPlan = () => {
  isCreatingCustomPlan.value = true;
  currentStep.value = 1;
};

const categories = ref<Category[]>([]);
const exercises = ref<ExerciseItem[]>([]);
const selectedCategory = ref<number | null>(null);
const selectedExercises = ref<any[]>([]);

const weekDays = ref([
  { dayOfWeek: 1, name: '周一', isSelected: true, isRestDay: false, restNote: '', exercises: [] },
  { dayOfWeek: 2, name: '周二', isSelected: true, isRestDay: false, restNote: '', exercises: [] },
  { dayOfWeek: 3, name: '周三', isSelected: false, isRestDay: true, restNote: '好好休息', exercises: [] },
  { dayOfWeek: 4, name: '周四', isSelected: true, isRestDay: false, restNote: '', exercises: [] },
  { dayOfWeek: 5, name: '周五', isSelected: true, isRestDay: false, restNote: '', exercises: [] },
  { dayOfWeek: 6, name: '周六', isSelected: false, isRestDay: true, restNote: '', exercises: [] },
  { dayOfWeek: 7, name: '周日', isSelected: false, isRestDay: true, restNote: '', exercises: [] }
]);

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

const loadCategories = async () => {
  try {
    const response = await api.categories.getList();
    if (response.code === 200 && response.data) {
      categories.value = response.data;
    }
  } catch (error) {
    console.error('加载分类失败:', error);
  }
};

const loadExercises = async () => {
  try {
    const response = await api.exercises.getList();
    if (response.code === 200 && response.data) {
      exercises.value = response.data;
    }
  } catch (error) {
    console.error('加载动作失败:', error);
  }
};

const filteredExercises = computed(() => {
  if (!selectedCategory.value) return exercises.value;
  return exercises.value.filter(e => e.categoryId === selectedCategory.value);
});

const isExerciseSelected = (exerciseId: number) => {
  return selectedExercises.value.some(e => e.id === exerciseId);
};

const toggleExercise = (exercise: ExerciseItem) => {
  const index = selectedExercises.value.findIndex(e => e.id === exercise.id);
  if (index !== -1) {
    selectedExercises.value.splice(index, 1);
  } else {
    selectedExercises.value.push({
      ...exercise,
      sets: exercise.defaultSets,
      reps: exercise.defaultReps || exercise.defaultDuration,
      weight: 0
    });
  }
};

const removeExercise = (exercise: any) => {
  const index = selectedExercises.value.findIndex(e => e.id === exercise.id);
  if (index !== -1) {
    selectedExercises.value.splice(index, 1);
    
    weekDays.value.forEach(day => {
      day.exercises = day.exercises.filter((e: any) => e.exerciseId !== exercise.id);
    });
  }
};

const toggleDaySelection = (day: any) => {
  day.isSelected = !day.isSelected;
};

const toggleRestDay = (day: any, event: Event) => {
  day.isRestDay = !day.isRestDay;
};

const openDayExerciseModal = (day: any) => {
  currentEditingDay.value = day;
  showDayExerciseModal.value = true;
};

const closeDayExerciseModal = () => {
  showDayExerciseModal.value = false;
  currentEditingDay.value = null;
};

const isExerciseInDay = (exerciseId: number) => {
  if (!currentEditingDay.value) return false;
  return currentEditingDay.value.exercises.some((e: any) => e.exerciseId === exerciseId);
};

const toggleExerciseInDay = (exercise: any) => {
  if (!currentEditingDay.value) return;
  
  const index = currentEditingDay.value.exercises.findIndex((e: any) => e.exerciseId === exercise.id);
  if (index !== -1) {
    currentEditingDay.value.exercises.splice(index, 1);
  } else {
    currentEditingDay.value.exercises.push({
      exerciseId: exercise.id,
      exerciseName: exercise.name,
      sets: exercise.sets,
      reps: exercise.reps,
      weight: exercise.weight,
      sortOrder: currentEditingDay.value.exercises.length + 1
    });
  }
};

const confirmDayExercises = () => {
  closeDayExerciseModal();
};

const selectedDays = computed(() => {
  return weekDays.value.filter(day => day.isSelected);
});

const getDifficultyText = (difficulty: string) => {
  const map: Record<string, string> = {
    beginner: '新手',
    intermediate: '中级',
    advanced: '高级'
  };
  return map[difficulty] || difficulty;
};

const canGoNext = computed(() => {
  if (currentStep.value === 1) {
    return planForm.value.name && planForm.value.goal && planForm.value.difficulty && planForm.value.durationWeeks > 0;
  }
  if (currentStep.value === 2) {
    return selectedExercises.value.length > 0;
  }
  if (currentStep.value === 3) {
    return selectedDays.value.some(day => !day.isRestDay && day.exercises.length > 0);
  }
  return true;
});

const prevStep = () => {
  if (currentStep.value > 1) {
    currentStep.value--;
  }
};

const nextStep = () => {
  if (currentStep.value < 4 && canGoNext.value) {
    currentStep.value++;
  }
};

const goToStep = (step: number) => {
  if (step <= currentStep.value) {
    currentStep.value = step;
  }
};

const savePlan = async () => {
  try {
    isSaving.value = true;
    
    const planData = {
      ...planForm.value,
      days: weekDays.value.filter(day => day.isSelected).map(day => ({
        dayOfWeek: day.dayOfWeek,
        isRestDay: day.isRestDay ? 1 : 0,
        restNote: day.restNote,
        exercises: day.exercises
      }))
    };
    
    const response = await api.plans.createCustom(planData);
    
    if (response.code === 200) {
      toast.success('计划创建成功！');
      router.push('/pages/home/index');
    } else {
      toast.error('计划创建失败，请重试');
    }
  } catch (error) {
    console.error('保存计划失败:', error);
    toast.error('保存计划失败，请重试');
  } finally {
    isSaving.value = false;
  }
};

const goBack = () => {
  router.push('/pages/home/index');
};

onMounted(() => {
  nextTick(() => {
    initParticles();
  });
  loadCategories();
  loadExercises();
  loadTemplates();
  if (isLoggedIn.value) {
    loadSharedPlans();
  }
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

.custom-plan-container {
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
  padding-bottom: 120.0px;
  position: relative;
  z-index: 1;
}

.steps-indicator {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  gap: 10px;
}

.step-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  opacity: 0.5;
  transition: all 0.3s ease;
}

.step-item.active {
  opacity: 1;
}

.step-item.completed {
  opacity: 1;
}

.step-number {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--bg-tertiary);
  border: 2px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
  transition: all 0.3s ease;
}

.step-item.active .step-number {
  background: var(--gradient-cyan);
  border-color: var(--neon-cyan);
  box-shadow: var(--glow-cyan);
}

.step-item.completed .step-number {
  background: rgba(0, 255, 136, 0.2);
  border-color: var(--neon-green);
  color: var(--neon-green);
}

.step-label {
  font-size: 11px;
  color: var(--text-secondary);
}

.step-item.active .step-label {
  color: var(--neon-cyan);
}

.form-section,
.categories-section,
.exercises-section,
.selected-exercises-section,
.schedule-section,
.preview-section {
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

.form-section::before,
.categories-section::before,
.exercises-section::before,
.selected-exercises-section::before,
.schedule-section::before,
.preview-section::before {
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

.form-section::after,
.categories-section::after,
.exercises-section::after,
.selected-exercises-section::after,
.schedule-section::after,
.preview-section::after {
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

.section-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 10px;
  position: relative;
}

.title-icon {
  font-size: 22px;
}

.section-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 20px;
}

.form-item {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.form-input,
.form-select {
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

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: var(--neon-cyan);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.2);
}

.category-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  gap: 10px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px;
  border-radius: 12px;
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(0, 245, 255, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.category-item:hover {
  background: rgba(0, 245, 255, 0.05);
  border-color: rgba(0, 245, 255, 0.3);
  transform: translateY(-2px);
}

.category-item.active {
  background: rgba(0, 245, 255, 0.15);
  border-color: var(--neon-cyan);
  box-shadow: 0 0 15px rgba(0, 245, 255, 0.3);
}

.category-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.category-name {
  font-size: 13px;
  color: var(--text-primary);
  text-align: center;
}

.exercises-list,
.selected-exercises-list,
.modal-exercise-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.exercise-item,
.selected-exercise-item,
.modal-exercise-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-radius: 10px;
  background: rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(0, 245, 255, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.exercise-item:hover,
.selected-exercise-item:hover,
.modal-exercise-item:hover {
  background: rgba(0, 245, 255, 0.05);
  border-color: rgba(0, 245, 255, 0.3);
  transform: translateX(4px);
}

.exercise-item.selected,
.modal-exercise-item.selected {
  background: rgba(0, 245, 255, 0.1);
  border-color: var(--neon-cyan);
}

.exercise-info,
.selected-exercise-info,
.modal-exercise-info {
  flex: 1;
}

.exercise-name,
.selected-exercise-name,
.modal-exercise-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.exercise-desc,
.modal-exercise-detail {
  font-size: 13px;
  color: var(--text-muted);
}

.exercise-check,
.modal-exercise-check {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: 2px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.exercise-item.selected .exercise-check,
.modal-exercise-item.selected .modal-exercise-check {
  background: var(--neon-cyan);
  border-color: var(--neon-cyan);
  color: var(--bg-primary);
  box-shadow: 0 0 10px var(--neon-cyan);
}

.exercise-params {
  display: flex;
  gap: 10px;
  margin-top: 10px;
  flex-wrap: wrap;
  align-items: center;
}

.param-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.param-label {
  font-size: 11px;
  color: var(--text-muted);
}

.param-input {
  width: 70px;
  padding: 8px;
  border: 1px solid var(--border-color);
  border-radius: 6px;
  font-size: 13px;
  background: var(--bg-tertiary);
  color: var(--text-primary);
  text-align: center;
}

.remove-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(255, 0, 110, 0.15);
  border: 1px solid var(--neon-pink);
  color: var(--neon-pink);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.remove-btn:hover {
  background: rgba(255, 0, 110, 0.3);
  box-shadow: 0 0 10px rgba(255, 0, 110, 0.3);
}

.days-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.day-card {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 12px;
  padding: 15px;
  border: 1px solid rgba(0, 245, 255, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.day-card:hover {
  background: rgba(0, 245, 255, 0.05);
  border-color: rgba(0, 245, 255, 0.3);
}

.day-card.selected {
  border-color: var(--neon-cyan);
  box-shadow: 0 0 15px rgba(0, 245, 255, 0.2);
}

.day-card.rest-day {
  border-color: var(--neon-pink);
}

.day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.day-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.day-toggle {
  display: flex;
  align-items: center;
  gap: 10px;
}

.toggle-switch {
  width: 50px;
  height: 28px;
  border-radius: 14px;
  background: var(--bg-tertiary);
  border: 1px solid var(--border-color);
  position: relative;
  cursor: pointer;
  transition: all 0.3s ease;
}

.toggle-switch.active {
  background: var(--gradient-cyan);
  border-color: var(--neon-cyan);
}

.toggle-handle {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: white;
  position: absolute;
  top: 2px;
  left: 2px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.toggle-switch.active .toggle-handle {
  left: 24px;
}

.toggle-label {
  font-size: 13px;
  color: var(--text-secondary);
}

.rest-input {
  width: 100%;
  padding: 10px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
  background: var(--bg-tertiary);
  color: var(--text-primary);
}

.day-exercises {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
}

.day-exercise-item {
  padding: 6px 12px;
  background: rgba(0, 245, 255, 0.1);
  border-radius: 16px;
  font-size: 13px;
  color: var(--neon-cyan);
}

.add-exercise-btn {
  padding: 8px 16px;
  background: var(--bg-tertiary);
  border: 1px dashed var(--border-color);
  border-radius: 8px;
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.add-exercise-btn:hover {
  border-color: var(--neon-cyan);
  color: var(--neon-cyan);
}

.btn-icon {
  font-size: 16px;
}

.plan-info {
  text-align: center;
  margin-bottom: 24px;
}

.plan-name {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 16px;
  background: var(--gradient-neon);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.plan-meta {
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
}

.plan-meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 20px;
  border: 1px solid rgba(0, 245, 255, 0.1);
}

.meta-icon {
  font-size: 16px;
}

.meta-text {
  font-size: 14px;
  color: var(--text-primary);
}

.plan-days-preview {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.day-preview-item {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 12px;
  padding: 15px;
  border: 1px solid rgba(0, 245, 255, 0.1);
}

.day-preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.day-preview-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.rest-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  background: rgba(255, 0, 110, 0.15);
  color: var(--neon-pink);
  border: 1px solid var(--neon-pink);
}

.day-preview-rest {
  font-size: 14px;
  color: var(--text-secondary);
}

.day-preview-exercises {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.exercise-preview-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: rgba(0, 0, 0, 0.15);
  border-radius: 8px;
}

.exercise-preview-name {
  font-size: 14px;
  color: var(--text-primary);
}

.exercise-preview-detail {
  font-size: 13px;
  color: var(--text-muted);
}

.step-navigation {
  display: flex;
  gap: 15px;
  margin-top: 20px;
}

.nav-button {
  flex: 1;
  padding: 14px;
  border: none;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
}

.nav-button.prev {
  background: var(--bg-tertiary);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.nav-button.prev:hover {
  border-color: var(--neon-cyan);
  color: var(--neon-cyan);
}

.nav-button.next.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  backdrop-filter: blur(10px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeInUp 0.3s ease;
}

.modal-content {
  background: var(--bg-card);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border-radius: 15.0px;
  width: 90%;
  max-width: 400px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 6.0px 20.0px rgba(0, 0, 0, 0.5);
  position: relative;
  animation: fadeInUp 0.4s ease;
  border: 1px solid var(--border-color);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
}

.modal-title {
  font-size: 18px;
  font-weight: bold;
  color: var(--text-primary);
}

.modal-close {
  background: var(--bg-tertiary);
  border: 1px solid var(--border-color);
  font-size: 24px;
  color: var(--text-muted);
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.modal-close:hover {
  background: rgba(255, 0, 110, 0.1);
  color: var(--neon-pink);
  border-color: var(--neon-pink);
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 15px 20px;
  border-top: 1px solid var(--border-color);
}

.modal-button {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.modal-button.cancel {
  background: var(--bg-tertiary);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.modal-button.cancel:hover {
  background: rgba(255, 0, 110, 0.1);
  color: var(--neon-pink);
  border-color: var(--neon-pink);
}

.glow-button {
  position: relative;
  overflow: hidden;
  background: var(--gradient-cyan);
  color: white;
  border: none;
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.3);
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.glow-button:hover:not(.disabled) {
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 0 30px rgba(0, 245, 255, 0.5), 0 0 60px rgba(0, 245, 255, 0.3);
}

.glow-button.disabled {
  background: var(--bg-tertiary);
  color: var(--text-muted);
  cursor: not-allowed;
  box-shadow: none;
}

.btn-glow {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s;
}

.glow-button:hover:not(.disabled) .btn-glow {
  left: 100%;
}

@media (max-width: 768px) {
  .content {
    padding: 10.0px 7.5px;
    padding-bottom: 100px;
  }
  
  .form-section,
  .categories-section,
  .exercises-section,
  .selected-exercises-section,
  .schedule-section,
  .preview-section {
    padding: 15.0px;
  }
  
  .title {
    font-size: 17.0px;
  }
  
  .section-title {
    font-size: 16px;
  }
  
  .category-list {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .plan-meta {
    gap: 10px;
  }
}
</style>
