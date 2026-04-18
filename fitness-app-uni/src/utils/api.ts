import { isTestEnvironment, mockData } from './env';
import toast from './toast';

const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

// 环境检测
const isUniApp = typeof uni !== 'undefined';

// 存储操作封装
const storage = {
  get: (key: string) => {
    if (isUniApp) {
      return uni.getStorageSync(key);
    } else {
      return localStorage.getItem(key);
    }
  },
  set: (key: string, value: any) => {
    if (isUniApp) {
      uni.setStorageSync(key, value);
    } else {
      localStorage.setItem(key, value);
    }
  },
  remove: (key: string) => {
    if (isUniApp) {
      uni.removeStorageSync(key);
    } else {
      localStorage.removeItem(key);
    }
  }
};

// 网络请求封装
const request = (options: any) => {
  return new Promise((resolve, reject) => {
    const token = storage.get('token');
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers
    };
    
    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }
    
    if (isUniApp) {
      // uni-app 环境
      uni.request({
        url: BASE_URL + options.url,
        method: options.method || 'GET',
        data: options.data,
        headers,
        success: (response) => {
          const res = response.data as any;
          // 检查响应是否包含错误code
          if (res && res.code !== undefined && res.code !== 200) {
            // 显示错误消息
            toast.error(res.message || '请求失败');
            // 拒绝Promise，让调用方进入catch块
            reject(new Error(res.message || '请求失败'));
          } else {
            resolve(response);
          }
        },
        fail: (error) => {
          // 显示错误消息
          toast.error('网络错误');
          reject(error);
        }
      });
    } else {
      // Web 环境
      const fetchOptions: RequestInit = {
        method: options.method || 'GET',
        headers,
        credentials: 'include'
      };
      
      if (options.method !== 'GET' && options.data) {
        fetchOptions.body = JSON.stringify(options.data);
      }
      
      fetch(BASE_URL + options.url, fetchOptions)
        .then(response => response.json())
        .then(data => {
          // 检查响应是否包含错误code
          if (data && data.code !== undefined && data.code !== 200) {
            // 显示错误消息
            toast.error(data.message || '请求失败');
            // 拒绝Promise，让调用方进入catch块
            reject(new Error(data.message || '请求失败'));
          } else {
            resolve({ data });
          }
        })
        .catch(error => {
          // 显示错误消息
          toast.error('网络错误');
          reject(error);
        });
    }
  });
};

export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T | null;
}

export interface Exercise {
  id: number;
  exerciseName: string;
  sets: number;
  reps: string;
  duration: string;
  note: string;
  sortOrder: number;
}

export interface TemplateDay {
  id: number;
  templateId: number;
  dayOfWeek: number;
  isRestDay: number;
  restNote: string;
  estimatedDuration: number;
}

export interface Template {
  id: number;
  name: string;
  description: string;
  image: string;
  difficulty: string;
  isPublic: number;
  createdBy: number | null;
}

export interface FitnessPlan {
  id: number;
  userId: number;
  templateId: number;
  name: string;
  type: string;
  goal: string;
  difficulty: string;
  durationWeeks: number;
  startDate: string;
  endDate: string;
  status: string;
  isShared: number;
  sharedCode: string;
  lastWorkoutDate: string;
  currentDay?: number;
  totalDays?: number;
  description?: string;
}

export interface WorkoutRecord {
  id: number;
  userId: number;
  planId: number;
  scheduleId: number;
  scheduleExerciseId: number;
  exerciseId: number;
  exerciseName: string;
  date: string;
  setsCompleted: string;
  weight: number | null;
  duration: number;
  notes: string;
  completed?: boolean;
}

export interface WorkoutSession {
  id: number;
  planId: number;
  planName: string;
  date: string;
  completed: boolean;
  duration: number;
  exercises: Array<{
    id: number;
    name: string;
    sets: number;
    reps: string;
    completed: boolean;
  }>;
}

export interface Category {
  id: number;
  name: string;
  icon: string;
}

export interface ExerciseItem {
  id: number;
  categoryId: number;
  name: string;
  description: string;
  defaultSets: number;
  defaultReps: string;
  defaultDuration: string;
}

export interface WorkoutSchedule {
  id: number;
  planId: number;
  dayOfWeek: number;
  isRestDay: number;
  estimatedDuration: number;
  date: string;
}

export interface ScheduleExercise {
  id: number;
  scheduleId: number;
  exerciseId: number;
  exerciseName: string;
  sets: number;
  reps: string;
  duration: string;
  weight: number;
  sortOrder: number;
}

export const api = {
  // 认证相关API
  auth: {
    login: async (username: string, password: string): Promise<{ success: boolean; token?: string; role?: string; message: string }> => {
      if (isTestEnvironment) {
        return {
          success: true,
          token: 'test-token',
          role: 'user',
          message: '登录成功'
        };
      }
      const response = await request({
        url: '/auth/login',
        method: 'POST',
        data: { username, password }
      });
      const data = (response as any).data;
      return data.data;
    },
    adminLogin: async (username: string, password: string): Promise<{ success: boolean; token?: string; role?: string; message: string }> => {
      if (isTestEnvironment) {
        return {
          success: true,
          token: 'test-admin-token',
          role: 'admin',
          message: '登录成功'
        };
      }
      const response = await request({
        url: '/auth/admin/login',
        method: 'POST',
        data: { username, password }
      });
      const data = (response as any).data;
      return data.data;
    },
    register: async (username: string, password: string, nickname: string): Promise<{ success: boolean; token?: string; role?: string; message: string }> => {
      if (isTestEnvironment) {
        return {
          success: true,
          token: 'test-token',
          role: 'user',
          message: '注册成功'
        };
      }
      const response = await request({
        url: '/auth/register',
        method: 'POST',
        data: { username, password, nickname }
      });
      const data = (response as any).data;
      return data.data;
    },
    wechatLogin: async (params: { code?: string; openId?: string; nickname?: string; avatarUrl?: string; gender?: number; country?: string; province?: string; city?: string }): Promise<{ success: boolean; token?: string; role?: string; username?: string; nickname?: string; message: string }> => {
      if (isTestEnvironment) {
        return {
          success: true,
          token: 'test-wechat-token',
          role: 'user',
          username: 'wechat_user_001',
          nickname: '微信用户',
          message: '微信登录成功'
        };
      }
      const response = await request({
        url: '/auth/wechat',
        method: 'POST',
        data: params
      });
      const data = (response as any).data;
      return data.data;
    }
  },
  
  // 用户相关API
  user: {
    getInfo: async (): Promise<ApiResponse<{
      id: number;
      nickname: string;
      avatarUrl: string;
      gender: number;
      country: string;
      province: string;
      city: string;
      fitnessGoals: string;
      fitnessLevel: string;
      height: number;
      weight: number;
    }>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: {
            id: 1,
            nickname: '健身爱好者',
            avatarUrl: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=fitness%20user%20avatar%20profile%20picture&image_size=square',
            gender: 1,
            country: '中国',
            province: '北京',
            city: '北京',
            fitnessGoals: '增肌',
            fitnessLevel: '中级',
            height: 1.75,
            weight: 70
          }
        };
      }
      const response = await request({
        url: '/user/info',
        method: 'GET'
      });
      return (response as any).data;
    },
    getStats: async (): Promise<ApiResponse<{
      completedWorkouts: number;
      caloriesBurned: number;
      totalDuration: number;
      completionRate: number;
      level: number;
      levelProgress: number;
    }>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: {
            completedWorkouts: 12,
            caloriesBurned: 2543,
            totalDuration: 36.5,
            completionRate: 85,
            level: 3,
            levelProgress: 65
          }
        };
      }
      const response = await request({
        url: '/user/stats',
        method: 'GET'
      });
      return (response as any).data;
    }
  },
  
  // 分类相关API
  categories: {
    getList: async (): Promise<ApiResponse<Category[]>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: mockData.categories
        };
      }
      const response = await request({
        url: '/movement/categories',
        method: 'GET'
      });
      const data = (response as any).data;
      return {
        code: data.success ? 200 : 500,
        message: data.success ? 'success' : 'failed',
        data: data.data
      };
    }
  },
  
  // 动作相关API
  exercises: {
    getList: async (categoryId?: number): Promise<ApiResponse<ExerciseItem[]>> => {
      if (isTestEnvironment) {
        const exercises = categoryId 
          ? mockData.exercises.filter(e => e.categoryId === categoryId)
          : mockData.exercises;
        return {
          code: 200,
          message: 'success',
          data: exercises
        };
      }
      const params = categoryId ? { categoryId } : {};
      const response = await request({
        url: '/movement/exercises',
        method: 'GET',
        data: params
      });
      const data = (response as any).data;
      return {
        code: data.success ? 200 : 500,
        message: data.success ? 'success' : 'failed',
        data: data.data
      };
    }
  },
  
  // 模板相关API
  templates: {
    getList: async (): Promise<ApiResponse<Template[]>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: mockData.templates
        };
      }
      const response = await request({
        url: '/templates/list',
        method: 'GET'
      });
      return (response as any).data;
    },
    
    getUser: async (): Promise<ApiResponse<Template[]>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: mockData.templates
        };
      }
      const response = await request({
        url: '/templates/user',
        method: 'GET'
      });
      return (response as any).data;
    },
    
    getDetail: async (id: number): Promise<ApiResponse<Template>> => {
      if (isTestEnvironment) {
        const template = mockData.templates.find(t => t.id === id);
        return {
          code: 200,
          message: 'success',
          data: template || mockData.templates[0]
        };
      }
      const response = await request({
        url: `/templates/detail/${id}`,
        method: 'GET'
      });
      return (response as any).data;
    },
    
    getDays: async (templateId: number): Promise<ApiResponse<TemplateDay[]>> => {
      if (isTestEnvironment) {
        const days = mockData.templateDays.filter(d => d.templateId === templateId);
        return {
          code: 200,
          message: 'success',
          data: days
        };
      }
      const response = await request({
        url: `/templates/${templateId}/days`,
        method: 'GET'
      });
      return (response as any).data;
    },
    
    getExercises: async (templateDayId: number): Promise<ApiResponse<Exercise[]>> => {
      if (isTestEnvironment) {
        const exercises = mockData.templateExercises.filter(e => e.templateDayId === templateDayId);
        return {
          code: 200,
          message: 'success',
          data: exercises
        };
      }
      const response = await request({
        url: `/templates/days/${templateDayId}/exercises`,
        method: 'GET'
      });
      return (response as any).data;
    },
    
    createPlan: async (data: {
      templateId: number;
      name: string;
      goal: string;
      difficulty: string;
      durationWeeks: number;
      startDate: string;
    }): Promise<ApiResponse<number>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: 999
        };
      }
      const response = await request({
        url: '/plans/template',
        method: 'POST',
        data
      });
      return (response as any).data;
    }
  },
  
  // 计划相关API
  plans: {
    getList: async (): Promise<ApiResponse<FitnessPlan[]>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: mockData.plans
        };
      }
      const response = await request({
        url: '/plans/user',
        method: 'GET'
      });
      return (response as any).data;
    },
    
    updateStatus: async (planId: number, status: string): Promise<ApiResponse<void>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: null
        };
      }
      const response = await request({
        url: `/plans/status/${planId}?status=${status}`,
        method: 'PUT'
      });
      return (response as any).data;
    },
    
    createCustom: async (data: any): Promise<ApiResponse<number>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: 999
        };
      }
      const response = await request({
        url: '/plans/custom',
        method: 'POST',
        data
      });
      return (response as any).data;
    },
    
    delete: async (planId: number): Promise<ApiResponse<void>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: null
        };
      }
      const response = await request({
        url: `/plans/${planId}`,
        method: 'DELETE'
      });
      return (response as any).data;
    }
  },
  
  // 训练计划安排API
  workoutSchedules: {
    getByPlan: async (planId: number): Promise<ApiResponse<WorkoutSchedule[]>> => {
      if (isTestEnvironment) {
        const schedules = mockData.workoutSchedules.filter(s => s.planId === planId);
        return {
          code: 200,
          message: 'success',
          data: schedules
        };
      }
      const response = await request({
        url: `/workout-schedules/plan/${planId}`,
        method: 'GET'
      });
      return (response as any).data;
    },
    
    getExercises: async (scheduleId: number): Promise<ApiResponse<ScheduleExercise[]>> => {
      if (isTestEnvironment) {
        const exercises = mockData.scheduleExercises.filter(e => e.scheduleId === scheduleId);
        return {
          code: 200,
          message: 'success',
          data: exercises
        };
      }
      const response = await request({
        url: `/workout-schedules/${scheduleId}/exercises`,
        method: 'GET'
      });
      return (response as any).data;
    },
    
    create: async (data: any): Promise<ApiResponse<number>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: 999
        };
      }
      const response = await request({
        url: '/workout-schedules',
        method: 'POST',
        data
      });
      return (response as any).data;
    }
  },
  
  // 训练记录相关API
  workout: {
    getToday: async (date?: string, planId?: number): Promise<ApiResponse<{
      records: any[];
      schedule: any | null;
      exercises: ScheduleExercise[];
    }>> => {
      if (isTestEnvironment) {
        let schedule: any | null = mockData.workoutSchedules[0] || null;
        if (planId) {
          schedule = mockData.workoutSchedules.find(s => s.planId === planId) || null;
        }
        const exercises = mockData.scheduleExercises.filter(e => e.scheduleId === schedule?.id);
        return {
          code: 200,
          message: 'success',
          data: { 
            records: [], 
            schedule, 
            exercises 
          }
        };
      }
      const params: any = {};
      if (date) params.date = date;
      if (planId) params.planId = planId;
      const response = await request({
        url: '/workout/today',
        method: 'GET',
        data: params
      });
      return (response as any).data;
    },
    
    getRecent: async (limit: number = 10): Promise<ApiResponse<{records: WorkoutRecord[], total: number}>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: {
            records: mockData.workoutRecords,
            total: mockData.workoutRecords.length
          }
        };
      }
      const response = await request({
        url: `/workout/recent?limit=${limit}`,
        method: 'GET'
      });
      return (response as any).data;
    },
    
    getSessions: async (params: {
      page?: number;
      pageSize?: number;
      planId?: number;
      date?: string;
    } = {}): Promise<ApiResponse<{
      records: WorkoutRecord[];
      totalPages: number;
      total: number;
    }>> => {
      if (isTestEnvironment) {
        const { page = 1, pageSize = 10, planId, date } = params;
        let filteredRecords = [...mockData.workoutRecords];
        
        if (planId) {
          filteredRecords = filteredRecords.filter(r => r.planId === planId);
        }
        
        if (date) {
          filteredRecords = filteredRecords.filter(r => r.date === date);
        }
        
        const start = (page - 1) * pageSize;
        const end = start + pageSize;
        const paginatedRecords = filteredRecords.slice(start, end);
        
        return {
          code: 200,
          message: 'success',
          data: {
            records: paginatedRecords,
            totalPages: Math.ceil(filteredRecords.length / pageSize),
            total: filteredRecords.length
          }
        };
      }
      const response = await request({
        url: '/workout/records',
        method: 'GET',
        data: params
      });
      return (response as any).data;
    },
    
    createRecord: async (record: Partial<WorkoutRecord>): Promise<ApiResponse<WorkoutRecord>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: {
            id: 999,
            userId: 1,
            planId: record.planId || 1,
            scheduleId: 1,
            scheduleExerciseId: 1,
            exerciseId: record.exerciseId || 1,
            exerciseName: record.exerciseName || '训练动作',
            date: record.date || new Date().toISOString().split('T')[0],
            setsCompleted: record.setsCompleted || '[3, 3, 3]',
            weight: record.weight || 0,
            duration: record.duration || 600,
            notes: record.notes || '',
            completed: true
          }
        };
      }
      const response = await request({
        url: '/workout/record',
        method: 'POST',
        data: record
      });
      return (response as any).data;
    },
    
    createBatch: async (records: Partial<WorkoutRecord>[]): Promise<ApiResponse<void>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: null
        };
      }
      const response = await request({
        url: '/workout/batch',
        method: 'POST',
        data: records
      });
      return (response as any).data;
    },
    
    updateRecord: async (id: number, record: Partial<WorkoutRecord>): Promise<ApiResponse<WorkoutRecord>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: {
            id,
            userId: 1,
            planId: record.planId || 1,
            scheduleId: 1,
            scheduleExerciseId: 1,
            exerciseId: record.exerciseId || 1,
            exerciseName: record.exerciseName || '训练动作',
            date: record.date || new Date().toISOString().split('T')[0],
            setsCompleted: record.setsCompleted || '[3, 3, 3]',
            weight: record.weight || 0,
            duration: record.duration || 600,
            notes: record.notes || '',
            completed: true
          }
        };
      }
      const response = await request({
        url: `/workout/record/${id}`,
        method: 'PUT',
        data: record
      });
      return (response as any).data;
    }
  },
  
  // 文件上传相关API
  file: {
    upload: async (file: any): Promise<ApiResponse<string>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=fitness%20training%20workout%20plan%20background&image_size=landscape_16_9'
        };
      }
      return new Promise((resolve, reject) => {
        if (isUniApp) {
          // uni-app 环境
          uni.uploadFile({
            url: BASE_URL + '/file/upload',
            filePath: file.tempFilePaths[0],
            name: 'file',
            success: (uploadFileRes) => {
              const data = JSON.parse(uploadFileRes.data);
              resolve(data);
            },
            fail: (error) => {
              toast.error('文件上传失败');
              reject(error);
            }
          });
        } else {
          // Web 环境
          const formData = new FormData();
          formData.append('file', file);
          
          fetch(BASE_URL + '/file/upload', {
            method: 'POST',
            body: formData,
            credentials: 'include'
          })
            .then(response => response.json())
            .then(data => {
              resolve(data);
            })
            .catch(error => {
              toast.error('文件上传失败');
              reject(error);
            });
        }
      });
    }
  },
  
  // 配置相关API
  config: {
    getAppConfig: async (): Promise<ApiResponse<{ [key: string]: any }>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: {
            app_name: '健身助手',
            version: '1.0.0',
            maintenance_mode: false,
            max_workout_records_per_user: 1000,
            default_plan_duration: 8,
            ai_enabled: true,
            music_enabled: true,
            social_share_enabled: true,
            max_file_size_mb: 10,
            workout_reminder_enabled: true
          }
        };
      }
      const response = await request({
        url: '/config',
        method: 'GET'
      });
      return (response as any).data;
    }
  },
  
  // 管理员相关API
  admin: {
    // 分类管理
    categories: {
      getList: async (): Promise<any[]> => {
        if (isTestEnvironment) {
          return mockData.categories.map(cat => ({
            id: cat.id,
            categoryName: cat.name,
            description: '',
            icon: cat.icon,
            sortOrder: cat.id,
            parentId: 0
          }));
        }
        const response = await request({
          url: '/admin/categories',
          method: 'GET'
        });
        return (response as any).data;
      },
      create: async (data: any): Promise<void> => {
        if (isTestEnvironment) {
          return;
        }
        await request({
          url: '/admin/categories',
          method: 'POST',
          data
        });
      },
      update: async (id: number, data: any): Promise<void> => {
        if (isTestEnvironment) {
          return;
        }
        await request({
          url: `/admin/categories/${id}`,
          method: 'PUT',
          data
        });
      },
      delete: async (id: number): Promise<void> => {
        if (isTestEnvironment) {
          return;
        }
        await request({
          url: `/admin/categories/${id}`,
          method: 'DELETE'
        });
      }
    },
    
    // 用户管理
    users: {
      getList: async (): Promise<any[]> => {
        if (isTestEnvironment) {
          return [
            { id: 1, username: 'user1', email: 'user1@example.com', role: 'user' },
            { id: 2, username: 'admin', email: 'admin@example.com', role: 'admin' }
          ];
        }
        const response = await request({
          url: '/admin/users',
          method: 'GET'
        });
        return (response as any).data;
      },
      setAdmin: async (userId: number): Promise<void> => {
        if (isTestEnvironment) {
          return;
        }
        await request({
          url: '/admin/users/set-admin',
          method: 'POST',
          data: { user_id: userId }
        });
      }
    },
    
    // 动作管理
    exercises: {
      getList: async (): Promise<any[]> => {
        if (isTestEnvironment) {
          return mockData.exercises.map(ex => ({
            id: ex.id,
            categoryId: ex.categoryId,
            name: ex.name,
            description: ex.description,
            defaultSets: ex.defaultSets,
            defaultReps: ex.defaultReps,
            defaultDuration: ex.defaultDuration
          }));
        }
        const response = await request({
          url: '/admin/exercises',
          method: 'GET'
        });
        return (response as any).data;
      },
      create: async (data: any): Promise<void> => {
        if (isTestEnvironment) {
          return;
        }
        await request({
          url: '/admin/exercises',
          method: 'POST',
          data
        });
      },
      update: async (id: number, data: any): Promise<void> => {
        if (isTestEnvironment) {
          return;
        }
        await request({
          url: `/admin/exercises/${id}`,
          method: 'PUT',
          data
        });
      },
      delete: async (id: number): Promise<void> => {
        if (isTestEnvironment) {
          return;
        }
        await request({
          url: `/admin/exercises/${id}`,
          method: 'DELETE'
        });
      }
    }
  }
};

export default api;
