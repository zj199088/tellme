<template>
  <div class="admin-container">
    <div class="header">
      <h1>用户管理</h1>
    </div>
    
    <div class="users-list">
      <div v-for="user in users" :key="user.id" class="user-item">
        <div class="user-info">
          <div class="user-avatar" v-if="user.avatar_url">
            <img :src="user.avatar_url" alt="Avatar">
          </div>
          <div class="user-details">
            <h3>{{ user.nickname }}</h3>
            <p v-if="user.username">管理员账号: {{ user.username }}</p>
            <div class="user-meta">
              <span>角色: {{ getRoleName(user.role) }}</span>
              <span>注册时间: {{ formatDate(user.created_at) }}</span>
            </div>
          </div>
        </div>
        <div class="user-actions">
          <button v-if="user.role === 'user'" class="set-admin-btn" @click="setAdmin(user.id)">设为管理员</button>
          <span v-else class="admin-badge">已为管理员</span>
        </div>
      </div>
    </div>
    
    <!-- 管理员设置成功弹窗 -->
    <div v-if="showSuccessModal" class="modal">
      <div class="modal-content">
        <h2>设置成功</h2>
        <div class="success-info">
          <p>用户已成功设置为管理员</p>
          <div class="admin-credentials">
            <p><strong>用户名:</strong> {{ adminCredentials.username }}</p>
            <p><strong>密码:</strong> {{ adminCredentials.password }}</p>
          </div>
          <p class="note">请保存好以上账号信息，用户可使用此账号登录管理后台</p>
        </div>
        <button class="ok-btn" @click="closeSuccessModal">确定</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const users = ref([]);
const showSuccessModal = ref(false);
const adminCredentials = ref({ username: '', password: '' });

const getUsers = async () => {
  try {
    const response = await axios.get('/api/admin/users', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    });
    users.value = response.data;
  } catch (err) {
    console.error('获取用户列表失败', err);
  }
};

const setAdmin = async (userId) => {
  if (confirm('确定要将此用户设置为管理员吗？')) {
    try {
      const response = await axios.post('/api/admin/users/set-admin', { user_id: userId }, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      });
      if (response.data.success) {
        adminCredentials.value = {
          username: response.data.username,
          password: response.data.password
        };
        showSuccessModal.value = true;
        await getUsers();
      } else {
        alert(response.data.message);
      }
    } catch (err) {
      console.error('设置管理员失败', err);
    }
  }
};

const getRoleName = (role) => {
  const roleMap = {
    'user': '普通用户',
    'admin': '管理员',
    'super_admin': '超级管理员'
  };
  return roleMap[role] || role;
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN');
};

const closeSuccessModal = () => {
  showSuccessModal.value = false;
  adminCredentials.value = { username: '', password: '' };
};

onMounted(() => {
  getUsers();
});
</script>

<style scoped>
.admin-container {
  max-width: 1000px;
  margin: 50px auto;
  padding: 20px;
}

.header {
  margin-bottom: 30px;
}

h1 {
  color: #FF6B35;
}

.users-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.user-item {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-details h3 {
  margin: 0 0 5px 0;
  color: #333;
}

.user-details p {
  margin: 0 0 10px 0;
  color: #666;
  font-size: 14px;
}

.user-meta {
  display: flex;
  flex-direction: column;
  gap: 5px;
  font-size: 12px;
  color: #999;
}

.user-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.set-admin-btn {
  padding: 8px 16px;
  background: #FF6B35;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
}

.admin-badge {
  padding: 5px 10px;
  background: #4ECDC4;
  color: white;
  border-radius: 4px;
  font-size: 12px;
  text-align: center;
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
  max-width: 400px;
  text-align: center;
}

.modal-content h2 {
  margin-top: 0;
  color: #FF6B35;
}

.success-info {
  margin: 20px 0;
}

.success-info p {
  margin: 10px 0;
  color: #333;
}

.admin-credentials {
  background: #f5f5f5;
  padding: 15px;
  border-radius: 4px;
  margin: 15px 0;
  text-align: left;
}

.admin-credentials p {
  margin: 8px 0;
  font-family: monospace;
}

.note {
  font-size: 12px;
  color: #666;
  font-style: italic;
}

.ok-btn {
  padding: 10px 20px;
  background: #FF6B35;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  margin-top: 20px;
}
</style>