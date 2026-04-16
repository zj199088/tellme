import { ref, createApp, type Component } from 'vue';
import Toast from '../components/Toast.vue';

interface ToastOptions {
  message: string;
  type?: 'success' | 'error' | 'info' | 'warning';
  duration?: number;
}

let toastContainer: HTMLElement | null = null;

const showToast = (options: string | ToastOptions) => {
  if (typeof options === 'string') {
    options = { message: options };
  }

  if (!toastContainer) {
    toastContainer = document.createElement('div');
    toastContainer.id = 'toast-container';
    document.body.appendChild(toastContainer);
  }

  const toastId = Date.now();
  const toastWrapper = document.createElement('div');
  toastWrapper.id = `toast-${toastId}`;
  toastContainer.appendChild(toastWrapper);

  const app = createApp(Toast, {
    ...options,
    onClose: () => {
      app.unmount();
      if (toastWrapper.parentNode) {
        toastWrapper.parentNode.removeChild(toastWrapper);
      }
    }
  });

  app.mount(toastWrapper);
};

export const toast = {
  success: (message: string, duration?: number) => showToast({ message, type: 'success', duration }),
  error: (message: string, duration?: number) => showToast({ message, type: 'error', duration }),
  info: (message: string, duration?: number) => showToast({ message, type: 'info', duration }),
  warning: (message: string, duration?: number) => showToast({ message, type: 'warning', duration })
};

export default toast;
