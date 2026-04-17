-- 创建数据库
CREATE DATABASE IF NOT EXISTS fitness_app 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE fitness_app;

-- 1. 用户表
CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  open_id VARCHAR(64) UNIQUE COMMENT '微信openID',
  nickname VARCHAR(100) COMMENT '昵称',
  avatar_url VARCHAR(500) COMMENT '头像URL',
  gender TINYINT COMMENT '性别 1男2女',
  country VARCHAR(50) COMMENT '国家',
  province VARCHAR(50) COMMENT '省份',
  city VARCHAR(50) COMMENT '城市',
  role ENUM('user','admin','super_admin') DEFAULT 'user' COMMENT '用户角色',
  username VARCHAR(50) UNIQUE COMMENT '管理员用户名',
  password VARCHAR(100) COMMENT '管理员密码（加密存储）',
  fitness_goals JSON COMMENT '健身目标 JSON数组',
  fitness_level ENUM('beginner','intermediate','advanced') DEFAULT 'beginner',
  height DECIMAL(5,1) COMMENT '身高(cm)',
  weight DECIMAL(5,1) COMMENT '体重(kg)',
  is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0否 1是',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_open_id (open_id),
  INDEX idx_username (username),
  INDEX idx_role (role),
  INDEX idx_is_deleted (is_deleted),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 用户设备表（新增）
CREATE TABLE user_devices (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '设备ID',
  user_id INT NOT NULL COMMENT '用户ID',
  device_type ENUM('ios', 'android', 'web') NOT NULL,
  device_token VARCHAR(500) COMMENT '设备推送token',
  device_id VARCHAR(255) COMMENT '设备唯一标识',
  app_version VARCHAR(20) COMMENT '应用版本',
  last_login_at TIMESTAMP NULL COMMENT '最后登录时间',
  is_active TINYINT DEFAULT 1 COMMENT '是否激活',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT DEFAULT 0,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_device (user_id, device_type),
  INDEX idx_device_token (device_token(255)),
  INDEX idx_device_id (device_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户设备表';

-- 3. 应用配置表（新增）
CREATE TABLE app_configs (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
  config_key VARCHAR(100) UNIQUE NOT NULL COMMENT '配置键',
  config_value TEXT COMMENT '配置值',
  config_type ENUM('string', 'number', 'boolean', 'json') DEFAULT 'string',
  description VARCHAR(255) COMMENT '配置描述',
  is_active TINYINT DEFAULT 1 COMMENT '是否启用',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT DEFAULT 0,
  INDEX idx_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用配置表';

-- 4. 运动分类表
CREATE TABLE movement_categories (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
  category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
  description TEXT COMMENT '分类描述',
  icon VARCHAR(100) COMMENT '图标标识',
  sort_order INT DEFAULT 0 COMMENT '排序',
  parent_id INT DEFAULT 0 COMMENT '父级ID 0表示顶级分类',
  is_active TINYINT DEFAULT 1 COMMENT '是否启用',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0否 1是',
  INDEX idx_parent_id (parent_id),
  INDEX idx_sort_order (sort_order),
  INDEX idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运动分类表';

-- 5. 运动动作表
CREATE TABLE movement_exercises (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '动作ID',
  exercise_name VARCHAR(100) NOT NULL COMMENT '动作名称',
  en_name VARCHAR(100) COMMENT '英文名称',
  category_id INT NOT NULL COMMENT '所属分类ID',
  difficulty_level ENUM('beginner','intermediate','advanced') DEFAULT 'beginner',
  target_muscle VARCHAR(200) COMMENT '目标肌肉群',
  equipment_needed VARCHAR(200) COMMENT '所需器材',
  description TEXT COMMENT '动作描述',
  video_url VARCHAR(500) COMMENT '教学视频链接',
  image_url VARCHAR(500) COMMENT '示意图链接',
  calories_per_hour DECIMAL(6,2) COMMENT '每小时消耗卡路里',
  is_popular TINYINT DEFAULT 0 COMMENT '是否热门',
  is_bodyweight TINYINT DEFAULT 0 COMMENT '是否自重训练',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0否 1是',
  FOREIGN KEY (category_id) REFERENCES movement_categories(id) ON DELETE CASCADE,
  INDEX idx_category_id (category_id),
  INDEX idx_difficulty (difficulty_level),
  INDEX idx_popular (is_popular),
  INDEX idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运动动作表';

-- 6. 健身模板表
CREATE TABLE templates (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '模板ID',
  name VARCHAR(100) NOT NULL COMMENT '模板名称',
  description TEXT NOT NULL COMMENT '模板描述',
  image VARCHAR(500) COMMENT '模板图片',
  difficulty ENUM('beginner','intermediate','advanced') DEFAULT 'beginner',
  is_public TINYINT DEFAULT 1 COMMENT '是否公开模板',
  created_by INT COMMENT '创建者用户ID',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0否 1是',
  FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
  INDEX idx_created_by (created_by),
  INDEX idx_is_public (is_public),
  INDEX idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健身模板表';

-- 7. 模板训练日表
CREATE TABLE template_days (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '训练日ID',
  template_id INT NOT NULL COMMENT '模板ID',
  day_of_week INT NOT NULL COMMENT '周几 1-7',
  is_rest_day TINYINT DEFAULT 0 COMMENT '是否休息日',
  rest_note VARCHAR(200) COMMENT '休息日备注',
  estimated_duration INT COMMENT '预计时长(分钟)',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0否 1是',
  FOREIGN KEY (template_id) REFERENCES templates(id) ON DELETE CASCADE,
  INDEX idx_template_id (template_id),
  INDEX idx_template_day (template_id, day_of_week),
  INDEX idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板训练日表';

-- 8. 模板动作关联表
CREATE TABLE template_exercises (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
  template_day_id INT NOT NULL COMMENT '模板训练日ID',
  exercise_id INT COMMENT '动作ID',
  exercise_name VARCHAR(100) COMMENT '动作名称（当没有关联动作时使用）',
  sets INT NOT NULL COMMENT '组数',
  reps VARCHAR(20) COMMENT '每组次数',
  duration VARCHAR(20) COMMENT '持续时间',
  note VARCHAR(200) COMMENT '备注',
  sort_order INT DEFAULT 0 COMMENT '排序',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0否 1是',
  FOREIGN KEY (template_day_id) REFERENCES template_days(id) ON DELETE CASCADE,
  FOREIGN KEY (exercise_id) REFERENCES movement_exercises(id) ON DELETE SET NULL,
  INDEX idx_template_day_id (template_day_id),
  INDEX idx_exercise_id (exercise_id),
  INDEX idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板动作关联表';

-- 9. 健身计划表
CREATE TABLE fitness_plans (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '计划ID',
  user_id INT NOT NULL COMMENT '用户ID',
  template_id INT COMMENT '模板ID',
  name VARCHAR(100) NOT NULL COMMENT '计划名称',
  type ENUM('template','custom','ai') NOT NULL COMMENT '计划类型',
  goal VARCHAR(50) NOT NULL COMMENT '健身目标',
  difficulty ENUM('beginner','intermediate','advanced') DEFAULT 'beginner',
  duration_weeks INT NOT NULL COMMENT '周期(周)',
  start_date DATE NOT NULL COMMENT '开始日期',
  end_date DATE NOT NULL COMMENT '结束日期',
  status ENUM('active','paused','completed','stopped') DEFAULT 'active',
  is_shared TINYINT DEFAULT 0 COMMENT '是否分享',
  shared_code VARCHAR(32) COMMENT '分享码',
  last_workout_date DATE COMMENT '最后训练日期',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0否 1是',
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (template_id) REFERENCES templates(id) ON DELETE SET NULL,
  INDEX idx_user_id (user_id),
  INDEX idx_status (status),
  INDEX idx_shared_code (shared_code),
  INDEX idx_user_status (user_id, status),
  INDEX idx_user_type_status (user_id, type, status),
  INDEX idx_user_date (user_id, start_date, end_date),
  INDEX idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健身计划表';

-- 10. 训练日程表
CREATE TABLE workout_schedules (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '日程ID',
  plan_id INT NOT NULL COMMENT '计划ID',
  week_num INT NOT NULL COMMENT '第几周',
  day_of_week INT NOT NULL COMMENT '周几 1-7',
  date DATE NOT NULL COMMENT '训练日期',
  is_rest_day TINYINT DEFAULT 0 COMMENT '是否休息日',
  rest_note VARCHAR(200) COMMENT '休息日备注',
  estimated_duration INT COMMENT '预计时长(分钟)',
  template_day_id INT COMMENT '关联的模板训练日ID',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0否 1是',
  FOREIGN KEY (plan_id) REFERENCES fitness_plans(id) ON DELETE CASCADE,
  FOREIGN KEY (template_day_id) REFERENCES template_days(id) ON DELETE SET NULL,
  INDEX idx_plan_id (plan_id),
  INDEX idx_plan_week (plan_id, week_num),
  INDEX idx_plan_day (plan_id, day_of_week),
  INDEX idx_plan_week_day (plan_id, week_num, day_of_week),
  INDEX idx_plan_template_day (plan_id, template_day_id),
  INDEX idx_date (date),
  INDEX idx_plan_date (plan_id, date),
  INDEX idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='训练日程表';

-- 11. 训练日程动作表
CREATE TABLE workout_schedule_exercises (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '训练动作ID',
  schedule_id INT NOT NULL COMMENT '日程ID',
  exercise_id INT COMMENT '动作ID',
  exercise_name VARCHAR(100) COMMENT '动作名称（当没有关联动作时使用）',
  sets INT NOT NULL COMMENT '组数',
  reps VARCHAR(20) COMMENT '每组次数',
  duration VARCHAR(20) COMMENT '持续时间',
  note VARCHAR(200) COMMENT '备注',
  sort_order INT DEFAULT 0 COMMENT '排序',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0否 1是',
  FOREIGN KEY (schedule_id) REFERENCES workout_schedules(id) ON DELETE CASCADE,
  FOREIGN KEY (exercise_id) REFERENCES movement_exercises(id) ON DELETE SET NULL,
  INDEX idx_schedule_id (schedule_id),
  INDEX idx_exercise_id (exercise_id),
  INDEX idx_schedule_sort (schedule_id, sort_order),
  INDEX idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='训练日程动作表';

-- 12. 锻炼记录表（移除分区，优化索引）
CREATE TABLE workout_records (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
  user_id INT NOT NULL COMMENT '用户ID',
  plan_id INT NOT NULL COMMENT '计划ID',
  schedule_id INT COMMENT '日程ID',
  schedule_exercise_id INT COMMENT '训练日程动作ID',
  exercise_id INT COMMENT '动作ID',
  exercise_name VARCHAR(100) COMMENT '动作名称',
  date DATE NOT NULL COMMENT '训练日期',
  sets_completed JSON NOT NULL COMMENT '完成组数 JSON',
  weight DECIMAL(5,1) COMMENT '重量(kg)',
  duration INT COMMENT '时长(秒)',
  notes TEXT COMMENT '备注',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0否 1是',
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (plan_id) REFERENCES fitness_plans(id) ON DELETE CASCADE,
  FOREIGN KEY (schedule_id) REFERENCES workout_schedules(id) ON DELETE SET NULL,
  FOREIGN KEY (schedule_exercise_id) REFERENCES workout_schedule_exercises(id) ON DELETE SET NULL,
  FOREIGN KEY (exercise_id) REFERENCES movement_exercises(id) ON DELETE SET NULL,
  INDEX idx_user_id (user_id),
  INDEX idx_plan_id (plan_id),
  INDEX idx_schedule_id (schedule_id),
  INDEX idx_exercise_id (exercise_id),
  INDEX idx_date (date),
  INDEX idx_user_date (user_id, date),
  INDEX idx_user_plan_date (user_id, plan_id, date),
  INDEX idx_plan_schedule_date (plan_id, schedule_id, date),
  INDEX idx_exercise_date (exercise_id, date),
  INDEX idx_date_user_plan (date, user_id, plan_id),
  INDEX idx_user_exercise_date (user_id, exercise_id, date),
  INDEX idx_schedule_date (schedule_id, date),
  INDEX idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='锻炼记录表';

-- 13. AI对话历史表
CREATE TABLE ai_conversations (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '对话ID',
  user_id INT NOT NULL COMMENT '用户ID',
  messages JSON NOT NULL COMMENT '对话历史 JSON',
  plan_id INT COMMENT '生成的计划ID',
  status ENUM('in_progress','completed','cancelled') DEFAULT 'in_progress',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0否 1是',
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (plan_id) REFERENCES fitness_plans(id) ON DELETE SET NULL,
  INDEX idx_user_id (user_id),
  INDEX idx_status (status),
  INDEX idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话历史表';

-- 14. 音乐表
CREATE TABLE music_tracks (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '音乐ID',
  name VARCHAR(100) NOT NULL COMMENT '音乐名称',
  artist VARCHAR(100) COMMENT '艺术家',
  album VARCHAR(100) COMMENT '专辑',
  duration INT COMMENT '时长(秒)',
  file_url VARCHAR(500) NOT NULL COMMENT '文件URL',
  cover_url VARCHAR(500) COMMENT '封面URL',
  genre VARCHAR(50) COMMENT '音乐流派',
  bpm INT COMMENT '节奏BPM',
  is_active TINYINT DEFAULT 1 COMMENT '是否启用',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0否 1是',
  INDEX idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='音乐表';



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

-- 3. 创建视图

-- 3.1 用户锻炼摘要视图
CREATE VIEW v_user_workout_summary AS
SELECT 
  u.id as user_id,
  u.nickname,
  COUNT(DISTINCT wr.date) as workout_days,
  COUNT(wr.id) as total_exercises,
  SUM(wr.duration) as total_duration_seconds
FROM users u
LEFT JOIN workout_records wr ON u.id = wr.user_id 
  AND wr.is_deleted = 0
  AND wr.date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
WHERE u.is_deleted = 0
GROUP BY u.id, u.nickname;

-- 3.2 用户计划进度视图
CREATE VIEW v_user_plan_progress AS
SELECT 
  fp.id as plan_id,
  fp.user_id,
  fp.name as plan_name,
  fp.start_date,
  fp.end_date,
  fp.status,
  COUNT(DISTINCT ws.id) as total_scheduled_workouts,
  COUNT(DISTINCT wr.schedule_id) as completed_workouts,
  ROUND(COUNT(DISTINCT wr.schedule_id) * 100.0 / GREATEST(COUNT(DISTINCT ws.id), 1), 2) as completion_rate
FROM fitness_plans fp
LEFT JOIN workout_schedules ws ON fp.id = ws.plan_id AND ws.is_deleted = 0
LEFT JOIN workout_records wr ON ws.id = wr.schedule_id AND wr.is_deleted = 0
WHERE fp.is_deleted = 0
GROUP BY fp.id, fp.user_id, fp.name, fp.start_date, fp.end_date, fp.status;

-- 4. 创建存储过程和函数

-- 4.1 清理旧数据的存储过程
DELIMITER //
CREATE PROCEDURE cleanup_old_workout_records(IN months_keep INT)
BEGIN
  DECLARE delete_date DATE;
  DECLARE deleted_count INT;
  
  -- 计算删除截止日期
  SET delete_date = DATE_SUB(CURDATE(), INTERVAL months_keep MONTH);
  
  -- 标记旧数据为已删除
  UPDATE workout_records 
  SET is_deleted = 1 
  WHERE date < delete_date 
    AND is_deleted = 0;
  
  -- 获取删除的记录数
  SET deleted_count = ROW_COUNT();
  
  -- 返回结果
  SELECT CONCAT('Marked ', deleted_count, ' records as deleted before ', delete_date) as result;
END //
DELIMITER ;

-- 4.2 创建健身计划的存储过程
DELIMITER //
CREATE PROCEDURE create_fitness_plan_from_template(
  IN p_user_id INT,
  IN p_template_id INT,
  IN p_start_date DATE,
  OUT p_plan_id INT
)
BEGIN
  DECLARE v_duration_weeks INT;
  DECLARE v_end_date DATE;
  DECLARE v_plan_name VARCHAR(100);
  DECLARE v_difficulty ENUM('beginner','intermediate','advanced');
  DECLARE v_goal VARCHAR(50);
  
  -- 获取模板信息
  SELECT name, difficulty, duration_weeks, 
         CASE difficulty 
           WHEN 'beginner' THEN '基础健身'
           WHEN 'intermediate' THEN '增肌'
           ELSE '减脂' 
         END INTO v_plan_name, v_difficulty, v_duration_weeks, v_goal
  FROM templates 
  WHERE id = p_template_id AND is_deleted = 0;
  
  -- 计算结束日期
  SET v_end_date = DATE_ADD(p_start_date, INTERVAL v_duration_weeks WEEK);
  
  -- 创建计划
  INSERT INTO fitness_plans (user_id, template_id, name, type, goal, difficulty, duration_weeks, start_date, end_date, status)
  VALUES (p_user_id, p_template_id, v_plan_name, 'template', v_goal, v_difficulty, v_duration_weeks, p_start_date, v_end_date, 'active');
  
  SET p_plan_id = LAST_INSERT_ID();
  
  -- 创建训练日程
  INSERT INTO workout_schedules (plan_id, week_num, day_of_week, date, is_rest_day, rest_note, estimated_duration, template_day_id)
  SELECT 
    p_plan_id,
    w.week_num,
    td.day_of_week,
    DATE_ADD(p_start_date, INTERVAL (w.week_num - 1) WEEK + (td.day_of_week - 1) DAY) as date,
    td.is_rest_day,
    td.rest_note,
    td.estimated_duration,
    td.id
  FROM (
    -- 生成周数
    SELECT n.week_num
    FROM (
      SELECT 1 as week_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8
    ) n
    WHERE n.week_num <= v_duration_weeks
  ) w
  CROSS JOIN template_days td
  WHERE td.template_id = p_template_id 
    AND td.is_deleted = 0
  ORDER BY w.week_num, td.day_of_week;
END //
DELIMITER ;

-- 4.3 获取用户最近训练日期的函数
DELIMITER //
CREATE FUNCTION get_user_last_workout_date(p_user_id INT) 
RETURNS DATE
DETERMINISTIC
READS SQL DATA
BEGIN
  DECLARE v_last_date DATE;
  
  SELECT MAX(date) INTO v_last_date
  FROM workout_records
  WHERE user_id = p_user_id 
    AND is_deleted = 0;
    
  RETURN v_last_date;
END //
DELIMITER ;

-- 5. 创建触发器

-- 5.1 在删除用户前检查是否有活跃计划
DELIMITER //
CREATE TRIGGER before_user_delete_check
BEFORE UPDATE ON users
FOR EACH ROW
BEGIN
  DECLARE active_plans INT;
  
  IF NEW.is_deleted = 1 AND OLD.is_deleted = 0 THEN
    -- 检查用户是否有活跃计划
    SELECT COUNT(*) INTO active_plans
    FROM fitness_plans
    WHERE user_id = OLD.id 
      AND status = 'active' 
      AND is_deleted = 0;
    
    IF active_plans > 0 THEN
      SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Cannot delete user with active fitness plans. Please cancel or complete plans first.';
    END IF;
  END IF;
END //
DELIMITER ;

-- 5.2 在创建锻炼记录时更新计划最后训练日期
DELIMITER //
CREATE TRIGGER after_workout_record_insert
AFTER INSERT ON workout_records
FOR EACH ROW
BEGIN
  UPDATE fitness_plans
  SET last_workout_date = NEW.date,
      updated_at = CURRENT_TIMESTAMP
  WHERE id = NEW.plan_id 
    AND (last_workout_date IS NULL OR last_workout_date < NEW.date)
    AND is_deleted = 0;
END //
DELIMITER ;

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

