<template>
  <div class="ai-container">
    <div class="particle-bg" id="particleBg"></div>
    <div class="glow-orbs">
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
      <div class="orb orb-3"></div>
    </div>
    
    <div class="header">
      <div class="header-content">
        <div class="header-icon">🤖</div>
        <h1 class="title neon-glow">AI健身助手</h1>
      </div>
      <div class="header-bg"></div>
      <div class="scanline"></div>
    </div>
    
    <div class="content">
      <div class="chat-container animate-in glow-card">
        <div class="messages" ref="messagesContainer">
          <div v-for="(message, index) in messages" :key="index" :class="['message', message.role, 'animate-in']" :style="{ animationDelay: `${index * 0.1}s` }">
            <div class="message-avatar">
              <span v-if="message.role === 'user'">👤</span>
              <span v-else>🤖</span>
            </div>
            <div class="message-wrapper">
              <div class="message-content">
                {{ message.content }}
              </div>
              <button 
                v-if="message.role === 'assistant' && !message.saved" 
                @click="savePlan(message)" 
                class="save-button glow-button"
              >
                <span class="btn-icon">💾</span>
                <span>保存计划</span>
                <span class="btn-glow"></span>
              </button>
            </div>
          </div>
          <div v-if="loading" class="message assistant animate-in">
            <div class="message-avatar">
              <span>🤖</span>
            </div>
            <div class="message-wrapper">
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
        </div>
        <div class="input-area">
          <input 
            v-model="inputMessage" 
            type="text" 
            placeholder="请输入您的健身需求..."
            @keyup.enter="sendMessage"
            class="input"
          />
          <button @click="sendMessage" class="send-button glow-button">
            <span class="send-icon">🚀</span>
            <span class="btn-glow"></span>
          </button>
        </div>
      </div>
    </div>
    
    <div v-if="showSaveModal" class="modal-overlay">
      <div class="modal-content animate-in glow-card">
        <div class="modal-header">
          <h2 class="modal-title">
            <span class="title-icon">💪</span>
            保存健身计划
          </h2>
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
          <button @click="confirmSavePlan" class="modal-button confirm glow-button">
            <span>保存</span>
            <span class="btn-glow"></span>
          </button>
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
      template_id: 1
    }, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      }
    });

    if (response.data.success) {
      const index = messages.value.findIndex(msg => msg === currentMessage.value);
      if (index !== -1) {
        messages.value[index].saved = true;
      }
      showSaveModal.value = false;
      planForm.value = {
        name: '',
        type: '',
        goal: '',
        difficulty: '',
        duration_weeks: 4,
        start_date: new Date().toISOString().split('T')[0]
      };
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
  }
  50% {
    transform: scale(1.05);
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

@keyframes neon-pulse {
  0%, 100% {
    text-shadow: 0 0 10px var(--neon-cyan), 0 0 20px var(--neon-cyan);
  }
  50% {
    text-shadow: 0 0 20px var(--neon-cyan), 0 0 40px var(--neon-cyan), 0 0 60px var(--neon-purple);
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

.animate-in {
  animation: fadeInUp 0.6s ease forwards;
}

.ai-container {
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
  padding-bottom: 80.0px;
  position: relative;
  z-index: 1;
}

.chat-container {
  background: var(--bg-card);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border-radius: 15.0px;
  padding: 15.0px;
  box-shadow: 0 4.0px 20.0px rgba(0, 0, 0, 0.4);
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  overflow: hidden;
  border: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  height: calc(100vh - 130.0px);
}

.chat-container::before {
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

.chat-container::after {
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

.messages {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 12.5px;
  padding: 5.0px 0;
}

.message {
  display: flex;
  gap: 7.5px;
  margin-bottom: 12.5px;
  position: relative;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 30.0px;
  height: 30.0px;
  border-radius: 50%;
  background: var(--bg-tertiary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15.0px;
  border: 2px solid var(--border-color);
  flex-shrink: 0;
}

.message.user .message-avatar {
  border-color: var(--neon-cyan);
  box-shadow: 0 0 15px rgba(0, 245, 255, 0.3);
}

.message.assistant .message-avatar {
  border-color: var(--neon-purple);
  box-shadow: 0 0 15px rgba(139, 92, 246, 0.3);
}

.message-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  max-width: 80%;
}

.message.user .message-wrapper {
  align-items: flex-end;
}

.message-content {
  padding: 10.0px 12.5px;
  border-radius: 10.0px;
  line-height: 1.6;
  font-size: 12.0px;
  position: relative;
  word-wrap: break-word;
}

.message.user .message-content {
  background: var(--gradient-cyan);
  color: white;
  border-bottom-right-radius: 3.0px;
  box-shadow: 0 0 15px rgba(0, 245, 255, 0.3);
}

.message.assistant .message-content {
  background: var(--bg-tertiary);
  color: var(--text-primary);
  border-bottom-left-radius: 3.0px;
  border: 1px solid var(--border-color);
}

.loading {
  display: flex;
  align-items: center;
  gap: 5.0px;
  color: var(--text-muted);
  font-style: italic;
}

.loading-dot {
  width: 6.0px;
  height: 6.0px;
  background: var(--neon-purple);
  border-radius: 50%;
  animation: dotPulse 1.4s infinite ease-in-out both;
}

.loading-dot:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dot:nth-child(2) {
  animation-delay: -0.16s;
}

.save-button {
  align-self: flex-start;
  margin-top: 7.5px;
  padding: 6.0px 12.0px;
  background: linear-gradient(135deg, var(--neon-green), var(--neon-green));
  color: white;
  border: none;
  border-radius: 6.0px;
  font-size: 10.0px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4.0px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 0 15px rgba(0, 255, 136, 0.3);
}

.btn-icon {
  font-size: 10.0px;
}

.input-area {
  display: flex;
  gap: 7.5px;
  padding: 7.5px;
  background: var(--bg-tertiary);
  border-radius: 12.0px;
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.input-area:focus-within {
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.3);
  border-color: var(--neon-cyan);
}

.input {
  flex: 1;
  padding: 10.0px;
  border: none;
  border-radius: 8.0px;
  font-size: 12.0px;
  background: var(--bg-primary);
  color: var(--text-primary);
  transition: all 0.3s ease;
}

.input::placeholder {
  color: var(--text-muted);
}

.input:focus {
  outline: none;
  box-shadow: 0 0 0 1.0px var(--neon-cyan);
}

.send-button {
  width: 35.0px;
  height: 35.0px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14.0px;
  position: relative;
  overflow: hidden;
}

.send-icon {
  position: relative;
  z-index: 1;
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

.glow-button:hover .btn-glow {
  left: 100%;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
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
  max-width: 250.0px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 6.0px 20.0px rgba(0, 0, 0, 0.5);
  position: relative;
  animation: fadeInUp 0.4s ease;
  border: 1px solid var(--border-color);
  overflow: hidden;
}

.modal-content::before {
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

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15.0px;
  border-bottom: 1px solid var(--border-color);
}

.modal-title {
  font-size: 15.0px;
  font-weight: bold;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 5.0px;
}

.title-icon {
  font-size: 16.0px;
}

.modal-close {
  background: var(--bg-tertiary);
  border: 1px solid var(--border-color);
  font-size: 20.0px;
  color: var(--text-muted);
  cursor: pointer;
  padding: 0;
  width: 25.0px;
  height: 25.0px;
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
  transform: rotate(90deg);
}

.modal-body {
  padding: 15.0px;
}

.form-item {
  margin-bottom: 12.5px;
}

.form-label {
  display: block;
  font-size: 12.0px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 6.0px;
}

.form-input {
  width: 100%;
  padding: 10.0px;
  border: 1px solid var(--border-color);
  border-radius: 6.0px;
  font-size: 12.0px;
  box-sizing: border-box;
  transition: all 0.3s ease;
  background: var(--bg-tertiary);
  color: var(--text-primary);
}

.form-input:focus {
  outline: none;
  border-color: var(--neon-cyan);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.3);
}

.form-input::placeholder {
  color: var(--text-muted);
}

.modal-footer {
  display: flex;
  justify-content: space-between;
  padding: 12.5px 15.0px;
  border-top: 1px solid var(--border-color);
  gap: 7.5px;
}

.modal-button {
  flex: 1;
  padding: 10.0px;
  border: none;
  border-radius: 6.0px;
  font-size: 12.0px;
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

.modal-button.confirm {
  position: relative;
  overflow: hidden;
}

@media (max-width: 768px) {
  .content {
    padding: 10.0px 7.5px;
  }
  
  .chat-container {
    padding: 12.5px;
  }
  
  .message-wrapper {
    max-width: 85%;
  }
  
  .message-content {
    padding: 7.5px 10.0px;
    font-size: 11.0px;
  }
  
  .input {
    padding: 7.5px;
    font-size: 11.0px;
  }
  
  .modal-body {
    padding: 12.5px 10.0px;
  }
  
  .form-input {
    padding: 7.5px;
    font-size: 11.0px;
  }
  
  .title {
    font-size: 17.0px;
  }
}
</style>
