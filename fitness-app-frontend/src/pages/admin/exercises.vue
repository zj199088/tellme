<template>
  <div class="admin-container">
    <div class="header">
      <h1>运动动作管理</h1>
      <button class="add-btn" @click="showAddForm = true">添加动作</button>
    </div>
    
    <div class="exercises-list">
      <div v-for="exercise in exercises" :key="exercise.id" class="exercise-item">
        <div class="exercise-info">
          <h3>{{ exercise.exerciseName }}</h3>
          <p v-if="exercise.enName">{{ exercise.enName }}</p>
          <div class="exercise-meta">
            <span>分类: {{ getCategoryName(exercise.categoryId) }}</span>
            <span>难度: {{ exercise.difficultyLevel }}</span>
          </div>
          <p class="exercise-description">{{ exercise.description }}</p>
        </div>
        <div class="exercise-actions">
          <button class="edit-btn" @click="editExercise(exercise)">编辑</button>
          <button class="delete-btn" @click="deleteExercise(exercise.id)">删除</button>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑动作弹窗 -->
    <div v-if="showAddForm || showEditForm" class="modal">
      <div class="modal-content">
        <h2>{{ showEditForm ? '编辑动作' : '添加动作' }}</h2>
        <form @submit.prevent="saveExercise">
          <div class="form-group">
            <label for="exerciseName">动作名称</label>
            <input type="text" id="exerciseName" v-model="form.exerciseName" required>
          </div>
          <div class="form-group">
            <label for="enName">英文名称</label>
            <input type="text" id="enName" v-model="form.enName">
          </div>
          <div class="form-group">
            <label for="categoryId">所属分类</label>
            <select id="categoryId" v-model.number="form.categoryId" required>
              <option value="">选择分类</option>
              <option v-for="category in categories" :key="category.id" :value="category.id">{{ category.categoryName }}</option>
            </select>
          </div>
          <div class="form-group">
            <label for="difficultyLevel">难度等级</label>
            <select id="difficultyLevel" v-model="form.difficultyLevel" required>
              <option value="beginner">初级</option>
              <option value="intermediate">中级</option>
              <option value="advanced">高级</option>
            </select>
          </div>
          <div class="form-group">
            <label for="targetMuscle">目标肌肉群</label>
            <input type="text" id="targetMuscle" v-model="form.targetMuscle">
          </div>
          <div class="form-group">
            <label for="equipmentNeeded">所需器材</label>
            <input type="text" id="equipmentNeeded" v-model="form.equipmentNeeded">
          </div>
          <div class="form-group">
            <label for="description">动作描述</label>
            <textarea id="description" v-model="form.description"></textarea>
          </div>
          <div class="form-group">
            <label for="videoUrl">教学视频链接</label>
            <input type="text" id="videoUrl" v-model="form.videoUrl">
          </div>
          <div class="form-group">
            <label for="imageUrl">示意图链接</label>
            <input type="text" id="imageUrl" v-model="form.imageUrl">
          </div>
          <div class="form-group">
            <label for="caloriesPerHour">每小时消耗卡路里</label>
            <input type="number" id="caloriesPerHour" v-model.number="form.caloriesPerHour">
          </div>
          <div class="form-group">
            <label>
              <input type="checkbox" v-model="form.isPopular"> 热门
            </label>
          </div>
          <div class="form-group">
            <label>
              <input type="checkbox" v-model="form.isBodyweight"> 自重训练
            </label>
          </div>
          <div class="form-actions">
            <button type="button" class="cancel-btn" @click="closeModal">取消</button>
            <button type="submit" class="save-btn">保存</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

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
  isPopular: 0,
  isBodyweight: 0
});

const getExercises = async () => {
  try {
    const response = await axios.get('/api/admin/exercises', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    });
    exercises.value = response.data;
  } catch (err) {
    console.error('获取动作失败', err);
  }
};

const getCategories = async () => {
  try {
    const response = await axios.get('/api/admin/categories', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    });
    categories.value = response.data;
  } catch (err) {
    console.error('获取分类失败', err);
  }
};

const saveExercise = async () => {
  try {
    // 转换布尔值为数字
    form.value.isPopular = form.value.isPopular ? 1 : 0;
    form.value.isBodyweight = form.value.isBodyweight ? 1 : 0;
    
    if (form.value.id) {
      // 编辑
      await axios.put(`/api/admin/exercises/${form.value.id}`, form.value, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      });
    } else {
      // 添加
      await axios.post('/api/admin/exercises', form.value, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      });
    }
    await getExercises();
    closeModal();
  } catch (err) {
    console.error('保存动作失败', err);
  }
};

const editExercise = (exercise) => {
  // 转换数字为布尔值
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
      await axios.delete(`/api/admin/exercises/${id}`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      });
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
    isPopular: 0,
    isBodyweight: 0
  };
};

onMounted(() => {
  getExercises();
  getCategories();
});
</script>

<style scoped>
.admin-container {
  max-width: 1000px;
  margin: 50px auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

h1 {
  color: #FF6B35;
}

.add-btn {
  padding: 10px 20px;
  background: #FF6B35;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
}

.exercises-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.exercise-item {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.exercise-info h3 {
  margin: 0 0 5px 0;
  color: #333;
}

.exercise-info p {
  margin: 0 0 10px 0;
  color: #666;
  font-size: 14px;
}

.exercise-meta {
  display: flex;
  gap: 15px;
  margin-bottom: 10px;
  font-size: 12px;
  color: #999;
}

.exercise-description {
  font-size: 13px;
  line-height: 1.4;
  color: #555;
}

.exercise-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.edit-btn, .delete-btn {
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.edit-btn {
  background: #4ECDC4;
  color: white;
}

.delete-btn {
  background: #ff4d4f;
  color: white;
}

/* 弹窗样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-content h2 {
  margin-top: 0;
  color: #FF6B35;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

input, textarea, select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

textarea {
  resize: vertical;
  min-height: 80px;
}

.form-group input[type="checkbox"] {
  width: auto;
  margin-right: 8px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
}

.cancel-btn, .save-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.cancel-btn {
  background: #f0f0f0;
  color: #333;
}

.save-btn {
  background: #FF6B35;
  color: white;
}
</style>