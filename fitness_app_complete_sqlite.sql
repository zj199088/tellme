-- 创建数据库（SQLite 自动创建文件）

-- 1. 用户表
CREATE TABLE IF NOT EXISTS users (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  open_id VARCHAR(64) UNIQUE,
  nickname VARCHAR(100),
  avatar_url VARCHAR(500),
  gender INTEGER,
  country VARCHAR(50),
  province VARCHAR(50),
  city VARCHAR(50),
  role TEXT DEFAULT 'user',
  username VARCHAR(50) UNIQUE,
  password VARCHAR(100),
  fitness_goals TEXT,
  fitness_level TEXT DEFAULT 'beginner',
  height REAL,
  weight REAL,
  is_deleted INTEGER DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. 用户设备表
CREATE TABLE IF NOT EXISTS user_devices (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id INTEGER NOT NULL,
  device_type TEXT NOT NULL,
  device_token VARCHAR(500),
  device_id VARCHAR(255),
  app_version VARCHAR(20),
  last_login_at TIMESTAMP NULL,
  is_active INTEGER DEFAULT 1,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_deleted INTEGER DEFAULT 0,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. 应用配置表
CREATE TABLE IF NOT EXISTS app_configs (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  config_key VARCHAR(100) UNIQUE NOT NULL,
  config_value TEXT,
  config_type TEXT DEFAULT 'string',
  description VARCHAR(255),
  is_active INTEGER DEFAULT 1,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_deleted INTEGER DEFAULT 0
);

-- 4. 运动分类表
CREATE TABLE IF NOT EXISTS movement_categories (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  category_name VARCHAR(50) NOT NULL,
  description TEXT,
  icon VARCHAR(100),
  sort_order INTEGER DEFAULT 0,
  parent_id INTEGER DEFAULT 0,
  is_active INTEGER DEFAULT 1,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_deleted INTEGER DEFAULT 0
);

-- 5. 运动动作表
CREATE TABLE IF NOT EXISTS movement_exercises (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  exercise_name VARCHAR(100) NOT NULL,
  en_name VARCHAR(100),
  category_id INTEGER NOT NULL,
  difficulty_level TEXT DEFAULT 'beginner',
  target_muscle VARCHAR(200),
  equipment_needed VARCHAR(200),
  description TEXT,
  video_url VARCHAR(500),
  image_url VARCHAR(500),
  calories_per_hour REAL,
  is_popular INTEGER DEFAULT 0,
  is_bodyweight INTEGER DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_deleted INTEGER DEFAULT 0,
  FOREIGN KEY (category_id) REFERENCES movement_categories(id) ON DELETE CASCADE
);

-- 6. 健身模板表
CREATE TABLE IF NOT EXISTS templates (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name VARCHAR(100) NOT NULL,
  description TEXT NOT NULL,
  image VARCHAR(500),
  difficulty TEXT DEFAULT 'beginner',
  is_public INTEGER DEFAULT 1,
  created_by INTEGER,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_deleted INTEGER DEFAULT 0,
  FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 7. 模板训练日表
CREATE TABLE IF NOT EXISTS template_days (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  template_id INTEGER NOT NULL,
  day_of_week INTEGER NOT NULL,
  is_rest_day INTEGER DEFAULT 0,
  rest_note VARCHAR(200),
  estimated_duration INTEGER,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_deleted INTEGER DEFAULT 0,
  FOREIGN KEY (template_id) REFERENCES templates(id) ON DELETE CASCADE
);

-- 8. 模板动作关联表
CREATE TABLE IF NOT EXISTS template_exercises (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  template_day_id INTEGER NOT NULL,
  exercise_id INTEGER,
  exercise_name VARCHAR(100),
  sets INTEGER NOT NULL,
  reps VARCHAR(20),
  duration VARCHAR(20),
  note VARCHAR(200),
  sort_order INTEGER DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_deleted INTEGER DEFAULT 0,
  FOREIGN KEY (template_day_id) REFERENCES template_days(id) ON DELETE CASCADE,
  FOREIGN KEY (exercise_id) REFERENCES movement_exercises(id) ON DELETE SET NULL
);

-- 9. 健身计划表
CREATE TABLE IF NOT EXISTS fitness_plans (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id INTEGER NOT NULL,
  template_id INTEGER,
  name VARCHAR(100) NOT NULL,
  type TEXT NOT NULL,
  goal VARCHAR(50) NOT NULL,
  difficulty TEXT DEFAULT 'beginner',
  duration_weeks INTEGER NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  status TEXT DEFAULT 'active',
  is_shared INTEGER DEFAULT 0,
  shared_code VARCHAR(32),
  last_workout_date DATE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_deleted INTEGER DEFAULT 0,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (template_id) REFERENCES templates(id) ON DELETE SET NULL
);

-- 10. 训练日程表
CREATE TABLE IF NOT EXISTS workout_schedules (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  plan_id INTEGER NOT NULL,
  week_num INTEGER NOT NULL,
  day_of_week INTEGER NOT NULL,
  is_rest_day INTEGER DEFAULT 0,
  rest_note VARCHAR(200),
  estimated_duration INTEGER,
  template_day_id INTEGER,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_deleted INTEGER DEFAULT 0,
  FOREIGN KEY (plan_id) REFERENCES fitness_plans(id) ON DELETE CASCADE,
  FOREIGN KEY (template_day_id) REFERENCES template_days(id) ON DELETE SET NULL
);

-- 11. 训练日程动作表
CREATE TABLE IF NOT EXISTS workout_schedule_exercises (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  schedule_id INTEGER NOT NULL,
  exercise_id INTEGER,
  exercise_name VARCHAR(100),
  sets INTEGER NOT NULL,
  reps VARCHAR(20),
  duration VARCHAR(20),
  note VARCHAR(200),
  sort_order INTEGER DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_deleted INTEGER DEFAULT 0,
  FOREIGN KEY (schedule_id) REFERENCES workout_schedules(id) ON DELETE CASCADE,
  FOREIGN KEY (exercise_id) REFERENCES movement_exercises(id) ON DELETE SET NULL
);

-- 12. 锻炼记录表
CREATE TABLE IF NOT EXISTS workout_records (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id INTEGER NOT NULL,
  plan_id INTEGER NOT NULL,
  schedule_id INTEGER,
  schedule_exercise_id INTEGER,
  exercise_id INTEGER,
  exercise_name VARCHAR(100),
  date DATE NOT NULL,
  sets_completed TEXT NOT NULL,
  weight REAL,
  duration INTEGER,
  notes TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_deleted INTEGER DEFAULT 0,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (plan_id) REFERENCES fitness_plans(id) ON DELETE CASCADE,
  FOREIGN KEY (schedule_id) REFERENCES workout_schedules(id) ON DELETE SET NULL,
  FOREIGN KEY (schedule_exercise_id) REFERENCES workout_schedule_exercises(id) ON DELETE SET NULL,
  FOREIGN KEY (exercise_id) REFERENCES movement_exercises(id) ON DELETE SET NULL
);

-- 13. AI对话历史表
CREATE TABLE IF NOT EXISTS ai_conversations (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id INTEGER NOT NULL,
  messages TEXT NOT NULL,
  plan_id INTEGER,
  status TEXT DEFAULT 'in_progress',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_deleted INTEGER DEFAULT 0,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (plan_id) REFERENCES fitness_plans(id) ON DELETE SET NULL
);

-- 14. 音乐表
CREATE TABLE IF NOT EXISTS music_tracks (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name VARCHAR(100) NOT NULL,
  artist VARCHAR(100),
  album VARCHAR(100),
  duration INTEGER,
  file_url VARCHAR(500) NOT NULL,
  cover_url VARCHAR(500),
  genre VARCHAR(50),
  bpm INTEGER,
  is_active INTEGER DEFAULT 1,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  is_deleted INTEGER DEFAULT 0
);

-- 15. 数据库迁移记录表
CREATE TABLE IF NOT EXISTS db_migrations (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  migration_name VARCHAR(200) NOT NULL UNIQUE,
  batch INTEGER NOT NULL,
  executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. 初始化数据

-- 插入运动分类数据
INSERT INTO movement_categories (category_name, description, icon, parent_id, sort_order) VALUES
-- 顶级分类
('力量训练', '通过抗阻训练增强肌肉力量和耐力', 'dumbbell', 0, 1),
('有氧运动', '提高心肺功能和燃烧脂肪的运动', 'heart', 0, 2),
('柔韧性训练', '提高关节活动度和肌肉伸展性', 'flexibility', 0, 3),
('功能性训练', '模拟日常生活动作的训练', 'function', 0, 4),

-- 力量训练的子分类
('胸部训练', '胸大肌、胸小肌等相关训练', 'chest', 1, 1),
('背部训练', '背阔肌、斜方肌等相关训练', 'back', 1, 2),
('腿部训练', '股四头肌、腘绳肌等相关训练', 'leg', 1, 3),
('肩部训练', '三角肌等相关训练', 'shoulder', 1, 4),
('手臂训练', '肱二头肌、肱三头肌训练', 'arm', 1, 5),
('核心训练', '腹肌、腰腹核心训练', 'core', 1, 6),

-- 有氧运动的子分类
('跑步类', '各种跑步和跑步机训练', 'run', 2, 1),
('骑行类', '自行车、动感单车等', 'bike', 2, 2),
('游泳类', '各种游泳姿势', 'swim', 2, 3),
('跳绳类', '各类跳绳运动', 'rope', 2, 4),

-- 柔韧性训练的子分类
('拉伸运动', '静态和动态拉伸', 'stretch', 3, 1),
('瑜伽', '各种瑜伽体式', 'yoga', 3, 2),
('普拉提', '普拉提核心训练', 'pilates', 3, 3),

-- 功能性训练的子分类
('爆发力训练', '提高瞬间爆发力', 'power', 4, 1),
('平衡训练', '提高身体平衡能力', 'balance', 4, 2),
('敏捷训练', '提高身体敏捷性', 'agility', 4, 3);

-- 插入超级管理员初始数据
INSERT INTO users (role, username, password, nickname) VALUES
('super_admin', 'admin', '$2a$10$eW75xU2uLr0yq9X1e0e7pu5h0Q5qY0X7e0e7pu5h0Q5qY0X7e0e7pu', '超级管理员');

-- 插入运动动作示例数据
INSERT INTO movement_exercises (exercise_name, en_name, category_id, difficulty_level, target_muscle, equipment_needed, description, calories_per_hour, is_popular, is_bodyweight) VALUES
-- 胸部训练动作
('俯卧撑', 'Push-up', 5, 'beginner', '胸大肌、三角肌前束、肱三头肌', '瑜伽垫(可选)', '经典的胸部自重训练动作，锻炼胸肌和手臂力量', 300.00, 1, 1),
('杠铃卧推', 'Barbell Bench Press', 5, 'intermediate', '胸大肌、三角肌前束、肱三头肌', '卧推凳、杠铃、杠铃片', '最经典的胸肌训练动作，可以有效增加胸部厚度', 400.00, 1, 0),
('哑铃飞鸟', 'Dumbbell Fly', 5, 'intermediate', '胸大肌、胸小肌', '卧推凳、哑铃', '孤立训练胸肌的动作，有助于塑造胸部线条', 350.00, 1, 0),

-- 背部训练动作
('引体向上', 'Pull-up', 6, 'advanced', '背阔肌、肱二头肌、斜方肌', '单杠', '最好的背部自重训练动作，需要较强力量', 450.00, 1, 1),
('杠铃划船', 'Barbell Row', 6, 'intermediate', '背阔肌、斜方肌、菱形肌', '杠铃、杠铃片', '增加背部厚度的经典动作', 400.00, 1, 0),
('高位下拉', 'Lat Pulldown', 6, 'beginner', '背阔肌、大圆肌、肱二头肌', '高位下拉器械', '适合初学者的背部训练动作', 380.00, 1, 0),

-- 腿部训练动作
('深蹲', 'Squat', 7, 'beginner', '股四头肌、臀大肌、腘绳肌', '自重或杠铃', '腿部训练的王牌动作，全身性复合动作', 500.00, 1, 1),
('杠铃深蹲', 'Barbell Squat', 7, 'intermediate', '股四头肌、臀大肌、核心肌群', '深蹲架、杠铃、杠铃片', '增加腿部力量和肌肉维度的最佳动作', 600.00, 1, 0),
('腿举', 'Leg Press', 7, 'beginner', '股四头肌、臀大肌', '腿举器械', '对膝盖压力较小的腿部训练动作', 450.00, 1, 0),

-- 肩部训练动作
('肩部推举', 'Shoulder Press', 8, 'beginner', '三角肌前中束、肱三头肌', '哑铃或杠铃', '肩部训练的基础动作', 350.00, 1, 0),
('侧平举', 'Lateral Raise', 8, 'beginner', '三角肌中束', '哑铃', '塑造肩部宽度的绝佳动作', 300.00, 1, 0),
('俯身飞鸟', 'Bent-over Fly', 8, 'intermediate', '三角肌后束', '哑铃', '锻炼肩部后束，改善体态', 320.00, 1, 0),

-- 手臂训练动作
('二头弯举', 'Bicep Curl', 9, 'beginner', '肱二头肌', '哑铃或杠铃', '最经典的二头肌训练动作', 300.00, 1, 0),
('三头下压', 'Tricep Pushdown', 9, 'beginner', '肱三头肌', '龙门架', '锻炼三头肌的高效动作', 320.00, 1, 0),
('锤式弯举', 'Hammer Curl', 9, 'beginner', '肱二头肌、肱肌', '哑铃', '同时锻炼二头肌和前臂', 310.00, 1, 0),

-- 核心训练动作
('卷腹', 'Crunch', 10, 'beginner', '腹直肌', '瑜伽垫', '经典的腹肌训练动作', 250.00, 1, 1),
('平板支撑', 'Plank', 10, 'beginner', '核心肌群', '瑜伽垫', '训练核心稳定性的最佳动作', 280.00, 1, 1),
('仰卧抬腿', 'Leg Raise', 10, 'intermediate', '下腹部', '瑜伽垫', '针对下腹部的训练动作', 260.00, 1, 1),

-- 有氧运动
('慢跑', 'Jogging', 11, 'beginner', '全身', '舒适的跑鞋', '简单有效的有氧运动', 400.00, 1, 1),
('动感单车', 'Spinning', 12, 'intermediate', '腿部、心肺', '动感单车', '高强度的有氧训练', 500.00, 1, 0),
('游泳', 'Swimming', 13, 'intermediate', '全身', '游泳池', '对关节友好的全身有氧运动', 600.00, 1, 0),
('跳绳', 'Jump Rope', 14, 'beginner', '全身、心肺', '跳绳', '高效的燃脂运动', 700.00, 1, 1),

-- 功能性训练动作
('弓步蹲', 'Lunge', 15, 'beginner', '股四头肌、臀大肌、腘绳肌', '自重或哑铃', '功能性腿部训练，提高平衡能力', 320.00, 1, 1),
('臀桥', 'Glute Bridge', 15, 'beginner', '臀大肌、腘绳肌', '瑜伽垫', '针对臀部的训练动作', 280.00, 1, 1);

-- 插入健身模板数据
INSERT INTO templates (name, description, image, difficulty, is_public, created_by) VALUES
('基础健身计划（新手）', '适合健身新手，包含基础动作和循序渐进的训练计划，每周训练5天，休息2天', 'https://example.com/images/beginner-template.jpg', 'beginner', 1, NULL),
('增肌计划（中级）', '针对有一定基础的训练者，以增肌为目标的训练计划，每周训练6天', 'https://example.com/images/muscle-template.jpg', 'intermediate', 1, NULL),
('减脂计划（新手）', '以减脂为目标的综合性训练计划，结合有氧和力量训练', 'https://example.com/images/fat-loss-template.jpg', 'beginner', 1, NULL);

-- 插入模板训练日数据
-- 基础健身计划（模板ID=1）
INSERT INTO template_days (template_id, day_of_week, is_rest_day, rest_note, estimated_duration) VALUES
(1, 1, 0, NULL, 45),
(1, 2, 0, NULL, 35),
(1, 3, 1, '好好休息，让肌肉恢复', 0),
(1, 4, 0, NULL, 45),
(1, 5, 0, NULL, 35),
(1, 6, 1, '可以做一些轻松的拉伸运动', 0),
(1, 7, 0, NULL, 30);

-- 增肌计划（模板ID=2）
INSERT INTO template_days (template_id, day_of_week, is_rest_day, rest_note, estimated_duration) VALUES
(2, 1, 0, NULL, 60),
(2, 2, 0, NULL, 60),
(2, 3, 1, '充分休息，保证肌肉生长', 0),
(2, 4, 0, NULL, 60),
(2, 5, 0, NULL, 60),
(2, 6, 0, NULL, 65),
(2, 7, 1, '完全休息，为下周训练做好准备', 0);

-- 减脂计划（模板ID=3）
INSERT INTO template_days (template_id, day_of_week, is_rest_day, rest_note, estimated_duration) VALUES
(3, 1, 0, NULL, 50),
(3, 2, 0, NULL, 45),
(3, 3, 1, '可以做一些轻松的散步', 0),
(3, 4, 0, NULL, 50),
(3, 5, 0, NULL, 45),
(3, 6, 0, NULL, 50),
(3, 7, 1, '好好休息，控制饮食', 0);

-- 插入模板动作关联数据
-- 基础健身计划 - 第1天
INSERT INTO template_exercises (template_day_id, exercise_id, exercise_name, sets, reps, duration, note, sort_order) VALUES
(1, 1, '俯卧撑', 3, '10-15', NULL, NULL, 1),
(1, 16, '卷腹', 3, '15-20', NULL, NULL, 2),
(1, 17, '平板支撑', 3, NULL, '30-45秒', NULL, 3),
(1, 7, '深蹲', 3, '12-15', NULL, NULL, 4),
(1, 22, '弓步蹲', 3, '10-12', NULL, '每侧', 5),
(1, 23, '臀桥', 3, '15-20', NULL, NULL, 6);

-- 基础健身计划 - 第2天
INSERT INTO template_exercises (template_day_id, exercise_id, exercise_name, sets, reps, duration, note, sort_order) VALUES
(2, 18, '慢跑', 1, NULL, '20-30分钟', NULL, 1),
(2, 21, '跳绳', 5, '1分钟', NULL, '组间休息30秒', 2),
(2, 17, '平板支撑', 3, NULL, '30-45秒', NULL, 3);

-- 基础健身计划 - 第4天
INSERT INTO template_exercises (template_day_id, exercise_id, exercise_name, sets, reps, duration, note, sort_order) VALUES
(4, 1, '俯卧撑', 3, '12-18', NULL, NULL, 1),
(4, 16, '卷腹', 3, '20-25', NULL, NULL, 2),
(4, 19, '仰卧抬腿', 3, '10-12', NULL, NULL, 3),
(4, 7, '深蹲', 3, '15-18', NULL, NULL, 4),
(4, 23, '臀桥', 3, '20-25', NULL, NULL, 5);

-- 基础健身计划 - 第5天
INSERT INTO template_exercises (template_day_id, exercise_id, exercise_name, sets, reps, duration, note, sort_order) VALUES
(5, 18, '慢跑', 1, NULL, '25-35分钟', NULL, 1),
(5, 21, '跳绳', 6, '1分钟', NULL, '组间休息30秒', 2);

-- 基础健身计划 - 第7天
INSERT INTO template_exercises (template_day_id, exercise_id, exercise_name, sets, reps, duration, note, sort_order) VALUES
(7, NULL, '全身拉伸', 1, NULL, '15-20分钟', NULL, 1),
(7, 17, '平板支撑', 3, NULL, '45-60秒', NULL, 2),
(7, 7, '深蹲', 2, '10-12', NULL, '活动一下', 3);

-- 增肌计划 - 第1天
INSERT INTO template_exercises (template_day_id, exercise_id, exercise_name, sets, reps, duration, note, sort_order) VALUES
(8, 2, '杠铃卧推', 4, '8-12', NULL, NULL, 1),
(8, 3, '哑铃飞鸟', 3, '10-12', NULL, NULL, 2),
(8, 10, '肩部推举', 4, '8-12', NULL, NULL, 3),
(8, 11, '侧平举', 3, '12-15', NULL, NULL, 4),
(8, 13, '二头弯举', 4, '8-12', NULL, NULL, 5),
(8, 14, '三头下压', 4, '8-12', NULL, NULL, 6);

-- 增肌计划 - 第2天
INSERT INTO template_exercises (template_day_id, exercise_id, exercise_name, sets, reps, duration, note, sort_order) VALUES
(9, 8, '杠铃深蹲', 5, '6-10', NULL, NULL, 1),
(9, 9, '腿举', 4, '10-12', NULL, NULL, 2),
(9, 5, '杠铃划船', 4, '8-12', NULL, NULL, 3),
(9, 6, '高位下拉', 4, '10-12', NULL, NULL, 4),
(9, 16, '卷腹', 4, '15-20', NULL, NULL, 5),
(9, 17, '平板支撑', 3, NULL, '60秒', NULL, 6);

-- 增肌计划 - 第4天
INSERT INTO template_exercises (template_day_id, exercise_id, exercise_name, sets, reps, duration, note, sort_order) VALUES
(11, 2, '杠铃卧推', 4, '8-12', NULL, NULL, 1),
(11, 3, '哑铃飞鸟', 3, '10-12', NULL, NULL, 2),
(11, 10, '肩部推举', 4, '8-12', NULL, NULL, 3),
(11, 12, '俯身飞鸟', 3, '12-15', NULL, NULL, 4),
(11, 15, '锤式弯举', 4, '8-12', NULL, NULL, 5),
(11, 14, '三头下压', 4, '8-12', NULL, NULL, 6);

-- 增肌计划 - 第5天
INSERT INTO template_exercises (template_day_id, exercise_id, exercise_name, sets, reps, duration, note, sort_order) VALUES
(12, 8, '杠铃深蹲', 5, '6-10', NULL, NULL, 1),
(12, 9, '腿举', 4, '10-12', NULL, NULL, 2),
(12, 4, '引体向上', 4, '8-12', NULL, NULL, 3),
(12, 6, '高位下拉', 4, '10-12', NULL, NULL, 4),
(12, 16, '卷腹', 4, '15-20', NULL, NULL, 5),
(12, 19, '仰卧抬腿', 3, '12-15', NULL, NULL, 6);

-- 增肌计划 - 第6天
INSERT INTO template_exercises (template_day_id, exercise_id, exercise_name, sets, reps, duration, note, sort_order) VALUES
(13, 20, '动感单车', 1, NULL, '45分钟', NULL, 1),
(13, NULL, '全身拉伸', 1, NULL, '20分钟', NULL, 2);

-- 减脂计划 - 第1天
INSERT INTO template_exercises (template_day_id, exercise_id, exercise_name, sets, reps, duration, note, sort_order) VALUES
(14, 18, '慢跑', 1, NULL, '30分钟', NULL, 1),
(14, 1, '俯卧撑', 3, '10-15', NULL, NULL, 2),
(14, 7, '深蹲', 3, '15-20', NULL, NULL, 3),
(14, 16, '卷腹', 3, '20-25', NULL, NULL, 4),
(14, 21, '跳绳', 5, '1分钟', NULL, '组间休息30秒', 5);

-- 减脂计划 - 第2天
INSERT INTO template_exercises (template_day_id, exercise_id, exercise_name, sets, reps, duration, note, sort_order) VALUES
(15, 20, '动感单车', 1, NULL, '40分钟', NULL, 1),
(15, 17, '平板支撑', 4, NULL, '45秒', NULL, 2),
(15, 22, '弓步蹲', 3, '12-15', NULL, '每侧', 3);

-- 减脂计划 - 第4天
INSERT INTO template_exercises (template_day_id, exercise_id, exercise_name, sets, reps, duration, note, sort_order) VALUES
(17, 18, '慢跑', 1, NULL, '35分钟', NULL, 1),
(17, 1, '俯卧撑', 3, '12-18', NULL, NULL, 2),
(17, 7, '深蹲', 3, '18-22', NULL, NULL, 3),
(17, 23, '臀桥', 3, '20-25', NULL, NULL, 4),
(17, 21, '跳绳', 6, '1分钟', NULL, '组间休息30秒', 5);

-- 减脂计划 - 第5天
INSERT INTO template_exercises (template_day_id, exercise_id, exercise_name, sets, reps, duration, note, sort_order) VALUES
(18, 20, '游泳', 1, NULL, '30分钟', NULL, 1),
(18, 16, '卷腹', 4, '20-25', NULL, NULL, 2),
(18, 17, '平板支撑', 4, NULL, '60秒', NULL, 3);

-- 减脂计划 - 第6天
INSERT INTO template_exercises (template_day_id, exercise_id, exercise_name, sets, reps, duration, note, sort_order) VALUES
(19, NULL, '全身拉伸', 1, NULL, '30分钟', NULL, 1),
(19, 18, '轻松慢跑', 1, NULL, '20分钟', NULL, 2);

-- 6. 插入应用配置数据
INSERT INTO app_configs (config_key, config_value, config_type, description) VALUES
('app_name', '健身助手', 'string', '应用名称'),
('version', '1.0.0', 'string', '应用版本'),
('maintenance_mode', 'false', 'boolean', '是否维护模式'),
('max_workout_records_per_user', '1000', 'number', '每个用户最大锻炼记录数'),
('default_plan_duration', '8', 'number', '默认计划周期(周)'),
('ai_enabled', 'true', 'boolean', '是否启用AI功能'),
('music_enabled', 'true', 'boolean', '是否启用音乐功能'),
('social_share_enabled', 'true', 'boolean', '是否启用社交分享'),
('max_file_size_mb', '10', 'number', '最大文件大小(MB)'),
('workout_reminder_enabled', 'true', 'boolean', '是否启用训练提醒');

-- 9. 记录迁移
INSERT INTO db_migrations (migration_name, batch) VALUES
('001_initial_schema.sql', 1);