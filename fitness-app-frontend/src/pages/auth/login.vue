<template>
  <div class="auth-container">
    <div class="particle-bg" id="particleBg"></div>
    <div class="glow-orb orb-1"></div>
    <div class="glow-orb orb-2"></div>
    
    <div class="auth-card glass-card animate-in">
      <div class="scan-line"></div>
      <h1 class="title neon-glow">用户登录</h1>
      <form @submit.prevent="login">
        <div class="form-group">
          <label for="username">用户名</label>
          <div class="input-wrapper">
            <input type="text" id="username" v-model="form.username" required class="form-input">
            <span class="input-glow"></span>
          </div>
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <div class="input-wrapper">
            <input type="password" id="password" v-model="form.password" required class="form-input">
            <span class="input-glow"></span>
          </div>
        </div>
        <button type="submit" class="login-btn neon-button">
          <span class="btn-text">登录</span>
          <span class="btn-glow"></span>
        </button>
        <div v-if="error" class="error-message">
          <span class="error-icon">⚠</span>
          {{ error }}
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const form = ref({
  username: '',
  password: ''
});
const error = ref('');

const initParticles = () => {
  const particleBg = document.getElementById('particleBg');
  if (!particleBg) return;

  for (let i = 0; i < 20; i++) {
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

const login = async () => {
  try {
    const response = await axios.post('/auth/login', form.value);
    if (response.data.success) {
      localStorage.setItem('token', response.data.token);
      router.push('/pages/home/index');
    } else {
      error.value = response.data.message;
    }
  } catch (err) {
    error.value = '登录失败，请检查网络连接';
  }
};

onMounted(() => {
  nextTick(() => {
    initParticles();
  });
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
}

.auth-container {
  width: 100%;
  min-height: 100vh;
  background: var(--bg-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: 20px;
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
  bottom: -50px;
  right: -50px;
  animation: float 10s ease-in-out infinite reverse;
}

.auth-card {
  width: 100%;
  max-width: 420px;
  padding: 40px 30px;
  position: relative;
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
  text-align: center;
  margin-bottom: 40px;
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

.form-group {
  margin-bottom: 25px;
}

.form-group label {
  display: block;
  margin-bottom: 10px;
  font-weight: 600;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 1px;
  font-size: 14px;
}

.input-wrapper {
  position: relative;
}

.form-input {
  width: 100%;
  padding: 14px 18px;
  border: 1px solid var(--glass-border);
  border-radius: 12px;
  font-size: 16px;
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

.input-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 12px;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.form-input:focus + .input-glow {
  opacity: 1;
  box-shadow: 0 0 30px var(--neon-cyan);
}

.login-btn {
  width: 100%;
  padding: 16px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-text {
  position: relative;
  z-index: 2;
}

.btn-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: var(--neon-cyan);
  transform: translate(-50%, -50%);
  transition: width 0.4s ease, height 0.4s ease;
}

.login-btn:hover .btn-glow {
  width: 300px;
  height: 300px;
}

.glass-card {
  background: var(--glass-bg);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--glass-border);
  border-radius: 20px;
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

.neon-button:hover {
  background: var(--neon-cyan);
  color: var(--bg-primary);
  box-shadow: 0 0 20px var(--neon-cyan), 0 0 40px var(--neon-cyan);
  transform: translateY(-2px);
}

.error-message {
  margin-top: 20px;
  padding: 12px 16px;
  background: rgba(255, 77, 79, 0.1);
  border: 1px solid var(--neon-red);
  border-radius: 10px;
  color: var(--neon-red);
  text-align: center;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  box-shadow: 0 0 15px rgba(255, 77, 79, 0.2);
}

.error-icon {
  font-size: 18px;
}

@media (max-width: 768px) {
  .auth-card {
    padding: 30px 20px;
  }

  .title {
    font-size: 24px;
  }
}
</style>