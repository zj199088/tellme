// 音乐相关API调用
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

// 封装 uni.request
const request = (options: any) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token');
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers
    };
    
    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }
    
    uni.request({
      url: API_BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      headers,
      success: (response) => {
        resolve(response);
      },
      fail: (error) => {
        reject(error);
      }
    });
  });
};

// 音乐相关API
export const musicApi = {
  // 获取音乐列表
  getMusicList: async () => {
    if (isTestEnvironment) {
      return mockMusicData;
    }
    
    try {
      const response = await request({
        url: '/music/tracks',
        method: 'GET'
      });
      const data = (response as any).data;
      // 后端返回的结构是 { code, message, data }，其中 data 是音乐列表
      if (data && data.data) {
        // 处理音乐列表，转换字段名并去除fileUrl中的反引号
        const tracks = data.data.map((track: any) => ({
          id: track.id,
          name: track.name,
          artist: track.artist,
          album: track.album,
          duration: track.duration || 180,
          file_url: track.fileUrl ? track.fileUrl.replace(/[`]/g, '') : '',
          cover_url: track.coverUrl || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover&image_size=square_hd',
          genre: track.genre
        }));
        return { tracks };
      }
      return mockMusicData;
    } catch (error) {
      console.error('获取音乐列表失败:', error);
      return mockMusicData;
    }
  },

  // 上传音乐文件
  uploadMusic: async (file: any, formData: any) => {
    if (isTestEnvironment) {
      return {
        success: true,
        track: {
          id: Date.now(),
          name: formData.name,
          artist: formData.artist,
          album: formData.album,
          duration: 180,
          file_url: `https://example.com/music/${Date.now()}.mp3`,
          cover_url: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover&image_size=square_hd',
          genre: formData.genre
        }
      };
    }
    
    return new Promise((resolve) => {
      uni.uploadFile({
        url: API_BASE_URL + '/music/upload',
        filePath: file.tempFilePaths[0],
        name: 'file',
        formData: formData,
        success: (uploadFileRes) => {
          const data = JSON.parse(uploadFileRes.data);
          if (data && data.data) {
            resolve({
              success: true,
              track: {
                id: data.data.id,
                name: data.data.name,
                artist: data.data.artist,
                album: data.data.album,
                duration: data.data.duration || 180,
                file_url: data.data.fileUrl ? data.data.fileUrl.replace(/[`]/g, '') : '',
                cover_url: data.data.coverUrl || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20album%20cover&image_size=square_hd',
                genre: data.data.genre
              }
            });
          } else {
            resolve({
              success: false,
              message: '上传失败'
            });
          }
        },
        fail: (error) => {
          console.error('上传音乐失败:', error);
          resolve({
            success: false,
            message: '上传失败'
          });
        },
        complete: (res) => {
          console.log('上传完成:', res);
        }
      });
    });
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
      const response = await request({
        url: `/music/tracks/${trackId}`,
        method: 'DELETE'
      });
      const data = (response as any).data;
      // 后端返回的结构是 { code, message, data }
      if (data && data.code === 200) {
        return {
          success: true,
          message: data.message || '音乐删除成功'
        };
      }
      return {
        success: false,
        message: data?.message || '删除失败'
      };
    } catch (error) {
      console.error('删除音乐失败:', error);
      return {
        success: false,
        message: '删除失败'
      };
    }
  }
};

export default musicApi;