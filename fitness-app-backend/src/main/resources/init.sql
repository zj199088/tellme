-- 健身应用数据库初始化脚本

-- 创建用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    open_id VARCHAR(100),
    username VARCHAR(50) UNIQUE,
    password VARCHAR(100),
    nickname VARCHAR(50),
    avatar_url VARCHAR(255),
    gender INT,
    country VARCHAR(50),
    province VARCHAR(50),
    city VARCHAR(50),
    role VARCHAR(20) DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建运动分类表
CREATE TABLE IF NOT EXISTS movement_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建运动动作表
CREATE TABLE IF NOT EXISTS movement_exercise (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    image_url VARCHAR(255),
    video_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建模板表
CREATE TABLE IF NOT EXISTS template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    difficulty VARCHAR(20),
    image VARCHAR(255),
    is_public BOOLEAN DEFAULT TRUE,
    creator_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建模板训练日表
CREATE TABLE IF NOT EXISTS template_day (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_id BIGINT,
    day_number INT,
    day_name VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建模板训练动作表
CREATE TABLE IF NOT EXISTS template_exercise (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_day_id BIGINT,
    exercise_id BIGINT,
    sets INT,
    reps INT,
    weight DECIMAL(10,2),
    rest_time INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建健身计划表
CREATE TABLE IF NOT EXISTS fitness_plan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    template_id BIGINT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    start_date DATE,
    end_date DATE,
    current_day INT DEFAULT 1,
    total_days INT,
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建训练记录表
CREATE TABLE IF NOT EXISTS workout_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    plan_id BIGINT,
    date DATE NOT NULL,
    day_number INT,
    completed BOOLEAN DEFAULT FALSE,
    exercises_json TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建训练日程表
CREATE TABLE IF NOT EXISTS workout_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    plan_id BIGINT,
    date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'scheduled',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_date (user_id, date)
);

-- 创建索引
CREATE INDEX idx_user_role ON user(role);
CREATE INDEX idx_template_is_public ON template(is_public);
CREATE INDEX idx_fitness_plan_user_status ON fitness_plan(user_id, status);
CREATE INDEX idx_workout_record_user_date ON workout_record(user_id, date);
CREATE INDEX idx_workout_schedule_user_date ON workout_schedule(user_id, date);

-- 插入初始数据
INSERT INTO movement_category (name, description) VALUES
('胸部', '胸部训练动作'),
('背部', '背部训练动作'),
('腿部', '腿部训练动作'),
('肩部', '肩部训练动作'),
('手臂', '手臂训练动作'),
('核心', '核心训练动作'),
('有氧', '有氧运动');

INSERT INTO movement_exercise (category_id, name, description) VALUES
(1, '俯卧撑', '基础胸部训练动作'),
(1, '卧推', '经典胸部训练动作'),
(2, '引体向上', '背部训练动作'),
(2, '划船', '背部训练动作'),
(3, '深蹲', '腿部训练动作'),
(3, '硬拉', '腿部训练动作'),
(4, '肩推', '肩部训练动作'),
(4, '侧平举', '肩部训练动作'),
(5, '二头肌弯举', '手臂训练动作'),
(5, '三头肌下压', '手臂训练动作'),
(6, '平板支撑', '核心训练动作'),
(6, '仰卧起坐', '核心训练动作'),
(7, '跑步', '有氧运动'),
(7, '跳绳', '有氧运动');

INSERT INTO template (name, description, difficulty, is_public) VALUES
('新手入门计划', '适合健身新手的基础训练计划', 'beginner', TRUE),
('增肌计划', '针对肌肉增长的训练计划', 'intermediate', TRUE),
('减脂计划', '针对脂肪减少的训练计划', 'intermediate', TRUE),
('高级训练计划', '适合有一定基础的健身爱好者', 'advanced', TRUE);

INSERT INTO template_day (template_id, day_number, day_name) VALUES
(1, 1, '胸部和三头肌'),
(1, 2, '背部和二头肌'),
(1, 3, '腿部'),
(1, 4, '肩部和核心'),
(1, 5, '有氧训练'),
(1, 6, '全身训练'),
(1, 7, '休息');

INSERT INTO template_exercise (template_day_id, exercise_id, sets, reps, weight, rest_time) VALUES
(1, 1, 3, 15, NULL, 60),
(1, 2, 3, 12, 20, 90),
(1, 10, 3, 15, 10, 60),
(2, 3, 3, 10, NULL, 90),
(2, 4, 3, 12, 15, 60),
(2, 9, 3, 15, 10, 60),
(3, 5, 4, 12, 30, 90),
(3, 6, 3, 8, 40, 120),
(4, 7, 3, 12, 15, 90),
(4, 8, 3, 15, 5, 60),
(4, 11, 3, 60, NULL, 30),
(5, 13, 1, 30, NULL, 0),
(5, 14, 1, 10, NULL, 0),
(6, 1, 2, 10, NULL, 60),
(6, 3, 2, 8, NULL, 60),
(6, 5, 2, 10, 20, 60),
(6, 7, 2, 10, 10, 60);
