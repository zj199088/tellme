<template>
  <div class="ai-container">
    <div class="particle-bg" id="particleBg"></div>
    <div class="header">
      <h1 class="title neon-glow">AI健身助手</h1>
      <div class="header-bg"></div>
    </div>
    <div class="chat-container">
      <div class="messages" ref="messagesContainer">
        <div v-for="(message, index) in messages" :key="index" :class="['message', message.role, 'animate-in']" :style="{ animationDelay: `${index * 0.1}s` }">
          <div class="message-content">
            {{ message.content }}
          </div>
          <button 
            v-if="message.role === 'assistant' && !message.saved" 
            @click="savePlan(message)" 
            class="save-button"
          >
            保存计划
          </button>
        </div>
        <div v-if="loading" class="message assistant animate-in">
          <div class="message-content">
            <div class="loading">
              <span class="loading-dot"></span>
              <span class="loading-dot"></span>
              <span class="loading-dot"></span>
              <span>生成中...</span>
            </div>
          </div>
        </div>
      </div>
      <div class="input-area">
        <input 
          v-model="inputMessage" 
          type="text" 
          placeholder="请输入您的健身需求..."
          @keyup.enter="sendMessage"
          class="input"
        />
        <button @click="sendMessage" class="send-button">
          <span class="send-icon">→</span>
        </button>
      </div>
    </div>
    <!-- 保存计划弹窗 -->
    <div v-if="showSaveModal" class="modal-overlay">
      <div class="modal-content animate-in">
        <div class="modal-header">
          <h2 class="modal-title">保存健身计划</h2>
          <button @click="showSaveModal = false" class="modal-close">×</button>
        </div>
        <div class="modal-body">
          <div class="form-item">
            <label class="form-label">计划名称</label>
            <input v-model="planForm.name" type="text" placeholder="请输入计划名称" class="form-input" />
          </div>
          <div class="form-item">
            <label class="form-label">计划类型</label>
            <input v-model="planForm.type" type="text" placeholder="如：力量训练、有氧运动等" class="form-input" />
          </div>
          <div class="form-item">
            <label class="form-label">目标</label>
            <input v-model="planForm.goal" type="text" placeholder="如：增肌、减脂、塑形等" class="form-input" />
          </div>
          <div class="form-item">
            <label class="form-label">难度</label>
            <input v-model="planForm.difficulty" type="text" placeholder="如：初级、中级、高级" class="form-input" />
          </div>
          <div class="form-item">
            <label class="form-label">持续周数</label>
            <input v-model.number="planForm.duration_weeks" type="number" placeholder="请输入周数" class="form-input" />
          </div>
          <div class="form-item">
            <label class="form-label">开始日期</label>
            <input v-model="planForm.start_date" type="date" class="form-input" />
          </div>
        </div>
        <div class="modal-footer">
          <button @click="showSaveModal = false" class="modal-button cancel">取消</button>
          <button @click="confirmSavePlan" class="modal-button confirm">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, onMounted } from 'vue';
import axios from 'axios';

const initParticles = () => {
  const particleBg = document.getElementById('particleBg');
  if (!particleBg) return;
  
  for (let i = 0; i < 12; i++) {
    const particle = document.createElement('div');
    particle.className = 'particle';
    const size = Math.random() * 20 + 6;
    particle.style.width = `${size}px`;
    particle.style.height = `${size}px`;
    particle.style.left = `${Math.random() * 100}%`;
    particle.style.top = `${Math.random() * 100}%`;
    particle.style.animationDelay = `${Math.random() * 15}s`;
    particle.style.animationDuration = `${Math.random() * 10 + 10}s`;
    
    const colors = ['#FF6B35', '#4ECDC4', '#FFD166', '#EF476F'];
    const color = colors[Math.floor(Math.random() * colors.length)];
    particle.style.background = `radial-gradient(circle, ${color}40 0%, ${color}00 70%)`;
    
    particleBg.appendChild(particle);
  }
};

onMounted(() => {
  nextTick(() => {
    initParticles();
  });
});

interface Message {
  role: 'user' | 'assistant';
  content: string;
  saved?: boolean;
}

const messages = ref<Message[]>([
  {
    role: 'assistant',
    content: '您好！我是您的AI健身助手。请告诉我您的健身目标、当前身体状况和可用时间，我将为您生成个性化的健身计划。'
  }
]);
const inputMessage = ref('');
const loading = ref(false);
const messagesContainer = ref<HTMLElement | null>(null);
const showSaveModal = ref(false);
const currentMessage = ref<Message | null>(null);

const planForm = ref({
  name: '',
  type: '',
  goal: '',
  difficulty: '',
  duration_weeks: 4,
  start_date: new Date().toISOString().split('T')[0]
});

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    }
  });
};

watch(messages, () => {
  scrollToBottom();
}, { deep: true });

const sendMessage = async () => {
  if (!inputMessage.value.trim()) return;

  const userMessage = inputMessage.value.trim();
  messages.value.push({
    role: 'user',
    content: userMessage
  });
  inputMessage.value = '';
  loading.value = true;

  try {
    const response = await axios.post('https://ark.cn-beijing.volces.com/api/v3/chat/completions', {
      model: 'ep-20260415164055-kx78l',
      messages: [
        {
          role: 'system',
          content: '你是一个专业的健身教练，根据用户的需求生成个性化的健身计划。计划应该包括：目标、每周训练安排、每个训练日的具体内容（包括动作、组数、次数、重量建议）、饮食建议和注意事项。请使用中文回复。'
        },
        ...messages.value.map(msg => ({
          role: msg.role,
          content: msg.content
        }))
      ],
      temperature: 0.7
    }, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer 19c64277-965e-476f-8539-a4946c3f0803'
      }
    });

    const aiResponse = response.data.choices[0].message.content;
    messages.value.push({
      role: 'assistant',
      content: aiResponse
    });
  } catch (error) {
    console.error('API调用失败:', error);
    messages.value.push({
      role: 'assistant',
      content: '抱歉，生成计划失败，请稍后重试。'
    });
  } finally {
    loading.value = false;
  }
};

const savePlan = (message: Message) => {
  currentMessage.value = message;
  showSaveModal.value = true;
};

const confirmSavePlan = async () => {
  if (!currentMessage.value) return;

  try {
    const response = await axios.post('http://localhost:8080/api/templates/create-plan', {
      ...planForm.value,
      template_id: 1 // 这里使用默认模板ID，实际应用中可能需要根据情况调整
    }, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token') // 假设token存储在localStorage中
      }
    });

    if (response.data.success) {
      // 标记消息为已保存
      const index = messages.value.findIndex(msg => msg === currentMessage.value);
      if (index !== -1) {
        messages.value[index].saved = true;
      }
      showSaveModal.value = false;
      // 重置表单
      planForm.value = {
        name: '',
        type: '',
        goal: '',
        difficulty: '',
        duration_weeks: 4,
        start_date: new Date().toISOString().split('T')[0]
      };
      // 显示保存成功消息
      messages.value.push({
        role: 'assistant',
        content: '计划保存成功！您可以在首页查看您的健身计划。'
      });
    } else {
      messages.value.push({
        role: 'assistant',
        content: '保存计划失败，请稍后重试。'
      });
    }
  } catch (error) {
    console.error('保存计划失败:', error);
    messages.value.push({
      role: 'assistant',
      content: '保存计划失败，请稍后重试。'
    });
  }
};
</script>

<style scoped>
/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-10rpx) rotate(1deg);
  }
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-5rpx);
  }
  60% {
    transform: translateY(-3rpx);
  }
}

@keyframes dotPulse {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

.animate-in {
  animation: fadeInUp 0.5s ease forwards;
}

/* 主容器 */
.ai-container {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  position: relative;
  overflow: hidden;
  padding-bottom: 140rpx;
}

/* 头部设计 */
.header {
  position: relative;
  background: linear-gradient(135deg, #FF6B35 0%, #FF8E53 100%);
  color: white;
  padding: 40rpx 20rpx;
  text-align: center;
  border-bottom-left-radius: 30rpx;
  border-bottom-right-radius: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(255, 107, 53, 0.3);
  overflow: hidden;
}

.header-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: radial-gradient(circle at 20% 80%, rgba(255, 255, 255, 0.2) 0%, transparent 50%),
                    radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
  animation: float 6s ease-in-out infinite;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  position: relative;
  z-index: 1;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
}

/* 聊天容器 */
.chat-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 220rpx);
  padding: 30rpx 20rpx;
}

/* 消息区域 */
.messages {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 25rpx;
  padding: 25rpx;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24rpx;
  box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.08);
  position: relative;
}

.messages::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4rpx;
  background: linear-gradient(90deg, #FF6B35, #FF8E53);
  border-top-left-radius: 24rpx;
  border-top-right-radius: 24rpx;
}

/* 消息样式 */
.message {
  margin-bottom: 25rpx;
  max-width: 85%;
  display: flex;
  flex-direction: column;
  position: relative;
}

.message.user {
  align-self: flex-end;
  margin-left: auto;
  animation: bounce 0.6s ease;
}

.message.assistant {
  align-self: flex-start;
  margin-right: auto;
  animation: bounce 0.6s ease;
}

.message-content {
  padding: 20rpx 25rpx;
  border-radius: 20rpx;
  line-height: 1.5;
  font-size: 24rpx;
  margin-bottom: 12rpx;
  position: relative;
  word-wrap: break-word;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.message.user .message-content {
  background: linear-gradient(135deg, #E3F2FD, #BBDEFB);
  color: #1976D2;
  border-bottom-right-radius: 6rpx;
}

.message.assistant .message-content {
  background: linear-gradient(135deg, #F5F5F5, #E0E0E0);
  color: #333;
  border-bottom-left-radius: 6rpx;
}

/* 加载状态 */
.loading {
  display: flex;
  align-items: center;
  gap: 10rpx;
  color: #999;
  font-style: italic;
}

.loading-dot {
  width: 8rpx;
  height: 8rpx;
  background: #FF6B35;
  border-radius: 50%;
  animation: dotPulse 1.4s infinite ease-in-out both;
}

.loading-dot:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dot:nth-child(2) {
  animation-delay: -0.16s;
}

/* 保存按钮 */
.save-button {
  align-self: flex-start;
  padding: 10rpx 20rpx;
  background: linear-gradient(135deg, #4CAF50, #45B7AA);
  color: white;
  border: none;
  border-radius: 12rpx;
  font-size: 20rpx;
  font-weight: 600;
  cursor: pointer;
  margin-top: 5rpx;
  transition: all 0.3s ease;
  box-shadow: 0 2rpx 8rpx rgba(76, 175, 80, 0.3);
}

.save-button:hover {
  transform: translateY(-2rpx);
  box-shadow: 0 4rpx 12rpx rgba(76, 175, 80, 0.4);
  animation: pulse 0.6s ease-in-out;
}

/* 输入区域 */
.input-area {
  display: flex;
  gap: 15rpx;
  padding: 15rpx;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24rpx;
  box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.input-area:focus-within {
  box-shadow: 0 12rpx 40rpx rgba(0, 0, 0, 0.12);
  transform: translateY(-2rpx);
}

.input {
  flex: 1;
  padding: 20rpx;
  border: 1rpx solid #E0E0E0;
  border-radius: 16rpx;
  font-size: 24rpx;
  background: white;
  transition: all 0.3s ease;
}

.input:focus {
  outline: none;
  border-color: #FF6B35;
  box-shadow: 0 0 0 3rpx rgba(255, 107, 53, 0.1);
}

/* 发送按钮 */
.send-button {
  width: 60rpx;
  height: 60rpx;
  background: linear-gradient(135deg, #FF6B35, #FF8E53);
  color: white;
  border: none;
  border-radius: 50%;
  font-size: 28rpx;
  font-weight: bold;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  box-shadow: 0 4rpx 12rpx rgba(255, 107, 53, 0.3);
}

.send-button:hover {
  transform: scale(1.1) rotate(10deg);
  box-shadow: 0 6rpx 16rpx rgba(255, 107, 53, 0.4);
  animation: pulse 0.6s ease-in-out;
}

.send-icon {
  transition: transform 0.3s ease;
}

.send-button:hover .send-icon {
  transform: translateX(3rpx);
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(5rpx);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeInUp 0.3s ease;
}

.modal-content {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24rpx;
  width: 90%;
  max-width: 500rpx;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 12rpx 40rpx rgba(0, 0, 0, 0.15);
  position: relative;
  animation: fadeInUp 0.4s ease;
}

.modal-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4rpx;
  background: linear-gradient(90deg, #FF6B35, #FF8E53);
  border-top-left-radius: 24rpx;
  border-top-right-radius: 24rpx;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 25rpx;
  border-bottom: 1rpx solid #E0E0E0;
}

.modal-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.modal-close {
  background: none;
  border: none;
  font-size: 40rpx;
  color: #999;
  cursor: pointer;
  padding: 0;
  width: 45rpx;
  height: 45rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.modal-close:hover {
  background: rgba(0, 0, 0, 0.05);
  color: #333;
  transform: rotate(90deg);
}

.modal-body {
  padding: 30rpx 25rpx;
}

.form-item {
  margin-bottom: 25rpx;
}

.form-label {
  display: block;
  font-size: 24rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 12rpx;
  transition: color 0.3s ease;
}

.form-input {
  width: 100%;
  padding: 20rpx;
  border: 1rpx solid #E0E0E0;
  border-radius: 12rpx;
  font-size: 24rpx;
  box-sizing: border-box;
  transition: all 0.3s ease;
  background: white;
}

.form-input:focus {
  outline: none;
  border-color: #FF6B35;
  box-shadow: 0 0 0 3rpx rgba(255, 107, 53, 0.1);
}

.modal-footer {
  display: flex;
  justify-content: space-between;
  padding: 25rpx;
  border-top: 1rpx solid #E0E0E0;
  gap: 15rpx;
}

.modal-button {
  flex: 1;
  padding: 20rpx;
  border: none;
  border-radius: 12rpx;
  font-size: 24rpx;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.modal-button.cancel {
  background: linear-gradient(135deg, #F5F5F5, #E0E0E0);
  color: #333;
}

.modal-button.confirm {
  background: linear-gradient(135deg, #FF6B35, #FF8E53);
  color: white;
}

.modal-button:hover {
  transform: translateY(-2rpx);
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.15);
  animation: pulse 0.6s ease-in-out;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-container {
    padding: 20rpx 15rpx;
  }
  
  .messages {
    padding: 20rpx;
  }
  
  .message-content {
    padding: 15rpx 20rpx;
    font-size: 22rpx;
  }
  
  .input {
    padding: 15rpx;
    font-size: 22rpx;
  }
  
  .modal-body {
    padding: 25rpx 20rpx;
  }
  
  .form-input {
    padding: 15rpx;
    font-size: 22rpx;
  }
  
  .title {
    font-size: 32rpx;
  }
}
</style>