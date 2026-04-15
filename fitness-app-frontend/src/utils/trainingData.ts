// 训练数据管理模块

// 训练项目类型
export interface Exercise {
  id: string;
  name: string;
  sets: number;
  reps: number;
  weight?: number;
  completed: boolean;
}

// 训练计划类型
export interface TrainingPlan {
  id: string;
  name: string;
  description: string;
  exercises: Exercise[];
  startDate: string;
  endDate: string;
  currentDay: number;
  totalDays: number;
  isActive: boolean;
}

// 训练记录类型
export interface TrainingRecord {
  id: string;
  date: string;
  planId: string;
  planName: string;
  exercises: Exercise[];
  completed: boolean;
}

// 存储键名
const STORAGE_KEYS = {
  PLANS: 'fitness_plans',
  RECORDS: 'fitness_records',
};

// 获取所有训练计划
export const getPlans = (): TrainingPlan[] => {
  const plans = localStorage.getItem(STORAGE_KEYS.PLANS);
  return plans ? JSON.parse(plans) : [];
};

// 保存训练计划
export const savePlans = (plans: TrainingPlan[]): void => {
  localStorage.setItem(STORAGE_KEYS.PLANS, JSON.stringify(plans));
};

// 获取所有训练记录
export const getRecords = (): TrainingRecord[] => {
  const records = localStorage.getItem(STORAGE_KEYS.RECORDS);
  return records ? JSON.parse(records) : [];
};

// 保存训练记录
export const saveRecords = (records: TrainingRecord[]): void => {
  localStorage.setItem(STORAGE_KEYS.RECORDS, JSON.stringify(records));
};

// 获取当前活跃计划
export const getActivePlan = (): TrainingPlan | null => {
  const plans = getPlans();
  return plans.find(plan => plan.isActive) || null;
};

// 获取今日训练记录
export const getTodayRecord = (): TrainingRecord | null => {
  const records = getRecords();
  const today = new Date().toISOString().split('T')[0];
  return records.find(record => record.date === today) || null;
};

// 创建训练记录
export const createRecord = (plan: TrainingPlan): TrainingRecord => {
  const record: TrainingRecord = {
    id: `record_${Date.now()}`,
    date: new Date().toISOString().split('T')[0],
    planId: plan.id,
    planName: plan.name,
    exercises: JSON.parse(JSON.stringify(plan.exercises)),
    completed: false,
  };
  
  const records = getRecords();
  records.push(record);
  saveRecords(records);
  
  return record;
};

// 更新训练记录
export const updateRecord = (record: TrainingRecord): void => {
  const records = getRecords();
  const index = records.findIndex(r => r.id === record.id);
  if (index !== -1) {
    records[index] = record;
    saveRecords(records);
  }
};

// 更新计划进度
export const updatePlanProgress = (planId: string): void => {
  const plans = getPlans();
  const plan = plans.find(p => p.id === planId);
  if (plan) {
    plan.currentDay = Math.min(plan.currentDay + 1, plan.totalDays);
    if (plan.currentDay >= plan.totalDays) {
      plan.isActive = false;
    }
    savePlans(plans);
  }
};

// 初始化示例数据（如果没有数据）
export const initializeSampleData = (): void => {
  const plans = getPlans();
  if (plans.length === 0) {
    const samplePlan: TrainingPlan = {
      id: 'plan_1',
      name: '初学者健身计划',
      description: '适合初学者的4周健身计划',
      exercises: [
        {
          id: 'ex_1',
          name: '俯卧撑',
          sets: 3,
          reps: 10,
          completed: false,
        },
        {
          id: 'ex_2',
          name: '深蹲',
          sets: 3,
          reps: 12,
          completed: false,
        },
        {
          id: 'ex_3',
          name: '平板支撑',
          sets: 3,
          reps: 30, // 秒
          completed: false,
        },
        {
          id: 'ex_4',
          name: '仰卧起坐',
          sets: 3,
          reps: 15,
          completed: false,
        },
      ],
      startDate: new Date().toISOString().split('T')[0],
      endDate: new Date(Date.now() + 28 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
      currentDay: 1,
      totalDays: 28,
      isActive: true,
    };
    
    savePlans([samplePlan]);
  }
};