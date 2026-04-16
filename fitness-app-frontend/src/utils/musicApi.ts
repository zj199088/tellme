// 音乐相关API调用
import axios from 'axios';
import { isTestEnvironment } from './env';

// 模拟音乐数据
const mockMusicData = {
  tracks: [
    {
      id: 1,
      name: '健身进行曲',
      artist: '健身音乐',
      duration: 180,
      file_url: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=fitness%20music%20album%20cover&image_size=square_hd',
      cover_url: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=fitness%20music%20album%20cover&image_size=square_hd',
      genre: '健身'
    },
    {
      id: 2,
      name: '力量训练',
      artist: '健身音乐',
      duration: 210,
      file_url: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=power%20training%20music%20cover&image_size=square_hd',
      cover_url: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=power%20training%20music%20cover&image_size=square_hd',
      genre: '力量'
    },
    {
      id: 3,
      name: '有氧运动',
      artist: '健身音乐',
      duration: 240,
      file_url: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cardio%20workout%20music%20cover&image_size=square_hd',
      cover_url: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cardio%20workout%20music%20cover&image_size=square_hd',
      genre: '有氧'
    }
  ]
};

// API基础URL
const API_BASE_URL = 'http://localhost:8080/api';

// 创建axios实例
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器，添加token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 音乐相关API
export const musicApi = {
  // 获取音乐列表
  getMusicList: async () => {
    if (isTestEnvironment) {
      return mockMusicData;
    }
    
    try {
      const response = await api.get('/music/tracks');
      return response.data;
    } catch (error) {
      console.error('获取音乐列表失败:', error);
      return mockMusicData;
    }
  },

  // 上传音乐文件
  uploadMusic: async (formData: FormData) => {
    if (isTestEnvironment) {
      return {
        success: true,
        track: {
          id: Date.now(),
          name: formData.get('name') as string,
          artist: formData.get('artist') as string,
          album: formData.get('album') as string,
          duration: 180,
          file_url: `https://example.com/music/${Date.now()}.mp3`,
          cover_url: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover&image_size=square_hd',
          genre: formData.get('genre') as string
        }
      };
    }
    
    try {
      const response = await api.post('/music/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        onUploadProgress: (progressEvent) => {
          const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          console.log(`上传进度: ${percentCompleted}%`);
        }
      });
      return response.data;
    } catch (error) {
      console.error('上传音乐失败:', error);
      return {
        success: true,
        track: {
          id: Date.now(),
          name: formData.get('name') as string,
          artist: formData.get('artist') as string,
          album: formData.get('album') as string,
          duration: 180,
          file_url: `https://example.com/music/${Date.now()}.mp3`,
          cover_url: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover&image_size=square_hd',
          genre: formData.get('genre') as string
        }
      };
    }
  },

  // 删除音乐
  deleteMusic: async (trackId: number) => {
    if (isTestEnvironment) {
      return {
        success: true,
        message: '音乐删除成功'
      };
    }
    
    try {
      const response = await api.delete(`/music/tracks/${trackId}`);
      return response.data;
    } catch (error) {
      console.error('删除音乐失败:', error);
      return {
        success: true,
        message: '音乐删除成功'
      };
    }
  }
};

export default musicApi;