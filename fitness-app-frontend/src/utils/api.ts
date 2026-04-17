import axios from 'axios';
import { isTestEnvironment, mockData } from './env';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    // 只在需要认证的接口返回401时重定向到登录页面
    // 对于公共接口，允许继续执行
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      // 只有在尝试访问需要认证的接口时才重定向
      // 公共接口（如获取模板列表）不需要重定向
      const url = error.config?.url || '';
      if (!url.includes('/templates/list') && !url.includes('/movement/categories') && !url.includes('/movement/exercises')) {
        window.location.href = '/auth/login';
      }
    }
    return Promise.reject(error);
  }
);

export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
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
  createdBy: number;
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
  weight: number;
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
      const response = await apiClient.post<{ code: number; message: string; data: { success: boolean; token?: string; role?: string; message: string } }>('/auth/login', { username, password });
      return response.data.data;
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
      const response = await apiClient.post<{ code: number; message: string; data: { success: boolean; token?: string; role?: string; message: string } }>('/auth/admin/login', { username, password });
      return response.data.data;
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
      const response = await apiClient.post<{ code: number; message: string; data: { success: boolean; token?: string; role?: string; message: string } }>('/auth/register', { username, password, nickname });
      return response.data.data;
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
      const response = await apiClient.get<ApiResponse<any>>('/user/info');
      return response.data;
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
      const response = await apiClient.get<ApiResponse<any>>('/user/stats');
      return response.data;
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
      const response = await apiClient.get<any>('/movement/categories');
      return {
        code: response.success ? 200 : 500,
        message: response.success ? 'success' : 'failed',
        data: response.data
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
      const response = await apiClient.get<any>('/movement/exercises', { params });
      return {
        code: response.success ? 200 : 500,
        message: response.success ? 'success' : 'failed',
        data: response.data
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
      const response = await apiClient.get<ApiResponse<Template[]>>('/templates/list');
      return response.data;
    },
    
    getUser: async (): Promise<ApiResponse<Template[]>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: mockData.templates
        };
      }
      const response = await apiClient.get<ApiResponse<Template[]>>('/templates/user');
      return response.data;
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
      const response = await apiClient.get<ApiResponse<Template>>(`/templates/detail/${id}`);
      return response.data;
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
      const response = await apiClient.get<ApiResponse<TemplateDay[]>>(`/templates/${templateId}/days`);
      return response.data;
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
      const response = await apiClient.get<ApiResponse<Exercise[]>>(`/templates/days/${templateDayId}/exercises`);
      return response.data;
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
      const response = await apiClient.post<ApiResponse<number>>('/plans/template', data);
      return response.data;
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
      const response = await apiClient.get<ApiResponse<FitnessPlan[]>>('/plans/user');
      return response.data;
    },
    
    updateStatus: async (planId: number, status: string): Promise<ApiResponse<void>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: null
        };
      }
      const response = await apiClient.put<ApiResponse<void>>(`/plans/status/${planId}?status=${status}`);
      return response.data;
    },
    
    createCustom: async (data: any): Promise<ApiResponse<number>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: 999
        };
      }
      const response = await apiClient.post<ApiResponse<number>>('/plans/custom', data);
      return response.data;
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
      const response = await apiClient.get<ApiResponse<WorkoutSchedule[]>>(`/workout-schedules/plan/${planId}`);
      return response.data;
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
      const response = await apiClient.get<ApiResponse<ScheduleExercise[]>>(`/workout-schedules/${scheduleId}/exercises`);
      return response.data;
    },
    
    create: async (data: any): Promise<ApiResponse<number>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: 999
        };
      }
      const response = await apiClient.post<ApiResponse<number>>('/workout-schedules', data);
      return response.data;
    }
  },
  
  // 训练记录相关API
  workout: {
    getToday: async (date?: string): Promise<ApiResponse<{
      records: any[];
      schedule: WorkoutSchedule | null;
      exercises: ScheduleExercise[];
    }>> => {
      if (isTestEnvironment) {
        const schedule = mockData.workoutSchedules[0] || null;
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
      const params = date ? { date } : {};
      const response = await apiClient.get<ApiResponse<any>>('/workout/today', { params });
      return response.data;
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
      const response = await apiClient.get<ApiResponse<{records: WorkoutRecord[], total: number}>>(`/workout/recent?limit=${limit}`);
      return response.data;
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
      const response = await apiClient.get<ApiResponse<any>>('/workout/records', { params });
      return response.data;
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
      const response = await apiClient.post<ApiResponse<WorkoutRecord>>('/workout/record', record);
      return response.data;
    },
    
    createBatch: async (records: Partial<WorkoutRecord>[]): Promise<ApiResponse<void>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: null
        };
      }
      const response = await apiClient.post<ApiResponse<void>>('/workout/batch', records);
      return response.data;
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
      const response = await apiClient.put<ApiResponse<WorkoutRecord>>(`/workout/record/${id}`, record);
      return response.data;
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
        const response = await apiClient.get<any[]>('/admin/categories');
        return response.data;
      },
      create: async (data: any): Promise<void> => {
        if (isTestEnvironment) {
          return;
        }
        await apiClient.post('/admin/categories', data);
      },
      update: async (id: number, data: any): Promise<void> => {
        if (isTestEnvironment) {
          return;
        }
        await apiClient.put(`/admin/categories/${id}`, data);
      },
      delete: async (id: number): Promise<void> => {
        if (isTestEnvironment) {
          return;
        }
        await apiClient.delete(`/admin/categories/${id}`);
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
        const response = await apiClient.get<any[]>('/admin/users');
        return response.data;
      },
      setAdmin: async (userId: number): Promise<void> => {
        if (isTestEnvironment) {
          return;
        }
        await apiClient.post('/admin/users/set-admin', { user_id: userId });
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
        const response = await apiClient.get<any[]>('/admin/exercises');
        return response.data;
      },
      create: async (data: any): Promise<void> => {
        if (isTestEnvironment) {
          return;
        }
        await apiClient.post('/admin/exercises', data);
      },
      update: async (id: number, data: any): Promise<void> => {
        if (isTestEnvironment) {
          return;
        }
        await apiClient.put(`/admin/exercises/${id}`, data);
      },
      delete: async (id: number): Promise<void> => {
        if (isTestEnvironment) {
          return;
        }
        await apiClient.delete(`/admin/exercises/${id}`);
      }
    }
  }
};

export default api;
