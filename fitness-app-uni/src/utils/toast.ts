interface ToastOptions {
  message: string;
  type?: 'success' | 'error' | 'info' | 'warning';
  duration?: number;
}

const showToast = (options: string | ToastOptions) => {
  if (typeof options === 'string') {
    options = { message: options };
  }

  const { message, type = 'info', duration = 2000 } = options;
  
  let icon: 'success' | 'error' | 'none' | 'loading' = 'none';
  switch (type) {
    case 'success':
      icon = 'success';
      break;
    case 'error':
      icon = 'error';
      break;
    case 'warning':
      icon = 'none';
      break;
    default:
      icon = 'none';
  }

  uni.showToast({
    title: message,
    icon,
    duration,
    mask: true
  });
};

export const toast = {
  success: (message: string, duration?: number) => showToast({ message, type: 'success', duration }),
  error: (message: string, duration?: number) => showToast({ message, type: 'error', duration }),
  info: (message: string, duration?: number) => showToast({ message, type: 'info', duration }),
  warning: (message: string, duration?: number) => showToast({ message, type: 'warning', duration })
};

export default toast;
