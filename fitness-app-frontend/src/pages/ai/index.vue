<template>
  <view class="ai-container">
    <view class="header">
      <text class="title">AI健身助手</text>
    </view>
    <view class="chat-container">
      <view class="messages" ref="messagesContainer">
        <view v-for="(message, index) in messages" :key="index" :class="['message', message.role]">
          <view class="message-content">
            {{ message.content }}
          </view>
          <button 
            v-if="message.role === 'assistant' && !message.saved" 
            @click="savePlan(message)" 
            class="save-button"
          >
            保存计划
          </button>
        </view>
        <view v-if="loading" class="message assistant">
          <view class="message-content">
            <text class="loading">生成中...</text>
          </view>
        </view>
      </view>
      <view class="input-area">
        <input 
          v-model="inputMessage" 
          type="text" 
          placeholder="请输入您的健身需求..."
          @keyup.enter="sendMessage"
          class="input"
        />
        <button @click="sendMessage" class="send-button">发送</button>
      </view>
    </view>
    <!-- 保存计划弹窗 -->
    <view v-if="showSaveModal" class="modal-overlay">
      <view class="modal-content">
        <view class="modal-header">
          <text class="modal-title">保存健身计划</text>
          <button @click="showSaveModal = false" class="modal-close">×</button>
        </view>
        <view class="modal-body">
          <view class="form-item">
            <text class="form-label">计划名称</text>
            <input v-model="planForm.name" type="text" placeholder="请输入计划名称" class="form-input" />
          </view>
          <view class="form-item">
            <text class="form-label">计划类型</text>
            <input v-model="planForm.type" type="text" placeholder="如：力量训练、有氧运动等" class="form-input" />
          </view>
          <view class="form-item">
            <text class="form-label">目标</text>
            <input v-model="planForm.goal" type="text" placeholder="如：增肌、减脂、塑形等" class="form-input" />
          </view>
          <view class="form-item">
            <text class="form-label">难度</text>
            <input v-model="planForm.difficulty" type="text" placeholder="如：初级、中级、高级" class="form-input" />
          </view>
          <view class="form-item">
            <text class="form-label">持续周数</text>
            <input v-model.number="planForm.duration_weeks" type="number" placeholder="请输入周数" class="form-input" />
          </view>
          <view class="form-item">
            <text class="form-label">开始日期</text>
            <input v-model="planForm.start_date" type="date" class="form-input" />
          </view>
        </view>
        <view class="modal-footer">
          <button @click="showSaveModal = false" class="modal-button cancel">取消</button>
          <button @click="confirmSavePlan" class="modal-button confirm">保存</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue';
import axios from 'axios';

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
.ai-container {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 120rpx;
}

.header {
  background-color: #FF6B35;
  color: white;
  padding: 20rpx;
  text-align: center;
}

.title {
  font-size: 32rpx;
  font-weight: bold;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 200rpx);
  padding: 20rpx;
}

.messages {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 20rpx;
  padding: 20rpx;
  background-color: white;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
}

.message {
  margin-bottom: 20rpx;
  max-width: 80%;
  display: flex;
  flex-direction: column;
}

.message.user {
  align-self: flex-end;
  margin-left: auto;
}

.message.assistant {
  align-self: flex-start;
  margin-right: auto;
}

.message-content {
  padding: 15rpx 20rpx;
  border-radius: 16rpx;
  line-height: 1.5;
  font-size: 24rpx;
  margin-bottom: 10rpx;
}

.message.user .message-content {
  background-color: #E3F2FD;
  color: #1976D2;
  border-bottom-right-radius: 4rpx;
}

.message.assistant .message-content {
  background-color: #F5F5F5;
  color: #333;
  border-bottom-left-radius: 4rpx;
}

.loading {
  color: #999;
  font-style: italic;
}

.save-button {
  align-self: flex-start;
  padding: 8rpx 16rpx;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 8rpx;
  font-size: 20rpx;
  cursor: pointer;
  margin-top: 5rpx;
}

.save-button:active {
  opacity: 0.8;
}

.input-area {
  display: flex;
  gap: 10rpx;
  padding: 10rpx;
  background-color: white;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
}

.input {
  flex: 1;
  padding: 15rpx;
  border: 1rpx solid #E0E0E0;
  border-radius: 12rpx;
  font-size: 24rpx;
}

.send-button {
  padding: 0 30rpx;
  background-color: #FF6B35;
  color: white;
  border: none;
  border-radius: 12rpx;
  font-size: 24rpx;
  font-weight: bold;
  cursor: pointer;
}

.send-button:active {
  opacity: 0.8;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 16rpx;
  width: 90%;
  max-width: 500rpx;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.15);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  border-bottom: 1rpx solid #E0E0E0;
}

.modal-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.modal-close {
  background: none;
  border: none;
  font-size: 36rpx;
  color: #999;
  cursor: pointer;
  padding: 0;
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-body {
  padding: 20rpx;
}

.form-item {
  margin-bottom: 20rpx;
}

.form-label {
  display: block;
  font-size: 24rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}

.form-input {
  width: 100%;
  padding: 15rpx;
  border: 1rpx solid #E0E0E0;
  border-radius: 8rpx;
  font-size: 24rpx;
  box-sizing: border-box;
}

.modal-footer {
  display: flex;
  justify-content: space-between;
  padding: 20rpx;
  border-top: 1rpx solid #E0E0E0;
  gap: 10rpx;
}

.modal-button {
  flex: 1;
  padding: 15rpx;
  border: none;
  border-radius: 8rpx;
  font-size: 24rpx;
  font-weight: bold;
  cursor: pointer;
}

.modal-button.cancel {
  background-color: #F5F5F5;
  color: #333;
}

.modal-button.confirm {
  background-color: #FF6B35;
  color: white;
}

.modal-button:active {
  opacity: 0.8;
}
</style>