import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080',
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
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/auth/login';
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
}

export const api = {
  // 模板相关API
  templates: {
    getList: async (): Promise<ApiResponse<Template[]>> => {
      const response = await apiClient.get<ApiResponse<Template[]>>('/api/templates/list');
      return response.data;
    },
    
    getDetail: async (id: number): Promise<ApiResponse<Template>> => {
      const response = await apiClient.get<ApiResponse<Template>>(`/api/templates/detail/${id}`);
      return response.data;
    },
    
    getDays: async (templateId: number): Promise<ApiResponse<TemplateDay[]>> => {
      const response = await apiClient.get<ApiResponse<TemplateDay[]>>(`/api/templates/${templateId}/days`);
      return response.data;
    },
    
    getExercises: async (templateDayId: number): Promise<ApiResponse<Exercise[]>> => {
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
      const response = await apiClient.post<ApiResponse<number>>('/api/plans/template', data);
      return response.data;
    }
  },
  
  // 计划相关API
  plans: {
    getList: async (): Promise<ApiResponse<FitnessPlan[]>> => {
      const response = await apiClient.get<ApiResponse<FitnessPlan[]>>('/api/plans/user');
      return response.data;
    },
    
    updateStatus: async (planId: number, status: string): Promise<ApiResponse<void>> => {
      const response = await apiClient.put<ApiResponse<void>>(`/api/plans/status/${planId}?status=${status}`);
      return response.data;
    }
  },
  
  // 训练记录相关API
  workout: {
    getToday: async (date?: string): Promise<ApiResponse<WorkoutRecord>> => {
      const params = date ? { date } : {};
      const response = await apiClient.get<ApiResponse<WorkoutRecord>>('/api/workout/today', { params });
      return response.data;
    },
    
    getRecent: async (limit: number = 10): Promise<ApiResponse<WorkoutRecord[]>> => {
      const response = await apiClient.get<ApiResponse<WorkoutRecord[]>>(`/api/workout/recent?limit=${limit}`);
      return response.data;
    },
    
    createRecord: async (record: Partial<WorkoutRecord>): Promise<ApiResponse<WorkoutRecord>> => {
      const response = await apiClient.post<ApiResponse<WorkoutRecord>>('/api/workout/record', record);
      return response.data;
    },
    
    updateRecord: async (id: number, record: Partial<WorkoutRecord>): Promise<ApiResponse<WorkoutRecord>> => {
      const response = await apiClient.put<ApiResponse<WorkoutRecord>>(`/api/workout/record/${id}`, record);
      return response.data;
    }
  }
};

export default api;
