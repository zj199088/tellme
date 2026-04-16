import axios from 'axios';
import { isTestEnvironment, mockData } from './env';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
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
      if (!url.includes('/api/templates/list') && !url.includes('/api/categories/list') && !url.includes('/api/exercises/list')) {
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
      const response = await apiClient.get<ApiResponse<Category[]>>('/api/categories/list');
      return response.data;
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
      const response = await apiClient.get<ApiResponse<ExerciseItem[]>>('/api/exercises/list', { params });
      return response.data;
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
      const response = await apiClient.get<ApiResponse<Template[]>>('/api/templates/list');
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
      const response = await apiClient.get<ApiResponse<Template[]>>('/api/templates/user');
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
      const response = await apiClient.get<ApiResponse<Template>>(`/api/templates/detail/${id}`);
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
      const response = await apiClient.get<ApiResponse<TemplateDay[]>>(`/api/templates/${templateId}/days`);
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
      const response = await apiClient.get<ApiResponse<Exercise[]>>(`/api/templates/days/${templateDayId}/exercises`);
      return response.data;
    },
    
    createPlan: async (data: {
      templateId: number;
      name: string;
      goal: string;
      difficulty: string;
      durationWeeks: number;
    }): Promise<ApiResponse<number>> => {
      if (isTestEnvironment) {
        return {
          code: 200,
          message: 'success',
          data: 999
        };
      }
      const response = await apiClient.post<ApiResponse<number>>('/api/plans/template', data);
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
      const response = await apiClient.get<ApiResponse<FitnessPlan[]>>('/api/plans/user');
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
      const response = await apiClient.put<ApiResponse<void>>(`/api/plans/status/${planId}?status=${status}`);
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
      const response = await apiClient.post<ApiResponse<number>>('/api/plans/custom', data);
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
      const response = await apiClient.get<ApiResponse<WorkoutSchedule[]>>(`/api/workout-schedules/plan/${planId}`);
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
      const response = await apiClient.get<ApiResponse<ScheduleExercise[]>>(`/api/workout-schedules/${scheduleId}/exercises`);
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
      const response = await apiClient.post<ApiResponse<number>>('/api/workout-schedules', data);
      return response.data;
    }
  },
  
  // 训练记录相关API
  workout: {
    getToday: async (planId?: number, date?: string): Promise<ApiResponse<{
      schedule: WorkoutSchedule | null;
      exercises: ScheduleExercise[];
    }>> => {
      if (isTestEnvironment) {
        const schedule = mockData.workoutSchedules[0] || null;
        const exercises = mockData.scheduleExercises.filter(e => e.scheduleId === schedule?.id);
        return {
          code: 200,
          message: 'success',
          data: { schedule, exercises }
        };
      }
      const params = { planId, date };
      const response = await apiClient.get<ApiResponse<any>>('/api/workout/today', { params });
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
      const response = await apiClient.get<ApiResponse<{records: WorkoutRecord[], total: number}>>(`/api/workout/recent?limit=${limit}`);
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
      const response = await apiClient.get<ApiResponse<any>>('/api/workout/records', { params });
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
      const response = await apiClient.post<ApiResponse<WorkoutRecord>>('/api/workout/record', record);
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
      const response = await apiClient.post<ApiResponse<void>>('/api/workout/batch', records);
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
      const response = await apiClient.put<ApiResponse<WorkoutRecord>>(`/api/workout/record/${id}`, record);
      return response.data;
    }
  }
};

export default api;
