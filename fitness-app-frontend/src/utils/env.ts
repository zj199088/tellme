// 环境配置 - 强制在开发环境使用模拟数据
const isTestEnvironment = true;

// 生成过去30天的日期（包含时间）
const generatePastDates = (days: number): string[] => {
  const dates: string[] = [];
  for (let i = 0; i < days; i++) {
    const date = new Date(Date.now() - i * 24 * 60 * 60 * 1000);
    // 随机生成时间部分
    const hours = Math.floor(Math.random() * 12) + 8; // 8-19点
    const minutes = Math.floor(Math.random() * 60);
    const seconds = Math.floor(Math.random() * 60);
    date.setHours(hours, minutes, seconds, 0);
    dates.push(date.toISOString());
  }
  return dates;
};

const pastDates = generatePastDates(30);

// 模拟数据
const mockData = {
  // 模拟运动分类数据
  categories: [
    { id: 1, name: '胸部', icon: '💪' },
    { id: 2, name: '背部', icon: '🏋️' },
    { id: 3, name: '腿部', icon: '🦵' },
    { id: 4, name: '肩部', icon: '💨' },
    { id: 5, name: '手臂', icon: '⚡' },
    { id: 6, name: '核心', icon: '🎯' },
    { id: 7, name: '有氧', icon: '🏃' },
    { id: 8, name: '拉伸', icon: '🧘' }
  ],
  
  // 模拟运动动作数据
  exercises: [
    { id: 1, categoryId: 1, name: '俯卧撑', description: '基础胸部训练', defaultSets: 3, defaultReps: '12-15', defaultDuration: '' },
    { id: 2, categoryId: 1, name: '平板哑铃飞鸟', description: '胸部肌肉线条训练', defaultSets: 3, defaultReps: '10-12', defaultDuration: '' },
    { id: 3, categoryId: 2, name: '引体向上', description: '背部宽度训练', defaultSets: 3, defaultReps: '8-10', defaultDuration: '' },
    { id: 4, categoryId: 2, name: '哑铃划船', description: '背部厚度训练', defaultSets: 3, defaultReps: '12-15', defaultDuration: '' },
    { id: 5, categoryId: 3, name: '深蹲', description: '腿部力量训练', defaultSets: 4, defaultReps: '10-12', defaultDuration: '' },
    { id: 6, categoryId: 3, name: '箭步蹲', description: '单腿力量训练', defaultSets: 3, defaultReps: '10-12', defaultDuration: '' },
    { id: 7, categoryId: 3, name: '臀桥', description: '臀部训练', defaultSets: 3, defaultReps: '15-20', defaultDuration: '' },
    { id: 8, categoryId: 4, name: '哑铃推举', description: '肩部力量训练', defaultSets: 3, defaultReps: '10-12', defaultDuration: '' },
    { id: 9, categoryId: 4, name: '侧平举', description: '肩部中束训练', defaultSets: 3, defaultReps: '12-15', defaultDuration: '' },
    { id: 10, categoryId: 5, name: '二头弯举', description: '肱二头肌训练', defaultSets: 3, defaultReps: '12-15', defaultDuration: '' },
    { id: 11, categoryId: 5, name: '三头下压', description: '肱三头肌训练', defaultSets: 3, defaultReps: '12-15', defaultDuration: '' },
    { id: 12, categoryId: 6, name: '卷腹', description: '上腹部训练', defaultSets: 3, defaultReps: '15-20', defaultDuration: '' },
    { id: 13, categoryId: 6, name: '平板支撑', description: '核心力量训练', defaultSets: 3, defaultReps: '', defaultDuration: '30-45秒' },
    { id: 14, categoryId: 7, name: '开合跳', description: '全身有氧训练', defaultSets: 4, defaultReps: '30', defaultDuration: '' },
    { id: 15, categoryId: 7, name: '登山者', description: '核心有氧训练', defaultSets: 3, defaultReps: '40', defaultDuration: '' },
    { id: 16, categoryId: 8, name: '全身拉伸', description: '运动后拉伸', defaultSets: 1, defaultReps: '', defaultDuration: '10分钟' }
  ],
  
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
      exerciseId: 12,
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
      exerciseId: 13,
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
      exerciseId: 5,
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
      exerciseId: 6,
      exerciseName: '箭步蹲',
      sets: 3,
      reps: '10-12',
      duration: '',
      note: '每侧',
      sortOrder: 5
    },
    {
      id: 6,
      templateDayId: 1,
      exerciseId: 7,
      exerciseName: '臀桥',
      sets: 3,
      reps: '15-20',
      duration: '',
      note: '',
      sortOrder: 6
    },
    {
      id: 7,
      templateDayId: 2,
      exerciseId: 14,
      exerciseName: '开合跳',
      sets: 4,
      reps: '30',
      duration: '',
      note: '',
      sortOrder: 1
    },
    {
      id: 8,
      templateDayId: 2,
      exerciseId: 15,
      exerciseName: '登山者',
      sets: 3,
      reps: '40',
      duration: '',
      note: '',
      sortOrder: 2
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
  
  // 模拟训练计划安排
  workoutSchedules: [
    {
      id: 1,
      planId: 1,
      dayOfWeek: 1,
      isRestDay: 0,
      estimatedDuration: 45,
      date: pastDates[0]
    },
    {
      id: 2,
      planId: 1,
      dayOfWeek: 2,
      isRestDay: 0,
      estimatedDuration: 35,
      date: pastDates[1]
    }
  ],
  
  // 模拟训练计划动作
  scheduleExercises: [
    { id: 1, scheduleId: 1, exerciseId: 1, exerciseName: '俯卧撑', sets: 3, reps: '10-15', duration: '', weight: 0, sortOrder: 1 },
    { id: 2, scheduleId: 1, exerciseId: 12, exerciseName: '卷腹', sets: 3, reps: '15-20', duration: '', weight: 0, sortOrder: 2 },
    { id: 3, scheduleId: 1, exerciseId: 13, exerciseName: '平板支撑', sets: 3, reps: '', duration: '30-45秒', weight: 0, sortOrder: 3 }
  ],
  
  // 模拟完整的训练记录数据
  workoutRecords: [
    {
      id: 1,
      userId: 1,
      planId: 1,
      plan_name: '我的基础健身计划',
      scheduleId: 1,
      scheduleExerciseId: 1,
      exerciseId: 1,
      exerciseName: '俯卧撑',
      date: pastDates[0],
      setsCompleted: '[3, 3, 3]',
      weight: 0,
      duration: 600,
      notes: '感觉良好',
      completed: true
    },
    {
      id: 2,
      userId: 1,
      planId: 1,
      plan_name: '我的基础健身计划',
      scheduleId: 1,
      scheduleExerciseId: 2,
      exerciseId: 12,
      exerciseName: '卷腹',
      date: pastDates[0],
      setsCompleted: '[3, 3, 3]',
      weight: 0,
      duration: 480,
      notes: '腹部有灼烧感',
      completed: true
    },
    {
      id: 3,
      userId: 1,
      planId: 1,
      plan_name: '我的基础健身计划',
      scheduleId: 1,
      scheduleExerciseId: 3,
      exerciseId: 13,
      exerciseName: '平板支撑',
      date: pastDates[0],
      setsCompleted: '[3, 3, 3]',
      weight: null,
      duration: 540,
      notes: '坚持下来了',
      completed: true
    },
    {
      id: 4,
      userId: 1,
      planId: 1,
      plan_name: '我的基础健身计划',
      scheduleId: 2,
      scheduleExerciseId: 4,
      exerciseId: 14,
      exerciseName: '哑铃飞鸟',
      date: pastDates[1],
      setsCompleted: '[4, 4, 4, 4]',
      weight: 15,
      duration: 720,
      notes: '心率提升很快',
      completed: true
    },
    {
      id: 5,
      userId: 1,
      planId: 2,
      plan_name: '我的减脂计划',
      scheduleId: 3,
      scheduleExerciseId: 5,
      exerciseId: 5,
      exerciseName: '深蹲',
      date: pastDates[7],
      setsCompleted: '[3, 3, 2]',
      weight: 20,
      duration: 660,
      notes: '最后一组有点吃力',
      completed: true
    }
  ],
  
  // 模拟完整的训练记录（按日期分组）
  workoutSessions: [
    {
      id: 1,
      planId: 1,
      planName: '我的基础健身计划',
      date: pastDates[0],
      completed: true,
      duration: 1620,
      exercises: [
        { id: 1, name: '俯卧撑', sets: 3, reps: '10-15', completed: true },
        { id: 2, name: '卷腹', sets: 3, reps: '15-20', completed: true },
        { id: 3, name: '平板支撑', sets: 3, reps: '30-45秒', completed: true }
      ]
    },
    {
      id: 2,
      planId: 1,
      planName: '我的基础健身计划',
      date: pastDates[1],
      completed: true,
      duration: 720,
      exercises: [
        { id: 4, name: '开合跳', sets: 4, reps: '30', completed: true }
      ]
    },
    {
      id: 3,
      planId: 2,
      planName: '我的减脂计划',
      date: pastDates[7],
      completed: true,
      duration: 660,
      exercises: [
        { id: 5, name: '深蹲', sets: 3, reps: '10-12', completed: true }
      ]
    }
  ]
};

export { isTestEnvironment, mockData };
