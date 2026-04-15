<template>
  <div class="upload-container">
    <h3>上传音乐</h3>
    <div class="upload-area" @dragover.prevent @drop="handleDrop">
      <input 
        type="file" 
        ref="fileInput"
        accept="audio/*"
        @change="handleFileChange"
        class="file-input"
      >
      <div class="upload-hint">
        <span class="icon">📁</span>
        <p>点击或拖拽音乐文件到此处上传</p>
        <p class="hint-text">支持 MP3、WAV、FLAC 等音频格式</p>
      </div>
    </div>
    
    <div class="upload-form" v-if="selectedFile">
      <div class="form-group">
        <label>音乐名称</label>
        <input 
          type="text" 
          v-model="formData.name"
          placeholder="请输入音乐名称"
        >
      </div>
      <div class="form-group">
        <label>艺术家</label>
        <input 
          type="text" 
          v-model="formData.artist"
          placeholder="请输入艺术家名称"
        >
      </div>
      <div class="form-group">
        <label>专辑</label>
        <input 
          type="text" 
          v-model="formData.album"
          placeholder="请输入专辑名称"
        >
      </div>
      <div class="form-group">
        <label>音乐流派</label>
        <input 
          type="text" 
          v-model="formData.genre"
          placeholder="请输入音乐流派"
        >
      </div>
      <div class="form-actions">
        <button @click="cancelUpload" class="btn btn-secondary">取消</button>
        <button @click="uploadFile" class="btn btn-primary" :disabled="isUploading">
          {{ isUploading ? '上传中...' : '上传' }}
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
      {{ message }}
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';

// 组件属性
const emit = defineEmits(['file-uploaded']);

// 状态
const fileInput = ref(null);
const selectedFile = ref(null);
const isUploading = ref(false);
const uploadProgress = ref(0);
const message = ref('');
const messageType = ref('');

// 表单数据
const formData = reactive({
  name: '',
  artist: '',
  album: '',
  genre: ''
});

// 处理文件选择
const handleFileChange = (e) => {
  const file = e.target.files[0];
  if (file) {
    selectedFile.value = file;
    // 默认使用文件名作为音乐名称
    formData.name = file.name.replace(/\.[^/.]+$/, '');
  }
};

// 处理拖拽上传
const handleDrop = (e) => {
  e.preventDefault();
  const file = e.dataTransfer.files[0];
  if (file && file.type.startsWith('audio/')) {
    selectedFile.value = file;
    formData.name = file.name.replace(/\.[^/.]+$/, '');
  }
};

// 上传文件
const uploadFile = async () => {
  if (!selectedFile.value) {
    showMessage('请选择要上传的音乐文件', 'error');
    return;
  }
  
  if (!formData.name) {
    showMessage('请输入音乐名称', 'error');
    return;
  }
  
  isUploading.value = true;
  uploadProgress.value = 0;
  
  try {
    // 模拟文件上传过程
    for (let i = 0; i <= 100; i += 10) {
      await new Promise(resolve => setTimeout(resolve, 300));
      uploadProgress.value = i;
    }
    
    // 模拟上传成功，返回音乐信息
    const musicInfo = {
      id: Date.now(),
      name: formData.name,
      artist: formData.artist,
      album: formData.album,
      duration: 180, // 模拟3分钟
      file_url: `https://example.com/music/${Date.now()}.mp3`,
      cover_url: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover&image_size=square_hd',
      genre: formData.genre
    };
    
    emit('file-uploaded', musicInfo);
    showMessage('音乐上传成功', 'success');
    
    // 重置表单
    resetForm();
  } catch (error) {
    showMessage('上传失败，请重试', 'error');
    console.error('上传失败:', error);
  } finally {
    isUploading.value = false;
  }
};

// 取消上传
const cancelUpload = () => {
  resetForm();
};

// 重置表单
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

// 显示消息
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
.upload-container {
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  padding: 20px;
  margin-bottom: 20px;
}

.upload-container h3 {
  color: #333;
  margin-bottom: 20px;
}

.upload-area {
  border: 2px dashed #ddd;
  border-radius: 8px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.upload-area:hover {
  border-color: #FF6B35;
  background-color: #FFF5F0;
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

.upload-hint .icon {
  font-size: 48px;
  display: block;
  margin-bottom: 15px;
}

.upload-hint p {
  margin: 0 0 5px 0;
  color: #666;
  font-size: 16px;
}

.upload-hint .hint-text {
  font-size: 14px;
  color: #999;
}

.upload-form {
  margin-top: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #333;
  font-size: 14px;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #FF6B35;
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background-color: #FF6B35;
  color: white;
}

.btn-primary:hover {
  background-color: #e55a2b;
}

.btn-primary:disabled {
  background-color: #ffb399;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #ddd;
  color: #333;
}

.btn-secondary:hover {
  background-color: #ccc;
}

.upload-progress {
  margin-top: 20px;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background-color: #ddd;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 10px;
}

.progress-fill {
  height: 100%;
  background-color: #4ECDC4;
  transition: width 0.3s;
}

.progress-text {
  text-align: center;
  color: #666;
  font-size: 14px;
  margin: 0;
}

.message {
  margin-top: 20px;
  padding: 10px;
  border-radius: 4px;
  text-align: center;
  font-size: 14px;
}

.message.success {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.message.error {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}
</style>