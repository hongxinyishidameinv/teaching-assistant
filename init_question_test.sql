-- 创建题目表
CREATE TABLE IF NOT EXISTS question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '题目ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    knowledge_point VARCHAR(200) COMMENT '知识点',
    type INT NOT NULL COMMENT '题型 1-单选 2-多选 3-判断 4-简答',
    content TEXT NOT NULL COMMENT '题目内容',
    options TEXT COMMENT '选项JSON或文本',
    answer TEXT NOT NULL COMMENT '答案',
    score DECIMAL(5,2) NOT NULL DEFAULT 1 COMMENT '分值',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_course_id (course_id),
    INDEX idx_knowledge_point (knowledge_point)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- 创建测试表
CREATE TABLE IF NOT EXISTS test (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '测试ID',
    title VARCHAR(200) NOT NULL COMMENT '测试标题',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    total_score DECIMAL(6,2) DEFAULT 0 COMMENT '总分',
    duration INT COMMENT '考试时长(分钟)',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    status INT DEFAULT 1 COMMENT '状态 1-未开始 2-进行中 3-已结束',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_course_id (course_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试表';

-- 创建测试题目关联表
CREATE TABLE IF NOT EXISTS test_question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    test_id BIGINT NOT NULL COMMENT '测试ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    score DECIMAL(5,2) NOT NULL COMMENT '该题目在测试中的分值',
    sort_order INT DEFAULT 0 COMMENT '排序',
    INDEX idx_test_id (test_id),
    INDEX idx_question_id (question_id),
    UNIQUE KEY uk_test_question (test_id, question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试题目关联表';

SELECT '题库测试相关表创建完成！' AS result;
