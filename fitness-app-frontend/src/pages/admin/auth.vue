<template>
  <div class="auth-container">
    <h1>管理员登录</h1>
    <form @submit.prevent="login">
      <div class="form-group">
        <label for="username">用户名</label>
        <input type="text" id="username" v-model="form.username" required>
      </div>
      <div class="form-group">
        <label for="password">密码</label>
        <input type="password" id="password" v-model="form.password" required>
      </div>
      <button type="submit" class="login-btn">登录</button>
      <div v-if="error" class="error-message">{{ error }}</div>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const form = ref({
  username: '',
  password: ''
});
const error = ref('');

const login = async () => {
  try {
    const response = await axios.post('/auth/admin/login', form.value);
    if (response.data.success) {
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('role', response.data.role);
      router.push('/pages/admin/categories');
    } else {
      error.value = response.data.message;
    }
  } catch (err) {
    error.value = '登录失败，请检查网络连接';
  }
};
</script>

<style scoped>
.auth-container {
  max-width: 400px;
  margin: 100px auto;
  padding: 30px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  color: #FF6B35;
  margin-bottom: 30px;
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

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.login-btn {
  width: 100%;
  padding: 12px;
  background: #FF6B35;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.3s;
}

.login-btn:hover {
  background: #FF5200;
}

.error-message {
  margin-top: 15px;
  color: #ff4d4f;
  text-align: center;
}
</style>