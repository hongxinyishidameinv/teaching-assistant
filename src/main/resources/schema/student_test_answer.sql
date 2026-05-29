CREATE TABLE IF NOT EXISTS student_test_answer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    test_id BIGINT NOT NULL,
    question_id INT NOT NULL,
    answer TEXT,
    score INT DEFAULT 0,
    status INT DEFAULT 0,
    submit_time DATETIME NOT NULL,
    grade_time DATETIME,
    FOREIGN KEY (test_id) REFERENCES test(id) ON DELETE CASCADE,
    INDEX idx_student_test (student_id, test_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
