<template>
  <div class="admin-container">
    <div class="particle-bg" id="particleBg"></div>
    <div class="glow-orb orb-1"></div>
    <div class="glow-orb orb-2"></div>
    

    
    <div class="exercises-list">
      <div v-for="(exercise, index) in exercises" :key="exercise.id" 
           class="exercise-item glass-card animate-in"
           :style="{ animationDelay: `${index * 0.1}s` }">
        <div class="exercise-info">
          <h3>{{ exercise.exerciseName }}</h3>
          <p v-if="exercise.enName" class="en-name">{{ exercise.enName }}</p>
          <div class="exercise-meta">
            <span class="meta-item">
              <span class="meta-icon">📁</span>
              分类: {{ getCategoryName(exercise.categoryId) }}
            </span>
            <span class="meta-item">
              <span class="meta-icon">⭐</span>
              难度: {{ exercise.difficultyLevel }}
            </span>
          </div>
          <p class="exercise-description">{{ exercise.description }}</p>
        </div>
        <div class="exercise-actions">
          <button class="edit-btn neon-button" @click="editExercise(exercise)">编辑</button>
          <button class="delete-btn neon-button danger" @click="deleteExercise(exercise.id)">删除</button>
        </div>
      </div>
    </div>
    
    <div v-if="showAddForm || showEditForm" class="modal">
      <div class="modal-content glass-card">
        <div class="scan-line"></div>
        <h2 class="modal-title">{{ showEditForm ? '编辑动作' : '添加动作' }}</h2>
        <form @submit.prevent="saveExercise">
          <div class="form-group">
            <label for="exerciseName">动作名称</label>
            <input type="text" id="exerciseName" v-model="form.exerciseName" required class="form-input">
          </div>
          <div class="form-group">
            <label for="enName">英文名称</label>
            <input type="text" id="enName" v-model="form.enName" class="form-input">
          </div>
          <div class="form-group">
            <label for="categoryId">所属分类</label>
            <select id="categoryId" v-model.number="form.categoryId" required class="form-input">
              <option value="">选择分类</option>
              <option v-for="category in categories" :key="category.id" :value="category.id">{{ category.categoryName }}</option>
            </select>
          </div>
          <div class="form-group">
            <label for="difficultyLevel">难度等级</label>
            <select id="difficultyLevel" v-model="form.difficultyLevel" required class="form-input">
              <option value="beginner">初级</option>
              <option value="intermediate">中级</option>
              <option value="advanced">高级</option>
            </select>
          </div>
          <div class="form-group">
            <label for="targetMuscle">目标肌肉群</label>
            <input type="text" id="targetMuscle" v-model="form.targetMuscle" class="form-input">
          </div>
          <div class="form-group">
            <label for="equipmentNeeded">所需器材</label>
            <input type="text" id="equipmentNeeded" v-model="form.equipmentNeeded" class="form-input">
          </div>
          <div class="form-group">
            <label for="description">动作描述</label>
            <textarea id="description" v-model="form.description" class="form-input"></textarea>
          </div>
          <div class="form-group">
            <label for="videoUrl">教学视频链接</label>
            <input type="text" id="videoUrl" v-model="form.videoUrl" class="form-input">
          </div>
          <div class="form-group">
            <label for="imageUrl">示意图链接</label>
            <input type="text" id="imageUrl" v-model="form.imageUrl" class="form-input">
          </div>
          <div class="form-group">
            <label for="caloriesPerHour">每小时消耗卡路里</label>
            <input type="number" id="caloriesPerHour" v-model.number="form.caloriesPerHour" class="form-input">
          </div>
          <div class="form-group checkbox-group">
            <label>
              <input type="checkbox" v-model="form.isPopular"> 热门
            </label>
          </div>
          <div class="form-group checkbox-group">
            <label>
              <input type="checkbox" v-model="form.isBodyweight"> 自重训练
            </label>
          </div>
          <div class="form-actions">
            <button type="button" class="cancel-btn neon-button" @click="closeModal">取消</button>
            <button type="submit" class="save-btn neon-button primary">保存</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import api from '@/utils/api';

const exercises = ref([]);
const categories = ref([]);
const showAddForm = ref(false);
const showEditForm = ref(false);
const form = ref({
  id: null,
  exerciseName: '',
  enName: '',
  categoryId: '',
  difficultyLevel: 'beginner',
  targetMuscle: '',
  equipmentNeeded: '',
  description: '',
  videoUrl: '',
  imageUrl: '',
  caloriesPerHour: 0,
  isPopular: false,
  isBodyweight: false
});

const initParticles = () => {
  const particleBg = document.getElementById('particleBg');
  if (!particleBg) return;

  for (let i = 0; i < 25; i++) {
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

const getExercises = async () => {
  try {
    exercises.value = await api.admin.exercises.getList();
  } catch (err) {
    console.error('获取动作失败', err);
  }
};

const getCategories = async () => {
  try {
    categories.value = await api.admin.categories.getList();
  } catch (err) {
    console.error('获取分类失败', err);
  }
};

const saveExercise = async () => {
  try {
    form.value.isPopular = form.value.isPopular ? 1 : 0;
    form.value.isBodyweight = form.value.isBodyweight ? 1 : 0;
    
    if (form.value.id) {
      await api.admin.exercises.update(form.value.id, form.value);
    } else {
      await api.admin.exercises.create(form.value);
    }
    await getExercises();
    closeModal();
  } catch (err) {
    console.error('保存动作失败', err);
  }
};

const editExercise = (exercise) => {
  form.value = {
    ...exercise,
    isPopular: exercise.isPopular === 1,
    isBodyweight: exercise.isBodyweight === 1
  };
  showEditForm.value = true;
};

const deleteExercise = async (id) => {
  if (confirm('确定要删除这个动作吗？')) {
    try {
      await api.admin.exercises.delete(id);
      await getExercises();
    } catch (err) {
      console.error('删除动作失败', err);
    }
  }
};

const getCategoryName = (categoryId) => {
  const category = categories.value.find(cat => cat.id === categoryId);
  return category ? category.categoryName : '';
};

const closeModal = () => {
  showAddForm.value = false;
  showEditForm.value = false;
  form.value = {
    id: null,
    exerciseName: '',
    enName: '',
    categoryId: '',
    difficultyLevel: 'beginner',
    targetMuscle: '',
    equipmentNeeded: '',
    description: '',
    videoUrl: '',
    imageUrl: '',
    caloriesPerHour: 0,
    isPopular: false,
    isBodyweight: false
  };
};

onMounted(() => {
  nextTick(() => {
    initParticles();
  });
  getExercises();
  getCategories();
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

@keyframes scanLine {
  0% {
    transform: translateY(-100%);
  }
  100% {
    transform: translateY(100%);
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
  opacity: 0;
}

.admin-container {
  width: 100%;
  min-height: 100vh;
  background: var(--bg-primary);
  position: relative;
  overflow: hidden;
  padding: 30px 20px;
  padding-bottom: 100px;
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
  background: var(--neon-purple);
  top: -100px;
  right: -100px;
  animation: float 8s ease-in-out infinite;
}

.orb-2 {
  width: 300px;
  height: 300px;
  background: var(--neon-pink);
  bottom: -50px;
  left: -50px;
  animation: float 10s ease-in-out infinite reverse;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px 25px;
  position: relative;
  z-index: 10;
}

.title {
  font-size: 24px;
  font-weight: bold;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.neon-glow {
  text-shadow: 0 0 10px var(--neon-cyan), 0 0 20px var(--neon-cyan), 0 0 30px var(--neon-cyan);
  animation: pulse 2s ease-in-out infinite;
}

.add-btn {
  padding: 12px 24px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-icon {
  font-size: 20px;
}

.exercises-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(420px, 1fr));
  gap: 20px;
  position: relative;
  z-index: 10;
}

.exercise-item {
  padding: 25px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  transition: all 0.3s ease;
}

.exercise-item:hover {
  transform: translateY(-5px);
  border-color: var(--neon-cyan);
  box-shadow: 0 10px 40px rgba(0, 245, 255, 0.2), 0 0 30px rgba(0, 245, 255, 0.1);
}

.exercise-info h3 {
  margin: 0 0 5px 0;
  color: var(--text-primary);
  font-size: 18px;
  font-weight: bold;
}

.en-name {
  margin: 0 0 10px 0;
  color: var(--text-muted);
  font-size: 13px;
  font-style: italic;
}

.exercise-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 10px;
  font-size: 12px;
  color: var(--text-muted);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.meta-icon {
  font-size: 14px;
}

.exercise-description {
  font-size: 13px;
  line-height: 1.5;
  color: var(--text-secondary);
}

.exercise-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.edit-btn, .delete-btn {
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
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

.neon-button.primary {
  color: var(--neon-purple);
  border-color: var(--neon-purple);
  box-shadow: 0 0 10px rgba(139, 92, 246, 0.3);
}

.neon-button.primary:hover {
  background: var(--neon-purple);
  box-shadow: 0 0 20px var(--neon-purple), 0 0 40px var(--neon-purple);
}

.neon-button.danger {
  color: var(--neon-red);
  border-color: var(--neon-red);
  box-shadow: 0 0 10px rgba(255, 77, 79, 0.3);
}

.neon-button.danger:hover {
  background: var(--neon-red);
  box-shadow: 0 0 20px var(--neon-red), 0 0 40px var(--neon-red);
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  padding: 35px;
  width: 90%;
  max-width: 620px;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
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

.modal-title {
  margin-top: 0;
  margin-bottom: 25px;
  font-size: 22px;
  font-weight: bold;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.form-group {
  margin-bottom: 18px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: var(--text-secondary);
  font-size: 14px;
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid var(--glass-border);
  border-radius: 10px;
  font-size: 14px;
  background: var(--glass-bg);
  color: var(--text-primary);
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: var(--neon-cyan);
  box-shadow: 0 0 0 3px rgba(0, 245, 255, 0.1), 0 0 20px rgba(0, 245, 255, 0.2);
}

textarea.form-input {
  resize: vertical;
  min-height: 80px;
}

.checkbox-group label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.checkbox-group input[type="checkbox"] {
  width: 18px;
  height: 18px;
  accent-color: var(--neon-cyan);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 30px;
}

.cancel-btn, .save-btn {
  padding: 12px 24px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
}

@media (max-width: 768px) {
  .admin-container {
    padding: 20px 15px;
  }

  .header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }

  .add-btn {
    justify-content: center;
  }

  .exercises-list {
    grid-template-columns: 1fr;
  }

  .exercise-item {
    flex-direction: column;
    gap: 15px;
  }

  .exercise-actions {
    flex-direction: row;
    width: 100%;
    justify-content: flex-end;
  }

  .modal-content {
    padding: 25px 20px;
  }

  .form-actions {
    flex-direction: column;
  }

  .cancel-btn, .save-btn {
    width: 100%;
  }
}
</style>