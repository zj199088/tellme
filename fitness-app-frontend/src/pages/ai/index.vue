<template>
  <div class="ai-container">
    <div class="particle-bg" id="particleBg"></div>
    <div class="glow-orbs">
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
      <div class="orb orb-3"></div>
    </div>
    

    
    <div v-if="!showChat" class="goal-setting animate-in">
      <div class="goal-card glow-card">
        <h2 class="card-title">
          <span class="title-icon">🎯</span>
          设定您的健身目标
        </h2>
        
        <div class="goal-options">
          <div 
            v-for="goal in goals" 
            :key="goal.id"
            :class="['goal-option', { selected: selectedGoal === goal.id }]"
            @click="selectedGoal = goal.id"
          >
            <div class="goal-icon">{{ goal.icon }}</div>
            <div class="goal-name">{{ goal.name }}</div>
            <div class="goal-desc">{{ goal.desc }}</div>
          </div>
        </div>
        
        <div class="form-section">
          <div class="form-item">
            <label class="form-label">训练难度</label>
            <select v-model="difficulty" class="form-input">
              <option value="beginner">初级</option>
              <option value="intermediate">中级</option>
              <option value="advanced">高级</option>
            </select>
          </div>
          
          <div class="form-item">
            <label class="form-label">每周训练天数</label>
            <div class="days-selector">
              <div 
                v-for="day in [1,2,3,4,5,6,7]" 
                :key="day"
                :class="['day-button', { selected: selectedDays.includes(day) }]"
                @click="toggleDay(day)"
              >
                {{ day }}
              </div>
            </div>
          </div>
          
          <div class="form-item">
            <label class="form-label">训练周期（周）</label>
            <input v-model.number="duration" type="number" min="1" max="12" class="form-input" />
          </div>
          
          <div class="form-item">
            <label class="form-label">其他需求（可选）</label>
            <textarea v-model="additionalNeeds" placeholder="例如：我想重点锻炼上半身，我有关节问题需要注意..." class="form-textarea"></textarea>
          </div>
        </div>
        
        <button @click="generatePlan" :disabled="loading" class="generate-button glow-button">
          <span v-if="loading" class="loading-dots">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
          </span>
          <template v-else>
            <span class="btn-icon">✨</span>
            <span>生成个性化计划</span>
          </template>
          <span class="btn-glow"></span>
        </button>
      </div>
    </div>
    
    <div v-else class="content">
      <div class="chat-container animate-in glow-card">
        <div class="messages" ref="messagesContainer">
          <div v-for="(message, index) in messages" :key="index" :class="['message', message.role, 'animate-in']" :style="{ animationDelay: `${index * 0.1}s` }">
            <div class="message-avatar">
              <span v-if="message.role === 'user'">👤</span>
              <span v-else>🤖</span>
            </div>
            <div class="message-wrapper">
              <div class="message-content">
                <div v-if="message.isPlan" class="plan-preview">
                  <div class="plan-header">
                    <h3 class="plan-title">{{ message.planData?.name }}</h3>
                    <div class="plan-meta">
                      <span class="meta-item">🎯 {{ message.planData?.goal }}</span>
                      <span class="meta-item">📊 {{ message.planData?.difficulty }}</span>
                      <span class="meta-item">📅 {{ message.planData?.duration_weeks }}周</span>
                    </div>
                  </div>
                  <div class="plan-days">
                    <div v-for="(day, dayIndex) in message.planData?.days" :key="dayIndex" class="plan-day">
                      <div class="day-title">第{{ day.day }}天 - {{ day.name }}</div>
                      <div class="day-exercises">
                        <div v-for="(exercise, exIndex) in day.exercises" :key="exIndex" class="exercise-item">
                          <span class="exercise-name">{{ exercise.name }}</span>
                          <span class="exercise-stats">{{ exercise.sets }}组 × {{ exercise.reps }}次</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div v-else>{{ message.content }}</div>
              </div>
              <button 
                v-if="message.isPlan && !message.saved" 
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
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="input-area">
          <input 
            v-model="inputMessage" 
            type="text" 
            placeholder="继续与AI助手对话..."
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
            <select v-model="planForm.goal" class="form-input">
              <option value="">请选择健身目标</option>
              <option value="增肌">增肌</option>
              <option value="减脂">减脂</option>
              <option value="塑形">塑形</option>
              <option value="增强耐力">增强耐力</option>
              <option value="提高力量">提高力量</option>
              <option value="改善体态">改善体态</option>
            </select>
          </div>
          <div class="form-item">
            <label class="form-label">难度</label>
            <select v-model="planForm.difficulty" class="form-input">
              <option value="beginner">初级</option>
              <option value="intermediate">中级</option>
              <option value="advanced">高级</option>
            </select>
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
import { useRouter } from 'vue-router';
import api from '../../utils/api';
import { mockData } from '../../utils/env';
import toast from '../../utils/toast';

const router = useRouter();

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

interface Goal {
  id: string;
  name: string;
  desc: string;
  icon: string;
}

interface Message {
  role: 'user' | 'assistant';
  content: string;
  saved?: boolean;
  isPlan?: boolean;
  planData?: any;
}

interface PlanDay {
  day: number;
  name: string;
  exercises: Array<{
    name: string;
    sets: number;
    reps: number;
  }>;
}

const showChat = ref(false);
const loading = ref(false);
const selectedGoal = ref('');
const difficulty = ref('beginner');
const selectedDays = ref<number[]>([]);
const duration = ref(4);
const additionalNeeds = ref('');
const messagesContainer = ref<HTMLElement | null>(null);
const showSaveModal = ref(false);
const currentMessage = ref<Message | null>(null);
const generatedPlanData = ref<any>(null);

const goals: Goal[] = [
  { id: 'muscle', name: '增肌', desc: '增加肌肉量，打造理想体型', icon: '💪' },
  { id: 'lose', name: '减脂', desc: '燃烧脂肪，塑造完美身材', icon: '🔥' },
  { id: 'strength', name: '力量', desc: '提升力量水平，突破极限', icon: '🏋️' },
  { id: 'endurance', name: '耐力', desc: '增强心肺功能，提升耐力', icon: '🏃' },
  { id: 'flexibility', name: '柔韧', desc: '提高身体柔韧性和协调性', icon: '🧘' }
];

const planForm = ref({
  name: '',
  type: '',
  goal: '',
  difficulty: 'beginner',
  duration_weeks: 4,
  start_date: new Date().toISOString().split('T')[0]
});

const messages = ref<Message[]>([
  {
    role: 'assistant',
    content: '您好！我是您的AI健身助手。基于您的目标和需求，我已经为您生成了个性化的健身计划。您可以保存这个计划，或者继续与我对话进行调整。'
  }
]);

const inputMessage = ref('');

const toggleDay = (day: number) => {
  const index = selectedDays.value.indexOf(day);
  if (index > -1) {
    selectedDays.value.splice(index, 1);
  } else {
    selectedDays.value.push(day);
    selectedDays.value.sort((a, b) => a - b);
  }
};

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

const generateMockPlan = (): { name: string; goal: string; difficulty: string; duration_weeks: number; days: PlanDay[] } => {
  const goalNames: Record<string, string> = {
    muscle: '增肌',
    lose: '减脂',
    strength: '力量',
    endurance: '耐力',
    flexibility: '柔韧'
  };
  
  const difficultyNames: Record<string, string> = {
    beginner: '初级',
    intermediate: '中级',
    advanced: '高级'
  };
  
  const dayNames = ['胸部训练', '背部训练', '腿部训练', '肩部训练', '手臂训练', '核心训练', '全身训练'];
  const exercises = mockData.exercises;
  
  const days: PlanDay[] = selectedDays.value.map((day, index) => ({
    day: index + 1,
    name: dayNames[index % dayNames.length],
    exercises: exercises.slice(index * 3, index * 3 + 3).map(ex => ({
      name: ex.name,
      sets: difficulty.value === 'beginner' ? 3 : difficulty.value === 'intermediate' ? 4 : 5,
      reps: difficulty.value === 'beginner' ? 12 : difficulty.value === 'intermediate' ? 10 : 8
    }))
  }));
  
  return {
    name: `${goalNames[selectedGoal.value]}${difficultyNames[difficulty.value]}计划`,
    goal: goalNames[selectedGoal.value],
    difficulty: difficultyNames[difficulty.value],
    duration_weeks: duration.value,
    days
  };
};

const generatePlan = async () => {
  if (!selectedGoal.value) {
    toast.warning('请先选择您的健身目标！');
    return;
  }
  if (selectedDays.value.length === 0) {
    toast.warning('请选择至少一天的训练日！');
    return;
  }
  
  loading.value = true;
  
  try {
    await new Promise(resolve => setTimeout(resolve, 2000));
    
    const planData = generateMockPlan();
    generatedPlanData.value = planData;
    
    planForm.value = {
      name: planData.name,
      type: '力量训练',
      goal: planData.goal,
      difficulty: difficulty.value,
      duration_weeks: duration.value,
      start_date: new Date().toISOString().split('T')[0]
    };
    
    messages.value.push({
      role: 'assistant',
      content: '',
      isPlan: true,
      planData
    });
    
    showChat.value = true;
  } catch (error) {
    console.error('生成计划失败:', error);
    toast.error('生成计划失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

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
    await new Promise(resolve => setTimeout(resolve, 1500));
    
    const responses = [
      '好的，我理解您的需求。根据您的反馈，我建议您可以调整训练强度，适当增加组数。',
      '这个想法很好！我建议您在训练前增加5-10分钟的热身运动，可以有效预防受伤。',
      '没问题！如果您有任何特定部位想要重点锻炼，请告诉我，我可以为您调整计划。',
      '根据您的目标，我建议您配合适当的饮食，蛋白质摄入对于肌肉增长很重要。',
      '很好的问题！您可以在每组之间休息60-90秒，这样可以保证训练质量。'
    ];
    
    const randomResponse = responses[Math.floor(Math.random() * responses.length)];
    
    messages.value.push({
      role: 'assistant',
      content: randomResponse
    });
  } catch (error) {
    console.error('发送消息失败:', error);
    messages.value.push({
      role: 'assistant',
      content: '抱歉，发送失败，请稍后重试。'
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
    loading.value = true;
    
    const response = await api.plans.createCustom({
      name: planForm.value.name,
      type: planForm.value.type,
      goal: planForm.value.goal,
      difficulty: planForm.value.difficulty,
      duration_weeks: planForm.value.duration_weeks,
      start_date: planForm.value.start_date,
      description: 'AI生成的个性化健身计划'
    });

    if (response.success) {
      const index = messages.value.findIndex(msg => msg === currentMessage.value);
      if (index !== -1) {
        messages.value[index].saved = true;
      }
      showSaveModal.value = false;
      
      messages.value.push({
        role: 'assistant',
        content: '计划保存成功！您可以在首页查看您的健身计划。'
      });
      
      setTimeout(() => {
        router.push('/pages/home/index');
      }, 1500);
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
  } finally {
    loading.value = false;
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

.goal-setting {
  padding: 15.0px 10.0px;
  position: relative;
  z-index: 1;
}

.goal-card {
  background: var(--bg-card);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border-radius: 15.0px;
  padding: 20.0px 15.0px;
  box-shadow: 0 4.0px 20.0px rgba(0, 0, 0, 0.4);
  border: 1px solid var(--border-color);
  position: relative;
  overflow: hidden;
}

.goal-card::before {
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

.card-title {
  font-size: 16.0px;
  font-weight: bold;
  margin-bottom: 20.0px;
  display: flex;
  align-items: center;
  gap: 7.5px;
  color: var(--text-primary);
}

.title-icon {
  font-size: 18.0px;
}

.goal-options {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10.0px;
  margin-bottom: 20.0px;
}

.goal-option {
  background: var(--bg-tertiary);
  border: 2px solid var(--border-color);
  border-radius: 10.0px;
  padding: 15.0px 10.0px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.goal-option:hover {
  border-color: var(--neon-cyan);
  box-shadow: 0 0 15px rgba(0, 245, 255, 0.3);
  transform: translateY(-2.0px);
}

.goal-option.selected {
  border-color: var(--neon-purple);
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.2), rgba(0, 245, 255, 0.1));
  box-shadow: 0 0 20px rgba(139, 92, 246, 0.4);
}

.goal-icon {
  font-size: 28.0px;
  margin-bottom: 7.5px;
}

.goal-name {
  font-size: 14.0px;
  font-weight: bold;
  color: var(--text-primary);
  margin-bottom: 4.0px;
}

.goal-desc {
  font-size: 10.0px;
  color: var(--text-muted);
}

.form-section {
  margin-top: 20.0px;
}

.form-item {
  margin-bottom: 15.0px;
}

.form-label {
  display: block;
  font-size: 12.0px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 7.5px;
}

.form-input {
  width: 100%;
  padding: 10.0px;
  border: 1px solid var(--border-color);
  border-radius: 8.0px;
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

/* 下拉框样式 */
select.form-input {
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%2300f5ff' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 10px center;
  background-size: 16px;
  padding-right: 32px;
}

select.form-input option {
  background: var(--bg-tertiary);
  color: var(--text-primary);
  padding: 10px;
}

.form-textarea {
  width: 100%;
  padding: 10.0px;
  border: 1px solid var(--border-color);
  border-radius: 8.0px;
  font-size: 12.0px;
  box-sizing: border-box;
  transition: all 0.3s ease;
  background: var(--bg-tertiary);
  color: var(--text-primary);
  min-height: 80.0px;
  resize: vertical;
}

.form-textarea:focus {
  outline: none;
  border-color: var(--neon-cyan);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.3);
}

.days-selector {
  display: flex;
  gap: 7.5px;
  flex-wrap: wrap;
}

.day-button {
  width: 36.0px;
  height: 36.0px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12.0px;
  font-weight: bold;
  background: var(--bg-tertiary);
  border: 2px solid var(--border-color);
  color: var(--text-primary);
  cursor: pointer;
  transition: all 0.3s ease;
}

.day-button:hover {
  border-color: var(--neon-cyan);
  box-shadow: 0 0 10px rgba(0, 245, 255, 0.3);
}

.day-button.selected {
  background: var(--gradient-cyan);
  border-color: var(--neon-cyan);
  color: white;
  box-shadow: 0 0 15px rgba(0, 245, 255, 0.4);
}

.generate-button {
  width: 100%;
  padding: 12.5px;
  background: var(--gradient-neon);
  color: white;
  border: none;
  border-radius: 10.0px;
  font-size: 14.0px;
  font-weight: bold;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 7.5px;
  position: relative;
  overflow: hidden;
  margin-top: 10.0px;
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.3);
}

.generate-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-dots {
  display: flex;
  gap: 5.0px;
}

.dot {
  width: 8.0px;
  height: 8.0px;
  border-radius: 50%;
  background: white;
  animation: dotPulse 1.4s infinite ease-in-out both;
}

.dot:nth-child(1) {
  animation-delay: -0.32s;
}

.dot:nth-child(2) {
  animation-delay: -0.16s;
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

.plan-preview {
  background: var(--bg-primary);
  border-radius: 8.0px;
  padding: 12.5px;
  border: 1px solid var(--border-color);
}

.plan-header {
  margin-bottom: 12.5px;
  padding-bottom: 10.0px;
  border-bottom: 1px solid var(--border-color);
}

.plan-title {
  font-size: 14.0px;
  font-weight: bold;
  color: var(--text-primary);
  margin-bottom: 7.5px;
}

.plan-meta {
  display: flex;
  gap: 10.0px;
  flex-wrap: wrap;
}

.meta-item {
  font-size: 10.0px;
  color: var(--text-muted);
  background: var(--bg-tertiary);
  padding: 4.0px 8.0px;
  border-radius: 4.0px;
}

.plan-days {
  display: flex;
  flex-direction: column;
  gap: 10.0px;
}

.plan-day {
  background: var(--bg-tertiary);
  border-radius: 6.0px;
  padding: 8.0px;
}

.day-title {
  font-size: 11.0px;
  font-weight: bold;
  color: var(--neon-cyan);
  margin-bottom: 6.0px;
}

.day-exercises {
  display: flex;
  flex-direction: column;
  gap: 4.0px;
}

.exercise-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 10.0px;
}

.exercise-name {
  color: var(--text-primary);
}

.exercise-stats {
  color: var(--text-muted);
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
  
  .goal-options {
    grid-template-columns: 1fr;
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
