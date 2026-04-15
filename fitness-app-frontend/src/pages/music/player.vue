<template>
  <div class="music-player">
    <div class="particle-bg" id="particleBg"></div>
    <div class="glow-orbs">
      <div class="orb orb-1"></div>
      <div class="orb orb-2"></div>
      <div class="orb orb-3"></div>
    </div>
    
    <div class="header">
      <div class="header-content">
        <div class="header-icon">🎵</div>
        <h1 class="title neon-glow">音乐播放器</h1>
      </div>
      <div class="header-bg"></div>
      <div class="scanline"></div>
    </div>
    
    <div class="content">
      <div class="upload-section animate-in glow-card">
        <UploadComponent @file-uploaded="handleFileUploaded" />
      </div>
      
      <div class="music-list animate-in glow-card" style="animation-delay: 0.1s;">
        <h2 class="section-title">
          <span class="title-icon">📋</span>
          音乐列表
          <span class="title-glow"></span>
        </h2>
        <div class="list-container">
          <div v-if="isLoading" class="loading-state">
            <div class="loading-spinner"></div>
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
              <div class="music-cover">
                <img v-if="track.cover_url" :src="track.cover_url" :alt="track.name" />
                <div v-else class="music-cover-placeholder">🎵</div>
              </div>
              <div class="music-info">
                <h3 class="music-name">{{ track.name }}</h3>
                <p class="music-artist">{{ track.artist || '未知艺术家' }}</p>
              </div>
              <div class="music-actions">
                <span class="music-duration">{{ formatDuration(track.duration) }}</span>
                <button @click.stop="removeTrack(track.id)" class="remove-btn">
                  <span class="icon">🗑</span>
                </button>
              </div>
            </div>
            <div v-if="musicTracks.length === 0" class="empty-list">
              <div class="empty-icon">🎶</div>
              <p>暂无音乐，请上传音乐文件</p>
            </div>
          </div>
        </div>
      </div>
      
      <div class="player-controls animate-in glow-card" style="animation-delay: 0.2s;" v-if="currentTrack">
        <div class="current-track-info">
          <div class="track-cover">
            <img v-if="currentTrack.cover_url" :src="currentTrack.cover_url" :alt="currentTrack.name" />
            <div v-else class="track-cover-placeholder">🎵</div>
          </div>
          <div class="track-details">
            <h2 class="track-name">{{ currentTrack.name }}</h2>
            <p class="track-artist">{{ currentTrack.artist || '未知艺术家' }}</p>
          </div>
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
          <button @click="togglePlay" class="control-btn play-btn glow-button">
            <span class="icon">{{ isPlaying ? '⏸' : '▶' }}</span>
            <span class="btn-glow"></span>
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
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue';
import UploadComponent from './upload.vue';
import { musicApi } from '../../utils/musicApi';

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

const musicTracks = ref([]);
const isLoading = ref(false);

const handleFileUploaded = (musicInfo) => {
  musicTracks.value.push(musicInfo);
};

const removeTrack = async (trackId) => {
  try {
    const response = await musicApi.deleteMusic(trackId);
    if (response.success) {
      const index = musicTracks.value.findIndex(track => track.id === trackId);
      if (index !== -1) {
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

const audio = ref(null);
const currentTrack = ref(null);
const isPlaying = ref(false);
const currentTime = ref(0);
const duration = ref(0);
const volume = ref(0.7);

const progress = computed(() => {
  return currentTime.value;
});

const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
};

const formatDuration = (seconds) => {
  return formatTime(seconds);
};

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

const togglePlay = () => {
  if (!audio.value) return;
  
  if (isPlaying.value) {
    audio.value.pause();
  } else {
    audio.value.play();
  }
  isPlaying.value = !isPlaying.value;
};

const prevTrack = () => {
  if (!currentTrack.value) return;
  
  const currentIndex = musicTracks.value.findIndex(track => track.id === currentTrack.value.id);
  const prevIndex = (currentIndex - 1 + musicTracks.value.length) % musicTracks.value.length;
  playTrack(musicTracks.value[prevIndex]);
};

const nextTrack = () => {
  if (!currentTrack.value) return;
  
  const currentIndex = musicTracks.value.findIndex(track => track.id === currentTrack.value.id);
  const nextIndex = (currentIndex + 1) % musicTracks.value.length;
  playTrack(musicTracks.value[nextIndex]);
};

const seek = (e) => {
  if (!audio.value) return;
  audio.value.currentTime = e.target.value;
  currentTime.value = e.target.value;
};

const setVolume = (e) => {
  volume.value = parseFloat(e.target.value);
  if (audio.value) {
    audio.value.volume = volume.value;
  }
};

onMounted(() => {
  console.log('音乐播放器组件已挂载');
  loadMusicList();
});

onUnmounted(() => {
  if (audio.value) {
    audio.value.pause();
    audio.value = null;
  }
});
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

.music-player {
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

.upload-section,
.music-list,
.player-controls {
  background: var(--bg-card);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border-radius: 15.0px;
  padding: 20.0px;
  margin-bottom: 15.0px;
  box-shadow: 0 4.0px 20.0px rgba(0, 0, 0, 0.4);
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  overflow: hidden;
  border: 1px solid var(--border-color);
}

.upload-section::before,
.music-list::before,
.player-controls::before {
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

.upload-section::after,
.music-list::after,
.player-controls::after {
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

.section-title {
  font-size: 16.0px;
  font-weight: bold;
  margin-bottom: 12.5px;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 6.0px;
  position: relative;
}

.title-icon {
  font-size: 18.0px;
}

.title-glow {
  position: absolute;
  bottom: -4.0px;
  left: 0;
  width: 30.0px;
  height: 1.5px;
  background: var(--gradient-cyan);
  border-radius: 1.0px;
  box-shadow: 0 0 10px var(--neon-cyan);
}

.list-container {
  position: relative;
}

.loading-state {
  text-align: center;
  padding: 30.0px 10.0px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10.0px;
}

.loading-spinner {
  width: 30.0px;
  height: 30.0px;
  border: 2.0px solid var(--border-color);
  border-top: 2.0px solid var(--neon-cyan);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  box-shadow: var(--glow-cyan);
}

.loading-state p {
  color: var(--text-muted);
  font-size: 12.0px;
}

.music-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10.0px;
  border-bottom: 1px solid rgba(0, 245, 255, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 8.0px;
  margin-bottom: 5.0px;
}

.music-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.music-item:hover {
  background: rgba(0, 245, 255, 0.08);
  transform: translateX(2.5px);
}

.music-item.active {
  background: rgba(0, 245, 255, 0.12);
  border: 1px solid var(--neon-cyan);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.2);
}

.music-cover {
  width: 35.0px;
  height: 35.0px;
  border-radius: 6.0px;
  overflow: hidden;
  margin-right: 10.0px;
  flex-shrink: 0;
  border: 2px solid var(--border-color);
}

.music-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.music-cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16.0px;
  background: var(--bg-tertiary);
}

.music-info {
  flex: 1;
}

.music-name {
  margin: 0 0 4.0px 0;
  color: var(--text-primary);
  font-size: 13.0px;
  font-weight: 600;
  transition: color 0.3s ease;
}

.music-item:hover .music-name {
  color: var(--neon-cyan);
}

.music-artist {
  margin: 0;
  color: var(--text-secondary);
  font-size: 11.0px;
}

.music-actions {
  display: flex;
  align-items: center;
  gap: 7.5px;
}

.music-duration {
  color: var(--text-muted);
  font-size: 11.0px;
}

.remove-btn {
  background: none;
  border: none;
  font-size: 12.0px;
  cursor: pointer;
  color: var(--text-muted);
  transition: all 0.3s ease;
  padding: 4.0px;
  border-radius: 50%;
  width: 22.5px;
  height: 22.5px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remove-btn:hover {
  color: var(--neon-pink);
  background: rgba(255, 0, 110, 0.1);
  transform: scale(1.1);
}

.empty-list {
  text-align: center;
  padding: 40.0px 10.0px;
}

.empty-icon {
  font-size: 40.0px;
  margin-bottom: 10.0px;
  animation: float 4s ease-in-out infinite;
}

.empty-list p {
  color: var(--text-muted);
  font-size: 12.0px;
}

.current-track-info {
  display: flex;
  align-items: center;
  gap: 12.5px;
  margin-bottom: 15.0px;
}

.track-cover {
  width: 60.0px;
  height: 60.0px;
  border-radius: 10.0px;
  overflow: hidden;
  flex-shrink: 0;
  border: 1.5px solid var(--neon-cyan);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.3);
}

.track-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.track-cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 25.0px;
  background: var(--bg-tertiary);
}

.track-details {
  flex: 1;
}

.track-name {
  margin: 0 0 4.0px 0;
  color: var(--text-primary);
  font-size: 15.0px;
  font-weight: bold;
  background: var(--gradient-neon);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.track-artist {
  margin: 0;
  color: var(--text-secondary);
  font-size: 12.0px;
}

.progress-container {
  display: flex;
  align-items: center;
  margin-bottom: 15.0px;
  gap: 7.5px;
}

.time {
  color: var(--text-muted);
  font-size: 11.0px;
  width: 40.0px;
  text-align: center;
  font-family: monospace;
}

.progress-bar {
  flex: 1;
  margin: 0;
  height: 5.0px;
  border-radius: 2.5px;
  background: var(--bg-tertiary);
  outline: none;
  -webkit-appearance: none;
  cursor: pointer;
}

.progress-bar::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 12.0px;
  height: 12.0px;
  border-radius: 50%;
  background: var(--neon-cyan);
  cursor: pointer;
  box-shadow: 0 0 15px rgba(0, 245, 255, 0.5);
  transition: transform 0.2s ease;
}

.progress-bar::-webkit-slider-thumb:hover {
  transform: scale(1.2);
}

.control-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 15.0px;
  gap: 15.0px;
}

.control-btn {
  background: var(--bg-tertiary);
  border: 2px solid var(--border-color);
  font-size: 16.0px;
  cursor: pointer;
  color: var(--text-primary);
  transition: all 0.3s ease;
  width: 35.0px;
  height: 35.0px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.control-btn:hover {
  color: var(--neon-cyan);
  border-color: var(--neon-cyan);
  transform: scale(1.1);
  box-shadow: 0 0 20px rgba(0, 245, 255, 0.3);
}

.play-btn {
  width: 45.0px;
  height: 45.0px;
  font-size: 20.0px;
  position: relative;
  overflow: hidden;
}

.play-btn .icon {
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

.volume-control {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 7.5px;
}

.volume-control .icon {
  font-size: 14.0px;
  color: var(--text-secondary);
}

.volume-bar {
  width: 100.0px;
  height: 4.0px;
  border-radius: 2.0px;
  background: var(--bg-tertiary);
  outline: none;
  -webkit-appearance: none;
  cursor: pointer;
}

.volume-bar::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 10.0px;
  height: 10.0px;
  border-radius: 50%;
  background: var(--neon-purple);
  cursor: pointer;
  box-shadow: 0 0 15px rgba(139, 92, 246, 0.5);
  transition: transform 0.2s ease;
}

.volume-bar::-webkit-slider-thumb:hover {
  transform: scale(1.2);
}

@media (max-width: 768px) {
  .content {
    padding: 10.0px 7.5px;
  }
  
  .upload-section,
  .music-list,
  .player-controls {
    padding: 15.0px;
  }
  
  .title {
    font-size: 17.0px;
  }
  
  .section-title {
    font-size: 14.0px;
  }
  
  .music-item {
    padding: 7.5px;
  }
  
  .music-cover {
    width: 30.0px;
    height: 30.0px;
  }
  
  .current-track-info {
    flex-direction: column;
    text-align: center;
  }
  
  .track-cover {
    width: 50.0px;
    height: 50.0px;
  }
  
  .control-buttons {
    gap: 10.0px;
  }
  
  .control-btn {
    width: 30.0px;
    height: 30.0px;
    font-size: 14.0px;
  }
  
  .play-btn {
    width: 40.0px;
    height: 40.0px;
    font-size: 18.0px;
  }
}
</style>
