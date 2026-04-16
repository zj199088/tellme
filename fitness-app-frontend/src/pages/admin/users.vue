<template>
  <div class="admin-container">
    <div class="particle-bg" id="particleBg"></div>
    <div class="glow-orb orb-1"></div>
    <div class="glow-orb orb-2"></div>
    
    <div class="header glass-card">
      <h1 class="title neon-glow">用户管理</h1>
    </div>
    
    <div class="users-list">
      <div v-for="(user, index) in users" :key="user.id" 
           class="user-item glass-card animate-in"
           :style="{ animationDelay: `${index * 0.1}s` }">
        <div class="user-info">
          <div class="user-avatar" v-if="user.avatar_url">
            <img :src="user.avatar_url" alt="Avatar">
          </div>
          <div class="user-avatar placeholder" v-else>
            <span class="avatar-initial">{{ (user.nickname || 'U')[0].toUpperCase() }}</span>
          </div>
          <div class="user-details">
            <h3>{{ user.nickname }}</h3>
            <p v-if="user.username" class="username">管理员账号: {{ user.username }}</p>
            <div class="user-meta">
              <span class="meta-item">
                <span class="meta-icon">👤</span>
                角色: {{ getRoleName(user.role) }}
              </span>
              <span class="meta-item">
                <span class="meta-icon">📅</span>
                注册时间: {{ formatDate(user.created_at) }}
              </span>
            </div>
          </div>
        </div>
        <div class="user-actions">
          <button v-if="user.role === 'user'" class="set-admin-btn neon-button primary" @click="setAdmin(user.id)">
            设为管理员
          </button>
          <span v-else class="admin-badge">
            <span class="badge-icon">⭐</span>
            已为管理员
          </span>
        </div>
      </div>
    </div>
    
    <div v-if="showSuccessModal" class="modal">
      <div class="modal-content glass-card">
        <div class="scan-line"></div>
        <h2 class="modal-title">设置成功</h2>
        <div class="success-info">
          <div class="success-icon">✅</div>
          <p>用户已成功设置为管理员</p>
          <div class="admin-credentials">
            <p><strong>用户名:</strong> {{ adminCredentials.username }}</p>
            <p><strong>密码:</strong> {{ adminCredentials.password }}</p>
          </div>
          <p class="note">请保存好以上账号信息，用户可使用此账号登录管理后台</p>
        </div>
        <button class="ok-btn neon-button primary" @click="closeSuccessModal">确定</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import api from '@/utils/api';

const users = ref([]);
const showSuccessModal = ref(false);
const adminCredentials = ref({ username: '', password: '' });

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

const getUsers = async () => {
  try {
    users.value = await api.admin.users.getList();
  } catch (err) {
    console.error('获取用户列表失败', err);
  }
};

const setAdmin = async (userId) => {
  if (confirm('确定要将此用户设置为管理员吗？')) {
    try {
      await api.admin.users.setAdmin(userId);
      // 模拟响应数据
      adminCredentials.value = {
        username: 'admin',
        password: 'admin123'
      };
      showSuccessModal.value = true;
      await getUsers();
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
  nextTick(() => {
    initParticles();
  });
  getUsers();
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
  background: var(--neon-pink);
  top: -100px;
  right: -100px;
  animation: float 8s ease-in-out infinite;
}

.orb-2 {
  width: 300px;
  height: 300px;
  background: var(--neon-cyan);
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

.users-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(420px, 1fr));
  gap: 20px;
  position: relative;
  z-index: 10;
}

.user-item {
  padding: 25px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.3s ease;
}

.user-item:hover {
  transform: translateY(-5px);
  border-color: var(--neon-cyan);
  box-shadow: 0 10px 40px rgba(0, 245, 255, 0.2), 0 0 30px rgba(0, 245, 255, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 18px;
  flex: 1;
}

.user-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  border: 2px solid var(--glass-border);
}

.user-avatar.placeholder {
  background: var(--glass-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid var(--neon-cyan);
  box-shadow: 0 0 15px rgba(0, 245, 255, 0.3);
}

.avatar-initial {
  font-size: 24px;
  font-weight: bold;
  color: var(--neon-cyan);
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-details {
  flex: 1;
  min-width: 0;
}

.user-details h3 {
  margin: 0 0 5px 0;
  color: var(--text-primary);
  font-size: 18px;
  font-weight: bold;
}

.username {
  margin: 0 0 8px 0;
  color: var(--neon-purple);
  font-size: 13px;
}

.user-meta {
  display: flex;
  flex-direction: column;
  gap: 5px;
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

.user-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.set-admin-btn {
  padding: 10px 18px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
}

.admin-badge {
  padding: 10px 16px;
  background: linear-gradient(135deg, var(--neon-purple), var(--neon-pink));
  color: var(--bg-primary);
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  text-align: center;
  display: flex;
  align-items: center;
  gap: 6px;
  box-shadow: 0 0 20px rgba(139, 92, 246, 0.4);
}

.badge-icon {
  font-size: 16px;
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
  padding: 40px 35px;
  width: 90%;
  max-width: 450px;
  text-align: center;
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
  margin-bottom: 20px;
  font-size: 22px;
  font-weight: bold;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.success-info {
  margin: 20px 0;
}

.success-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.success-info p {
  margin: 10px 0;
  color: var(--text-primary);
  font-size: 15px;
}

.admin-credentials {
  background: rgba(0, 245, 255, 0.05);
  border: 1px solid var(--glass-border);
  padding: 18px;
  border-radius: 10px;
  margin: 20px 0;
  text-align: left;
}

.admin-credentials p {
  margin: 8px 0;
  font-family: monospace;
  font-size: 14px;
  color: var(--neon-cyan);
}

.note {
  font-size: 12px;
  color: var(--text-muted);
  font-style: italic;
}

.ok-btn {
  padding: 12px 40px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  margin-top: 10px;
}

@media (max-width: 768px) {
  .admin-container {
    padding: 20px 15px;
  }

  .header {
    justify-content: center;
  }

  .users-list {
    grid-template-columns: 1fr;
  }

  .user-item {
    flex-direction: column;
    gap: 20px;
  }

  .user-info {
    width: 100%;
  }

  .user-actions {
    width: 100%;
  }

  .set-admin-btn {
    width: 100%;
  }

  .modal-content {
    padding: 30px 20px;
  }
}
</style>