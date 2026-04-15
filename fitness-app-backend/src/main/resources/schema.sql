
CREATE TABLE IF NOT EXISTS workout_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    plan_id BIGINT,
    date DATE NOT NULL,
    day_number INT,
    completed BOOLEAN DEFAULT FALSE,
    exercises_json TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_date (user_id, date),
    INDEX idx_user_id (user_id)
);

