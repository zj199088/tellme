// 环境配置
const isTestEnvironment = import.meta.env.MODE === 'development' || import.meta.env.VITE_IS_TEST === 'true';

// 模拟数据
const mockData = {
  // 模拟模板数据
  templates: [
    {
      id: 1,
      name: '基础健身计划（新手）',
      description: '适合健身新手，包含基础动作和循序渐进的训练计划，每周训练5天，休息2天',
      image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=fitness%20training%20workout%20template%20for%20beginners&image_size=landscape_16_9',
      difficulty: 'beginner',
      isPublic: 1,
      createdBy: null
    },
    {
      id: 2,
      name: '增肌计划（中级）',
      description: '针对有一定基础的训练者，以增肌为目标的训练计划，每周训练6天',
      image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=muscle%20building%20workout%20template%20for%20intermediate&image_size=landscape_16_9',
      difficulty: 'intermediate',
      isPublic: 1,
      createdBy: null
    },
    {
      id: 3,
      name: '减脂计划（新手）',
      description: '以减脂为目标的综合性训练计划，结合有氧和力量训练',
      image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=fat%20loss%20workout%20template%20for%20beginners&image_size=landscape_16_9',
      difficulty: 'beginner',
      isPublic: 1,
      createdBy: null
    }
  ],
  
  // 模拟模板训练日数据
  templateDays: [
    {
      id: 1,
      templateId: 1,
      dayOfWeek: 1,
      isRestDay: 0,
      restNote: '',
      estimatedDuration: 45
    },
    {
      id: 2,
      templateId: 1,
      dayOfWeek: 2,
      isRestDay: 0,
      restNote: '',
      estimatedDuration: 35
    },
    {
      id: 3,
      templateId: 1,
      dayOfWeek: 3,
      isRestDay: 1,
      restNote: '好好休息，让肌肉恢复',
      estimatedDuration: 0
    },
    {
      id: 4,
      templateId: 1,
      dayOfWeek: 4,
      isRestDay: 0,
      restNote: '',
      estimatedDuration: 45
    },
    {
      id: 5,
      templateId: 1,
      dayOfWeek: 5,
      isRestDay: 0,
      restNote: '',
      estimatedDuration: 35
    },
    {
      id: 6,
      templateId: 1,
      dayOfWeek: 6,
      isRestDay: 1,
      restNote: '可以做一些轻松的拉伸运动',
      estimatedDuration: 0
    },
    {
      id: 7,
      templateId: 1,
      dayOfWeek: 7,
      isRestDay: 0,
      restNote: '',
      estimatedDuration: 30
    }
  ],
  
  // 模拟模板训练动作数据
  templateExercises: [
    {
      id: 1,
      templateDayId: 1,
      exerciseId: 1,
      exerciseName: '俯卧撑',
      sets: 3,
      reps: '10-15',
      duration: '',
      note: '',
      sortOrder: 1
    },
    {
      id: 2,
      templateDayId: 1,
      exerciseId: 16,
      exerciseName: '卷腹',
      sets: 3,
      reps: '15-20',
      duration: '',
      note: '',
      sortOrder: 2
    },
    {
      id: 3,
      templateDayId: 1,
      exerciseId: 17,
      exerciseName: '平板支撑',
      sets: 3,
      reps: '',
      duration: '30-45秒',
      note: '',
      sortOrder: 3
    },
    {
      id: 4,
      templateDayId: 1,
      exerciseId: 7,
      exerciseName: '深蹲',
      sets: 3,
      reps: '12-15',
      duration: '',
      note: '',
      sortOrder: 4
    },
    {
      id: 5,
      templateDayId: 1,
      exerciseId: 22,
      exerciseName: '弓步蹲',
      sets: 3,
      reps: '10-12',
      duration: '',
      note: '每侧',
      sortOrder: 5
    },
    {
      id: 6,
      templateDayId: 1,
      exerciseId: 23,
      exerciseName: '臀桥',
      sets: 3,
      reps: '15-20',
      duration: '',
      note: '',
      sortOrder: 6
    }
  ],
  
  // 模拟用户计划数据
  plans: [
    {
      id: 1,
      userId: 1,
      templateId: 1,
      name: '我的基础健身计划',
      type: 'template',
      goal: '基础健身',
      difficulty: 'beginner',
      durationWeeks: 8,
      startDate: new Date().toISOString().split('T')[0],
      endDate: new Date(Date.now() + 8 * 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
      status: 'active',
      isShared: 0,
      sharedCode: '',
      lastWorkoutDate: new Date().toISOString().split('T')[0]
    },
    {
      id: 2,
      userId: 1,
      templateId: 3,
      name: '我的减脂计划',
      type: 'template',
      goal: '减脂',
      difficulty: 'beginner',
      durationWeeks: 12,
      startDate: new Date().toISOString().split('T')[0],
      endDate: new Date(Date.now() + 12 * 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
      status: 'paused',
      isShared: 0,
      sharedCode: '',
      lastWorkoutDate: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
    }
  ],
  
  // 模拟训练记录数据
  workoutRecords: [
    {
      id: 1,
      userId: 1,
      planId: 1,
      scheduleId: 1,
      scheduleExerciseId: 1,
      exerciseId: 1,
      exerciseName: '俯卧撑',
      date: new Date().toISOString().split('T')[0],
      setsCompleted: '[3, 3, 3]',
      weight: 0,
      duration: 600,
      notes: '感觉良好'
    },
    {
      id: 2,
      userId: 1,
      planId: 1,
      scheduleId: 1,
      scheduleExerciseId: 2,
      exerciseId: 16,
      exerciseName: '卷腹',
      date: new Date().toISOString().split('T')[0],
      setsCompleted: '[3, 3, 3]',
      weight: 0,
      duration: 480,
      notes: '腹部有灼烧感'
    }
  ]
};

export { isTestEnvironment, mockData };
