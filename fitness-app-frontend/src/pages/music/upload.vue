<template>
  <div class="upload-container">
    <h2 class="section-title">
      <span class="title-icon">📁</span>
      上传音乐
      <span class="title-glow"></span>
    </h2>
    <div class="upload-area" @dragover.prevent @drop="handleDrop" :class="{ 'drag-over': isDragOver }" @dragover="isDragOver = true" @dragleave="isDragOver = false">
      <input 
        type="file" 
        ref="fileInput"
        accept="audio/*"
        @change="handleFileChange"
        class="file-input"
      >
      <div class="upload-hint">
          <div class="upload-icon-wrapper">
            <span class="icon">📁</span>
          </div>
          <p class="upload-text">点击或拖拽音乐文件到此处上传</p>
          <p class="hint-text">支持 MP3、WAV、FLAC 等音频格式</p>
          <p class="hint-text">文件大小限制：10MB</p>
        </div>
    </div>
    
    <div class="upload-form" v-if="selectedFile">
      <div class="form-header">
        <div class="form-header-icon">🎵</div>
        <div class="form-header-text">
          <h3 class="form-header-title">编辑音乐信息</h3>
          <p class="form-header-desc">请完善音乐的详细信息</p>
        </div>
      </div>
      <div class="form-group">
        <label class="form-label">音乐名称</label>
        <input 
          type="text" 
          v-model="formData.name"
          placeholder="请输入音乐名称"
          class="form-input"
        >
      </div>
      <div class="form-group">
        <label class="form-label">艺术家</label>
        <input 
          type="text" 
          v-model="formData.artist"
          placeholder="请输入艺术家名称"
          class="form-input"
        >
      </div>
      <div class="form-group">
        <label class="form-label">专辑</label>
        <input 
          type="text" 
          v-model="formData.album"
          placeholder="请输入专辑名称"
          class="form-input"
        >
      </div>
      <div class="form-group">
        <label class="form-label">音乐流派</label>
        <input 
          type="text" 
          v-model="formData.genre"
          placeholder="请输入音乐流派"
          class="form-input"
        >
      </div>
      <div class="form-actions">
        <button @click="cancelUpload" class="btn btn-secondary">
          <span class="btn-icon">❌</span>
          取消
        </button>
        <button @click="uploadFile" class="btn btn-primary glow-button" :disabled="isUploading">
          <span class="btn-icon" v-if="!isUploading">📤</span>
          <span class="btn-icon loading-spinner" v-else></span>
          <span class="btn-text">{{ isUploading ? '上传中...' : '上传' }}</span>
          <span class="btn-glow"></span>
        </button>
      </div>
    </div>
    
    <div class="upload-progress" v-if="isUploading">
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
      </div>
      <p class="progress-text">{{ uploadProgress }}%</p>
    </div>
    
    <div class="message" :class="{ success: messageType === 'success', error: messageType === 'error' }" v-if="message">
      <span class="message-icon">{{ messageType === 'success' ? '✅' : '❌' }}</span>
      <span class="message-text">{{ message }}</span>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { musicApi } from '../../utils/musicApi';

const emit = defineEmits(['file-uploaded']);

const isDragOver = ref(false);
const fileInput = ref(null);
const selectedFile = ref(null);
const isUploading = ref(false);
const uploadProgress = ref(0);
const message = ref('');
const messageType = ref('');

const formData = reactive({
  name: '',
  artist: '',
  album: '',
  genre: ''
});

const handleFileChange = (e) => {
  const file = e.target.files[0];
  if (file) {
    selectedFile.value = file;
    formData.name = file.name.replace(/\.[^/.]+$/, '');
  }
};

const handleDrop = (e) => {
  e.preventDefault();
  isDragOver.value = false;
  const file = e.dataTransfer.files[0];
  if (file && file.type.startsWith('audio/')) {
    selectedFile.value = file;
    formData.name = file.name.replace(/\.[^/.]+$/, '');
  }
};

const uploadFile = async () => {
  if (!selectedFile.value) {
    showMessage('请选择要上传的音乐文件', 'error');
    return;
  }
  
  isUploading.value = true;
  uploadProgress.value = 0;
  
  try {
    // 创建FormData对象
    const formDataToSend = new FormData();
    formDataToSend.append('file', selectedFile.value);
    formDataToSend.append('name', formData.name || selectedFile.value.name.replace(/\.[^/.]+$/, ''));
    formDataToSend.append('artist', formData.artist || '');
    formDataToSend.append('album', formData.album || '');
    formDataToSend.append('genre', formData.genre || '');
    
    // 调用音乐上传API
    const response = await musicApi.uploadMusic(formDataToSend);
    
    if (response.success) {
      const musicInfo = response.track;
      emit('file-uploaded', musicInfo);
      showMessage('音乐上传成功', 'success');
      resetForm();
    } else {
      showMessage('上传失败: ' + (response.message || '未知错误'), 'error');
    }
  } catch (error) {
    showMessage('上传失败，请重试', 'error');
    console.error('上传失败:', error);
  } finally {
    isUploading.value = false;
  }
};

const cancelUpload = () => {
  resetForm();
};

const resetForm = () => {
  selectedFile.value = null;
  formData.name = '';
  formData.artist = '';
  formData.album = '';
  formData.genre = '';
  if (fileInput.value) {
    fileInput.value.value = '';
  }
};

const showMessage = (msg, type) => {
  message.value = msg;
  messageType.value = type;
  setTimeout(() => {
    message.value = '';
    messageType.value = '';
  }, 3000);
};
</script>

<style scoped>
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10.0px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
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

.section-title {
  font-size: 14.0px;
  font-weight: bold;
  margin-bottom: 10.0px;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 5.0px;
  position: relative;
}

.title-icon {
  font-size: 16.0px;
}

.title-glow {
  position: absolute;
  bottom: -4.0px;
  left: 0;
  width: 25.0px;
  height: 1.5px;
  background: var(--gradient-cyan);
  border-radius: 1.0px;
  box-shadow: 0 0 10px var(--neon-cyan);
}

.upload-container {
  position: relative;
}

.upload-area {
  border: 2px dashed var(--border-color);
  border-radius: 10.0px;
  padding: 30.0px 15.0px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  background: var(--bg-tertiary);
}

.upload-area:hover,
.upload-area.drag-over {
  border-color: var(--neon-cyan);
  background: rgba(0, 245, 255, 0.08);
  box-shadow: 0 0 30px rgba(0, 245, 255, 0.2);
}

.file-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
  z-index: 1;
}

.upload-hint {
  position: relative;
  z-index: 0;
}

.upload-icon-wrapper {
  width: 50.0px;
  height: 50.0px;
  border-radius: 50%;
  background: var(--bg-card);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 10.0px;
  border: 2px solid var(--border-color);
  transition: all 0.3s ease;
}

.upload-area:hover .upload-icon-wrapper,
.upload-area.drag-over .upload-icon-wrapper {
  border-color: var(--neon-cyan);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.3);
  transform: scale(1.1);
}

.upload-hint .icon {
  font-size: 25.0px;
}

.upload-text {
  margin: 0 0 5.0px 0;
  color: var(--text-primary);
  font-size: 12.0px;
  font-weight: 600;
}

.hint-text {
  margin: 0;
  color: var(--text-muted);
  font-size: 11.0px;
}

.upload-form {
  margin-top: 15.0px;
  animation: fadeInUp 0.4s ease;
}

.form-header {
  display: flex;
  align-items: center;
  gap: 10.0px;
  margin-bottom: 12.5px;
  padding-bottom: 10.0px;
  border-bottom: 1px solid var(--border-color);
}

.form-header-icon {
  width: 30.0px;
  height: 30.0px;
  border-radius: 50%;
  background: var(--bg-tertiary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16.0px;
  border: 2px solid var(--neon-cyan);
  box-shadow: 0 0 15px rgba(0, 245, 255, 0.2);
}

.form-header-title {
  margin: 0 0 2.5px 0;
  color: var(--text-primary);
  font-size: 13.0px;
  font-weight: 600;
}

.form-header-desc {
  margin: 0;
  color: var(--text-secondary);
  font-size: 11.0px;
}

.form-group {
  margin-bottom: 10.0px;
}

.form-label {
  display: block;
  margin-bottom: 5.0px;
  font-weight: 600;
  color: var(--text-primary);
  font-size: 12.0px;
}

.form-input {
  width: 100%;
  padding: 9.0px;
  border: 1px solid var(--border-color);
  border-radius: 6.0px;
  font-size: 12.0px;
  background: var(--bg-tertiary);
  color: var(--text-primary);
  transition: all 0.3s ease;
}

.form-input:focus {
  outline: none;
  border-color: var(--neon-cyan);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.3);
}

.form-input::placeholder {
  color: var(--text-muted);
}

.form-actions {
  display: flex;
  gap: 7.5px;
  margin-top: 12.5px;
}

.btn {
  flex: 1;
  padding: 9.0px;
  border: none;
  border-radius: 6.0px;
  font-size: 12.0px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5.0px;
}

.btn-secondary {
  background: var(--bg-tertiary);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.btn-secondary:hover {
  background: rgba(255, 0, 110, 0.1);
  color: var(--neon-pink);
  border-color: var(--neon-pink);
  transform: translateY(-1.0px);
}

.btn-primary {
  position: relative;
  overflow: hidden;
  background: var(--gradient-cyan);
  color: white;
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.3);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-1.0px) scale(1.02);
  box-shadow: 0 0 30px rgba(0, 245, 255, 0.5);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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

.btn-icon {
  font-size: 10.0px;
  position: relative;
  z-index: 1;
}

.loading-spinner {
  width: 10.0px;
  height: 10.0px;
  border: 1.5px solid rgba(255, 255, 255, 0.3);
  border-top: 1.5px solid white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.btn-text {
  position: relative;
  z-index: 1;
}

.upload-progress {
  margin-top: 12.5px;
  animation: fadeInUp 0.3s ease;
}

.progress-bar {
  width: 100%;
  height: 6.0px;
  background: var(--bg-tertiary);
  border-radius: 3.0px;
  overflow: hidden;
  margin-bottom: 6.0px;
  border: 1px solid var(--border-color);
}

.progress-fill {
  height: 100%;
  background: var(--gradient-neon);
  border-radius: 3.0px;
  transition: width 0.3s ease;
}

.progress-text {
  text-align: center;
  color: var(--text-secondary);
  font-size: 11.0px;
  margin: 0;
  font-weight: 600;
  background: var(--gradient-neon);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.message {
  margin-top: 12.5px;
  padding: 9.0px;
  border-radius: 6.0px;
  display: flex;
  align-items: center;
  gap: 6.0px;
  font-size: 12.0px;
  animation: fadeInUp 0.3s ease;
}

.message.success {
  background: rgba(0, 255, 136, 0.1);
  color: var(--neon-green);
  border: 1px solid var(--neon-green);
}

.message.error {
  background: rgba(255, 0, 110, 0.1);
  color: var(--neon-pink);
  border: 1px solid var(--neon-pink);
}

.message-icon {
  font-size: 12.0px;
}

.message-text {
  font-weight: 500;
}

@media (max-width: 768px) {
  .section-title {
    font-size: 12.0px;
  }
  
  .upload-area {
    padding: 20.0px 10.0px;
  }
  
  .upload-icon-wrapper {
    width: 40.0px;
    height: 40.0px;
  }
  
  .upload-hint .icon {
    font-size: 20.0px;
  }
  
  .upload-text {
    font-size: 11.0px;
  }
  
  .form-label {
    font-size: 11.0px;
  }
  
  .form-input {
    padding: 7.5px;
    font-size: 11.0px;
  }
  
  .btn {
    padding: 7.5px;
    font-size: 11.0px;
  }
}
</style>
