<template>
  <div class="admin-container">
    <div class="header">
      <h1>运动分类管理</h1>
      <button class="add-btn" @click="showAddForm = true">添加分类</button>
    </div>
    
    <div class="categories-list">
      <div v-for="category in categories" :key="category.id" class="category-item">
        <div class="category-info">
          <h3>{{ category.categoryName }}</h3>
          <p>{{ category.description }}</p>
          <div class="category-meta">
            <span>排序: {{ category.sortOrder }}</span>
            <span v-if="category.parentId > 0">父分类: {{ getParentName(category.parentId) }}</span>
          </div>
        </div>
        <div class="category-actions">
          <button class="edit-btn" @click="editCategory(category)">编辑</button>
          <button class="delete-btn" @click="deleteCategory(category.id)">删除</button>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑分类弹窗 -->
    <div v-if="showAddForm || showEditForm" class="modal">
      <div class="modal-content">
        <h2>{{ showEditForm ? '编辑分类' : '添加分类' }}</h2>
        <form @submit.prevent="saveCategory">
          <div class="form-group">
            <label for="categoryName">分类名称</label>
            <input type="text" id="categoryName" v-model="form.categoryName" required>
          </div>
          <div class="form-group">
            <label for="description">分类描述</label>
            <textarea id="description" v-model="form.description"></textarea>
          </div>
          <div class="form-group">
            <label for="icon">图标</label>
            <input type="text" id="icon" v-model="form.icon">
          </div>
          <div class="form-group">
            <label for="sortOrder">排序</label>
            <input type="number" id="sortOrder" v-model.number="form.sortOrder">
          </div>
          <div class="form-group">
            <label for="parentId">父分类</label>
            <select id="parentId" v-model.number="form.parentId">
              <option value="0">顶级分类</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id" v-if="cat.parentId === 0">{{ cat.categoryName }}</option>
            </select>
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

const categories = ref([]);
const showAddForm = ref(false);
const showEditForm = ref(false);
const form = ref({
  id: null,
  categoryName: '',
  description: '',
  icon: '',
  sortOrder: 0,
  parentId: 0
});

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

const saveCategory = async () => {
  try {
    if (form.value.id) {
      // 编辑
      await axios.put(`/api/admin/categories/${form.value.id}`, form.value, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      });
    } else {
      // 添加
      await axios.post('/api/admin/categories', form.value, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      });
    }
    await getCategories();
    closeModal();
  } catch (err) {
    console.error('保存分类失败', err);
  }
};

const editCategory = (category) => {
  form.value = { ...category };
  showEditForm.value = true;
};

const deleteCategory = async (id) => {
  if (confirm('确定要删除这个分类吗？')) {
    try {
      await axios.delete(`/api/admin/categories/${id}`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      });
      await getCategories();
    } catch (err) {
      console.error('删除分类失败', err);
    }
  }
};

const getParentName = (parentId) => {
  const parent = categories.value.find(cat => cat.id === parentId);
  return parent ? parent.categoryName : '';
};

const closeModal = () => {
  showAddForm.value = false;
  showEditForm.value = false;
  form.value = {
    id: null,
    categoryName: '',
    description: '',
    icon: '',
    sortOrder: 0,
    parentId: 0
  };
};

onMounted(() => {
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

.categories-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.category-item {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.category-info h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.category-info p {
  margin: 0 0 15px 0;
  color: #666;
  font-size: 14px;
}

.category-meta {
  font-size: 12px;
  color: #999;
}

.category-actions {
  display: flex;
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
  max-width: 500px;
}

.modal-content h2 {
  margin-top: 0;
  color: #FF6B35;
}

.form-group {
  margin-bottom: 20px;
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