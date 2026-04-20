<template>
  <div class="auth-container">
    <div class="particle-bg" id="particleBg"></div>
    <div class="glow-orb orb-1"></div>
    <div class="glow-orb orb-2"></div>
    
    <div class="auth-card glass-card animate-in">
      <div class="scan-line"></div>
      <div class="logo-container">
        <div class="logo-icon">💪</div>
        <h1 class="title neon-glow">{{ appConfig.isMiniprogram ? '微信登录' : (isRegisterMode ? '用户注册' : '用户登录') }}</h1>
      </div>
      
      <div v-if="appConfig.isMiniprogram" class="miniprogram-login">
        <p class="login-description">使用微信授权登录健身助手</p>
        <button 
          @click="handleWechatLogin" 
          class="wechat-login-btn neon-button" 
          :disabled="isLoading"
        >
          <span v-if="isLoading" class="loading-spinner"></span>
          <span class="wechat-icon">💬</span>
          <span class="btn-text">{{ isLoading ? '登录中...' : '微信授权登录' }}</span>
          <span class="btn-glow"></span>
        </button>
        <div v-if="error" class="error-message">
          <span class="error-icon">⚠</span>
          {{ error }}
        </div>
      </div>
      
      <form v-else @submit.prevent="handleSubmit" class="login-form">
        <div class="form-group" :class="{ 'has-error': errors.username }">
          <label for="username" class="form-label">用户名</label>
          <div class="input-wrapper">
            <span class="input-icon">👤</span>
            <input 
              type="text" 
              id="username" 
              v-model="form.username" 
              required 
              class="form-input"
              placeholder="请输入用户名"
              @focus="clearError('username')"
              @keyup.enter="focusNext"
            >
            <span class="input-glow"></span>
          </div>
          <div v-if="errors.username" class="field-error">{{ errors.username }}</div>
        </div>
        <div v-if="isRegisterMode" class="form-group" :class="{ 'has-error': errors.nickname }">
          <label for="nickname" class="form-label">昵称</label>
          <div class="input-wrapper">
            <span class="input-icon">😊</span>
            <input 
              type="text" 
              id="nickname" 
              v-model="form.nickname" 
              required 
              class="form-input"
              placeholder="请输入昵称"
              @focus="clearError('nickname')"
              @keyup.enter="focusPassword"
            >
            <span class="input-glow"></span>
          </div>
          <div v-if="errors.nickname" class="field-error">{{ errors.nickname }}</div>
        </div>
        <div class="form-group" :class="{ 'has-error': errors.password }">
          <label for="password" class="form-label">密码</label>
          <div class="input-wrapper">
            <span class="input-icon">🔒</span>
            <input 
              :type="showPassword ? 'text' : 'password'" 
              id="password" 
              v-model="form.password" 
              required 
              class="form-input"
              placeholder="请输入密码"
              @focus="clearError('password')"
              @keyup.enter="handleSubmit"
            >
            <button 
              type="button" 
              class="password-toggle" 
              @click="togglePasswordVisibility"
              aria-label="切换密码可见性"
            >
              {{ showPassword ? '🙈' : '👁️' }}
            </button>
            <span class="input-glow"></span>
          </div>
          <div v-if="errors.password" class="field-error">{{ errors.password }}</div>
        </div>
        <div v-if="!isRegisterMode" class="form-options">
          <label class="remember-me">
            <input type="checkbox" v-model="form.remember">
            <span class="checkmark"></span>
            记住我
          </label>
          <a href="#" class="forgot-password">忘记密码？</a>
        </div>
        <button 
          type="submit" 
          class="login-btn neon-button" 
          :disabled="isLoading"
        >
          <span v-if="isLoading" class="loading-spinner"></span>
          <span class="btn-text">{{ isLoading ? (isRegisterMode ? '注册中...' : '登录中...') : (isRegisterMode ? '注册' : '登录') }}</span>
          <span class="btn-glow"></span>
        </button>
        <div v-if="error" class="error-message">
          <span class="error-icon">⚠</span>
          {{ error }}
        </div>
        <div class="auth-footer">
          <p>
            {{ isRegisterMode ? '已有账号？' : '还没有账号？' }}
            <a href="#" class="register-link" @click.prevent="toggleMode">
              {{ isRegisterMode ? '立即登录' : '立即注册' }}
            </a>
          </p>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, computed } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/utils/api';
import appConfig from '@/utils/config';

const router = useRouter();
const isRegisterMode = ref(false);
const form = ref({
  username: '',
  password: '',
  nickname: '',
  remember: false
});
const error = ref('');
const isLoading = ref(false);
const showPassword = ref(false);
const errors = ref({
  username: '',
  password: '',
  nickname: ''
});

const passwordInput = ref<HTMLInputElement | null>(null);

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

const validateForm = (): boolean => {
  let isValid = true;
  
  if (!form.value.username.trim()) {
    errors.value.username = '请输入用户名';
    isValid = false;
  }
  
  if (isRegisterMode.value && !form.value.nickname.trim()) {
    errors.value.nickname = '请输入昵称';
    isValid = false;
  }
  
  if (!form.value.password) {
    errors.value.password = '请输入密码';
    isValid = false;
  } else if (form.value.password.length < 6) {
    errors.value.password = '密码长度至少6位';
    isValid = false;
  }
  
  return isValid;
};

const toggleMode = () => {
  isRegisterMode.value = !isRegisterMode.value;
  error.value = '';
  errors.value = { username: '', password: '', nickname: '' };
};

const focusNext = () => {
  if (isRegisterMode.value) {
    document.getElementById('nickname')?.focus();
  } else {
    focusPassword();
  }
};

const clearError = (field: string) => {
  errors.value[field as keyof typeof errors.value] = '';
  error.value = '';
};

const focusPassword = () => {
  passwordInput.value?.focus();
};

const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value;
  nextTick(() => {
    passwordInput.value?.focus();
  });
};

const handleSubmit = async () => {
  if (!validateForm()) return;
  
  try {
    isLoading.value = true;
    error.value = '';
    
    let response;
    if (isRegisterMode.value) {
      response = await api.auth.register(form.value.username, form.value.password, form.value.nickname);
    } else {
      response = await api.auth.login(form.value.username, form.value.password);
    }
    
    if (response.success) {
      localStorage.setItem('token', response.token);
      const userInfo = {
        username: form.value.username,
        role: response.role,
        nickname: form.value.nickname || form.value.username
      };
      localStorage.setItem('userInfo', JSON.stringify(userInfo));
      
      if (!isRegisterMode.value && form.value.remember) {
        localStorage.setItem('rememberedUser', form.value.username);
      } else {
        localStorage.removeItem('rememberedUser');
      }
      router.push('/pages/custom/create');
    } else {
      error.value = response.message || (isRegisterMode.value ? '注册失败' : '登录失败，请检查用户名和密码');
    }
  } catch (err) {
    console.error(isRegisterMode.value ? '注册错误:' : '登录错误:', err);
    error.value = isRegisterMode.value ? '注册失败，请检查网络连接' : '登录失败，请检查网络连接';
  } finally {
    isLoading.value = false;
  }
};

const handleWechatLogin = async () => {
  try {
    isLoading.value = true;
    error.value = '';
    
    // 调用微信登录API获取code
    let code;
    if (typeof wx !== 'undefined' && wx.login) {
      // 微信小程序环境
      const loginResult = await new Promise((resolve, reject) => {
        wx.login({
          success: resolve,
          fail: reject
        });
      });
      code = loginResult.code;
    } else if (typeof WeixinJSBridge !== 'undefined') {
      // 微信浏览器环境
      code = await new Promise((resolve, reject) => {
        WeixinJSBridge.invoke('login', {}, (res) => {
          if (res.code) {
            resolve(res.code);
          } else {
            reject(new Error('获取微信code失败'));
          }
        });
      });
    } else {
      // 其他环境，使用模拟code（仅用于测试）
      code = 'test_code_' + Date.now();
      console.warn('非微信环境，使用模拟code');
    }
    
    if (!code) {
      throw new Error('获取微信code失败');
    }
    
    // 调用后端微信登录接口
    const response = await api.auth.wechatLogin({
      code: code
    });
    
    if (response.success) {
      localStorage.setItem('token', response.token);
      const userInfo = {
        username: response.nickname || '微信用户',
        role: response.role,
        nickname: response.nickname
      };
      localStorage.setItem('userInfo', JSON.stringify(userInfo));
      router.push('/pages/custom/create');
    } else {
      error.value = response.message || '微信登录失败，请稍后重试';
    }
  } catch (err) {
    console.error('微信登录错误:', err);
    error.value = '微信登录失败: ' + (err.message || '请检查网络连接');
  } finally {
    isLoading.value = false;
  }
};

const loadRememberedUser = () => {
  const rememberedUser = localStorage.getItem('rememberedUser');
  if (rememberedUser) {
    form.value.username = rememberedUser;
    form.value.remember = true;
  }
};

onMounted(() => {
  nextTick(() => {
    initParticles();
    loadRememberedUser();
  });
});
</script>

<style scoped>
/* 定义登录页面所需的CSS变量 */
:root {
  --bg-primary: #12121e;
  --glass-bg: rgba(20, 30, 50, 0.9);
  --glass-border: rgba(0, 245, 255, 0.4);
  --gradient-primary: linear-gradient(90deg, #00f5ff, #8b5cf6);
  --neon-red: #ff4d4f;
  --neon-green: #4caf50;
  --neon-cyan: #00f5ff;
  --neon-purple: #8b5cf6;
  --text-primary: #ffffff;
  --text-secondary: #e2e8f0;
  --text-muted: #cbd5e1;
}

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

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
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

.logo-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
}

.logo-icon {
  font-size: 48px;
  margin-bottom: 16px;
  animation: pulse 2s ease-in-out infinite;
}

.title {
  font-size: 28px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 8px;
  position: relative;
  z-index: 5;
  color: #ffffff;
  text-shadow: 0 0 15px rgba(0, 245, 255, 0.8), 0 0 30px rgba(0, 245, 255, 0.5);
  font-family: 'Arial', sans-serif;
  letter-spacing: 2px;
}

.neon-glow {
  text-shadow: 0 0 10px var(--neon-cyan), 0 0 20px var(--neon-cyan), 0 0 30px var(--neon-cyan);
  animation: pulse 2s ease-in-out infinite;
}

.miniprogram-login {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}

.login-description {
  color: var(--text-secondary);
  font-size: 14px;
  text-align: center;
  margin: 0;
}

.wechat-login-btn {
  width: 100%;
  padding: 16px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  background: var(--glass-bg);
  color: var(--neon-green);
  border: 1px solid var(--neon-green);
  box-shadow: 0 0 10px rgba(76, 175, 80, 0.3);
}

.wechat-login-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.wechat-login-btn:hover:not(:disabled) {
  background: var(--neon-green);
  color: var(--bg-primary);
  box-shadow: 0 0 20px var(--neon-green), 0 0 40px var(--neon-green);
  transform: translateY(-2px);
}

.wechat-icon {
  font-size: 20px;
}

.login-form {
  width: 100%;
}

.form-group {
  margin-bottom: 20px;
  position: relative;
}

.form-group.has-error .form-input {
  border-color: var(--neon-red);
  box-shadow: 0 0 0 3px rgba(255, 77, 79, 0.1);
}

.form-label {
  display: block;
  margin-bottom: 10px;
  font-weight: bold;
  color: #ffffff;
  text-transform: uppercase;
  letter-spacing: 1px;
  font-size: 14px;
  transition: color 0.3s ease;
  text-shadow: 0 0 10px rgba(0, 245, 255, 0.6);
}

.form-group.has-error .form-label {
  color: var(--neon-red);
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 16px;
  font-size: 18px;
  color: var(--text-muted);
  z-index: 2;
  transition: color 0.3s ease;
}

.form-input:focus + .input-glow + .input-icon,
.form-group.has-error .input-icon {
  color: var(--neon-cyan);
}

.form-input {
  width: 100%;
  padding: 16px 20px 16px 50px;
  border: 2px solid var(--glass-border);
  border-radius: 12px;
  font-size: 16px;
  background: rgba(20, 30, 50, 0.8);
  color: var(--text-primary);
  transition: all 0.3s ease;
  box-sizing: border-box;
  outline: none;
  box-shadow: 0 0 10px rgba(0, 245, 255, 0.1);
}

.form-input::placeholder {
  color: var(--text-muted);
  transition: color 0.3s ease;
}

.form-input:focus {
  outline: none;
  border-color: var(--neon-cyan);
  box-shadow: 0 0 0 3px rgba(0, 245, 255, 0.1), 0 0 20px rgba(0, 245, 255, 0.2);
}

.form-input:focus::placeholder {
  color: var(--text-secondary);
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
  z-index: 1;
}

.form-input:focus + .input-glow {
  opacity: 1;
  box-shadow: 0 0 30px var(--neon-cyan);
}

.password-toggle {
  position: absolute;
  right: 16px;
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  z-index: 2;
  transition: transform 0.2s ease, color 0.2s ease;
  padding: 4px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
  text-shadow: 0 0 5px rgba(0, 245, 255, 0.3);
}

.password-toggle:hover {
  transform: scale(1.1);
  background: rgba(0, 245, 255, 0.1);
  color: var(--neon-cyan);
}

.field-error {
  margin-top: 6px;
  font-size: 12px;
  color: var(--neon-red);
  text-align: left;
  animation: fadeInUp 0.3s ease;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  font-size: 14px;
  color: #ffffff;
  text-shadow: 0 0 10px rgba(0, 245, 255, 0.6);
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  user-select: none;
}

.remember-me input[type="checkbox"] {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0;
  width: 0;
}

.checkmark {
  height: 20px;
  width: 20px;
  background: rgba(20, 30, 50, 0.8);
  border: 2px solid var(--neon-cyan);
  border-radius: 4px;
  position: relative;
  transition: all 0.3s ease;
  box-shadow: 0 0 10px rgba(0, 245, 255, 0.3);
}

.remember-me:hover input ~ .checkmark {
  border-color: var(--neon-cyan);
  box-shadow: 0 0 15px rgba(0, 245, 255, 0.5);
  transform: scale(1.1);
}

.remember-me input:checked ~ .checkmark {
  background: var(--neon-cyan);
  border-color: var(--neon-cyan);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.6);
}

.checkmark:after {
  content: "";
  position: absolute;
  display: none;
  left: 7px;
  top: 3px;
  width: 5px;
  height: 10px;
  border: solid #12121e;
  border-width: 0 3px 3px 0;
  transform: rotate(45deg);
  font-weight: bold;
}

.remember-me input:checked ~ .checkmark:after {
  display: block;
}

.forgot-password {
  color: var(--neon-cyan);
  text-decoration: none;
  transition: all 0.3s ease;
  font-weight: 500;
}

.forgot-password:hover {
  text-decoration: underline;
  text-shadow: 0 0 10px rgba(0, 245, 255, 0.5);
}

.login-btn {
  width: 100%;
  padding: 18px;
  border-radius: 12px;
  font-size: 18px;
  font-weight: bold;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: linear-gradient(135deg, rgba(0, 245, 255, 0.2), rgba(139, 92, 246, 0.2));
  color: #ffffff;
  border: 2px solid var(--neon-cyan);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.5), 0 0 40px rgba(0, 245, 255, 0.3);
  text-shadow: 0 0 10px rgba(0, 245, 255, 0.8);
}

.login-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.login-btn:disabled:hover {
  background: var(--glass-bg);
  color: var(--neon-cyan);
  box-shadow: 0 0 10px rgba(0, 245, 255, 0.3);
}

.loading-spinner {
  width: 18px;
  height: 18px;
  border: 2px solid transparent;
  border-top: 2px solid currentColor;
  border-radius: 50%;
  animation: spin 1s linear infinite;
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
  background: rgba(25, 35, 60, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 2px solid rgba(0, 245, 255, 0.5);
  border-radius: 20px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 8px 32px 0 rgba(0, 245, 255, 0.2), 0 0 50px rgba(0, 245, 255, 0.1);
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

.error-message {
  margin-top: 16px;
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
  animation: fadeInUp 0.3s ease;
}

.error-icon {
  font-size: 18px;
}

.auth-footer {
  margin-top: 24px;
  text-align: center;
  font-size: 14px;
  color: var(--text-secondary);
}

.register-link {
  color: var(--neon-cyan);
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.register-link:hover {
  text-decoration: underline;
  text-shadow: 0 0 10px rgba(0, 245, 255, 0.5);
}

@media (max-width: 768px) {
  .auth-card {
    padding: 30px 20px;
  }

  .title {
    font-size: 24px;
  }

  .logo-icon {
    font-size: 36px;
  }

  .form-options {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .forgot-password {
    align-self: flex-end;
  }
}
</style>