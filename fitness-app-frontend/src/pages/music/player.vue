<template>
  <div class="music-player">
    <div class="player-header">
      <h2>音乐播放器</h2>
    </div>
    
    <!-- 上传音乐 -->
    <UploadComponent @file-uploaded="handleFileUploaded" />
    
    <!-- 音乐列表 -->
    <div class="music-list">
      <h3>音乐列表</h3>
      <div class="list-container">
        <div v-if="isLoading" class="loading-state">
          <p>加载中...</p>
        </div>
        <div v-else>
          <div 
            v-for="track in musicTracks" 
            :key="track.id"
            class="music-item"
            :class="{ active: currentTrack && currentTrack.id === track.id }"
            @click="playTrack(track)"
          >
            <div class="music-info">
              <h4>{{ track.name }}</h4>
              <p>{{ track.artist || '未知艺术家' }}</p>
            </div>
            <div class="music-actions">
              <span class="music-duration">{{ formatDuration(track.duration) }}</span>
              <button @click.stop="removeTrack(track.id)" class="remove-btn">
                <span class="icon">🗑</span>
              </button>
            </div>
          </div>
          <div v-if="musicTracks.length === 0" class="empty-list">
            <p>暂无音乐，请上传音乐文件</p>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 播放控制 -->
    <div class="player-controls">
      <div class="current-track-info" v-if="currentTrack">
        <h3>{{ currentTrack.name }}</h3>
        <p>{{ currentTrack.artist || '未知艺术家' }}</p>
      </div>
      
      <div class="progress-container">
        <span class="time">{{ formatTime(currentTime) }}</span>
        <input 
          type="range" 
          class="progress-bar" 
          :value="progress" 
          @input="seek"
          :max="duration"
        >
        <span class="time">{{ formatTime(duration) }}</span>
      </div>
      
      <div class="control-buttons">
        <button @click="prevTrack" class="control-btn">
          <span class="icon">⏮</span>
        </button>
        <button @click="togglePlay" class="control-btn play-btn">
          <span class="icon">{{ isPlaying ? '⏸' : '▶' }}</span>
        </button>
        <button @click="nextTrack" class="control-btn">
          <span class="icon">⏭</span>
        </button>
      </div>
      
      <div class="volume-control">
        <span class="icon">🔊</span>
        <input 
          type="range" 
          class="volume-bar" 
          :value="volume" 
          @input="setVolume"
          min="0"
          max="1"
          step="0.1"
        >
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import UploadComponent from './upload.vue';
import { musicApi } from '../../utils/musicApi';

// 音乐数据
const musicTracks = ref([]);
const isLoading = ref(false);

// 处理文件上传
const handleFileUploaded = (musicInfo) => {
  musicTracks.value.push(musicInfo);
};

// 删除音乐
const removeTrack = async (trackId) => {
  try {
    const response = await musicApi.deleteMusic(trackId);
    if (response.success) {
      const index = musicTracks.value.findIndex(track => track.id === trackId);
      if (index !== -1) {
        // 如果删除的是当前正在播放的音乐，停止播放
        if (currentTrack.value && currentTrack.value.id === trackId) {
          if (audio.value) {
            audio.value.pause();
            audio.value = null;
          }
          currentTrack.value = null;
          isPlaying.value = false;
        }
        musicTracks.value.splice(index, 1);
      }
    }
  } catch (error) {
    console.error('删除音乐失败:', error);
  }
};

// 加载音乐列表
const loadMusicList = async () => {
  isLoading.value = true;
  try {
    const response = await musicApi.getMusicList();
    musicTracks.value = response.tracks;
  } catch (error) {
    console.error('加载音乐列表失败:', error);
  } finally {
    isLoading.value = false;
  }
};

// 播放器状态
const audio = ref(null);
const currentTrack = ref(null);
const isPlaying = ref(false);
const currentTime = ref(0);
const duration = ref(0);
const volume = ref(0.7);

// 计算属性
const progress = computed(() => {
  return currentTime.value;
});

// 格式化时间
const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
};

// 格式化时长
const formatDuration = (seconds) => {
  return formatTime(seconds);
};

// 播放音乐
const playTrack = (track) => {
  if (audio.value) {
    audio.value.pause();
  }
  
  currentTrack.value = track;
  audio.value = new Audio(track.file_url);
  audio.value.volume = volume.value;
  
  audio.value.addEventListener('timeupdate', () => {
    currentTime.value = audio.value.currentTime;
    duration.value = audio.value.duration || 0;
  });
  
  audio.value.addEventListener('ended', () => {
    nextTrack();
  });
  
  audio.value.play();
  isPlaying.value = true;
};

// 暂停/播放
const togglePlay = () => {
  if (!audio.value) return;
  
  if (isPlaying.value) {
    audio.value.pause();
  } else {
    audio.value.play();
  }
  isPlaying.value = !isPlaying.value;
};

// 上一首
const prevTrack = () => {
  if (!currentTrack.value) return;
  
  const currentIndex = musicTracks.value.findIndex(track => track.id === currentTrack.value.id);
  const prevIndex = (currentIndex - 1 + musicTracks.value.length) % musicTracks.value.length;
  playTrack(musicTracks.value[prevIndex]);
};

// 下一首
const nextTrack = () => {
  if (!currentTrack.value) return;
  
  const currentIndex = musicTracks.value.findIndex(track => track.id === currentTrack.value.id);
  const nextIndex = (currentIndex + 1) % musicTracks.value.length;
  playTrack(musicTracks.value[nextIndex]);
};

// 调整进度
const seek = (e) => {
  if (!audio.value) return;
  audio.value.currentTime = e.target.value;
  currentTime.value = e.target.value;
};

// 调整音量
const setVolume = (e) => {
  volume.value = parseFloat(e.target.value);
  if (audio.value) {
    audio.value.volume = volume.value;
  }
};

// 组件挂载时初始化
onMounted(() => {
  console.log('音乐播放器组件已挂载');
  loadMusicList();
});

// 组件卸载时清理
onUnmounted(() => {
  if (audio.value) {
    audio.value.pause();
    audio.value = null;
  }
});
</script>

<style scoped>
.music-player {
  padding: 20px;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.player-header {
  text-align: center;
  margin-bottom: 30px;
}

.player-header h2 {
  color: #FF6B35;
  font-size: 24px;
  margin: 0;
}

.music-list {
  margin-bottom: 30px;
}

.music-list h3 {
  color: #333;
  margin-bottom: 15px;
}

.list-container {
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  overflow: hidden;
}

.music-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s;
}

.music-item:last-child {
  border-bottom: none;
}

.music-item:hover {
  background-color: #f9f9f9;
}

.music-item.active {
  background-color: #FFF5F0;
}

.music-info {
  flex: 1;
}

.music-info h4 {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 16px;
}

.music-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.music-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.music-duration {
  color: #999;
  font-size: 14px;
  margin-right: 10px;
}

.remove-btn {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  color: #999;
  transition: color 0.3s;
  padding: 5px;
  border-radius: 4px;
}

.remove-btn:hover {
  color: #ff4d4f;
  background-color: #fff1f0;
}

.empty-list {
  text-align: center;
  padding: 40px 20px;
  color: #999;
  font-size: 16px;
}

.loading-state {
  text-align: center;
  padding: 40px 20px;
  color: #666;
  font-size: 16px;
}

.player-controls {
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  padding: 20px;
}

.current-track-info {
  text-align: center;
  margin-bottom: 20px;
}

.current-track-info h3 {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 18px;
}

.current-track-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.progress-container {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.time {
  color: #666;
  font-size: 12px;
  width: 50px;
  text-align: center;
}

.progress-bar {
  flex: 1;
  margin: 0 10px;
  height: 4px;
  border-radius: 2px;
  background: #ddd;
  outline: none;
  -webkit-appearance: none;
}

.progress-bar::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #FF6B35;
  cursor: pointer;
}

.control-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
}

.control-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  margin: 0 15px;
  color: #333;
  transition: color 0.3s;
}

.control-btn:hover {
  color: #FF6B35;
}

.play-btn {
  font-size: 32px;
}

.volume-control {
  display: flex;
  align-items: center;
  justify-content: center;
}

.volume-control .icon {
  font-size: 18px;
  margin-right: 10px;
  color: #666;
}

.volume-bar {
  width: 150px;
  height: 4px;
  border-radius: 2px;
  background: #ddd;
  outline: none;
  -webkit-appearance: none;
}

.volume-bar::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #4ECDC4;
  cursor: pointer;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .music-player {
    padding: 10px;
  }
  
  .control-btn {
    margin: 0 10px;
  }
  
  .volume-bar {
    width: 100px;
  }
}
</style>