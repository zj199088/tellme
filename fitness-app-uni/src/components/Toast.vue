<template>
  <div class="toast-container" v-if="show">
    <div class="toast" :class="type">
      <div class="toast-icon">{{ icon }}</div>
      <div class="toast-message">{{ message }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';

interface Props {
  message: string;
  type?: 'success' | 'error' | 'info' | 'warning';
  duration?: number;
}

const props = withDefaults(defineProps<Props>(), {
  type: 'info',
  duration: 3000
});

const emit = defineEmits(['close']);
const show = ref(false);

const icons = {
  success: '✓',
  error: '✗',
  info: 'ℹ',
  warning: '⚠'
};

const icon = computed(() => icons[props.type]);

let timer: number | null = null;

onMounted(() => {
  show.value = true;
  timer = window.setTimeout(() => {
    show.value = false;
    setTimeout(() => {
      emit('close');
    }, 300);
  }, props.duration);
});

onUnmounted(() => {
  if (timer) {
    clearTimeout(timer);
  }
});
</script>

<style scoped>
.toast-container {
  position: fixed;
  top: 80px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  animation: slideDown 0.3s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}

.toast {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  border-radius: 10px;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  border: 1px solid var(--border-color);
}

.toast.success {
  background: linear-gradient(135deg, rgba(46, 213, 115, 0.2) 0%, rgba(46, 213, 115, 0.1) 100%);
  border-color: rgba(46, 213, 115, 0.3);
}

.toast.error {
  background: linear-gradient(135deg, rgba(255, 71, 87, 0.2) 0%, rgba(255, 71, 87, 0.1) 100%);
  border-color: rgba(255, 71, 87, 0.3);
}

.toast.info {
  background: linear-gradient(135deg, rgba(0, 245, 255, 0.2) 0%, rgba(0, 245, 255, 0.1) 100%);
  border-color: rgba(0, 245, 255, 0.3);
}

.toast.warning {
  background: linear-gradient(135deg, rgba(255, 159, 67, 0.2) 0%, rgba(255, 159, 67, 0.1) 100%);
  border-color: rgba(255, 159, 67, 0.3);
}

.toast-icon {
  font-size: 20px;
  font-weight: bold;
}

.toast.success .toast-icon {
  color: #2ed573;
}

.toast.error .toast-icon {
  color: #ff4757;
}

.toast.info .toast-icon {
  color: #00f5ff;
}

.toast.warning .toast-icon {
  color: #ff9f43;
}

.toast-message {
  font-size: 14px;
  color: var(--text-primary);
}
</style>
