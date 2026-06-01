-- 教师答疑消息表
CREATE TABLE IF NOT EXISTS `teacher_message` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    `student_id` INT NOT NULL COMMENT '学生ID',
    `student_name` VARCHAR(50) NOT NULL COMMENT '学生姓名',
    `teacher_id` INT NOT NULL COMMENT '教师ID',
    `teacher_name` VARCHAR(50) NOT NULL COMMENT '教师姓名',
    `content` TEXT COMMENT '消息内容',
    `attachment_path` VARCHAR(500) COMMENT '附件存储路径',
    `attachment_name` VARCHAR(200) COMMENT '附件原文件名',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-未读, 1-已读',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    INDEX `idx_student_id` (`student_id`),
    INDEX `idx_teacher_id` (`teacher_id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师答疑消息表';

-- 教师回复表
CREATE TABLE IF NOT EXISTS `teacher_message_reply` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '回复ID',
    `message_id` BIGINT NOT NULL COMMENT '关联的消息ID',
    `teacher_id` INT NOT NULL COMMENT '教师ID',
    `teacher_name` VARCHAR(50) NOT NULL COMMENT '教师姓名',
    `student_id` INT NOT NULL COMMENT '学生ID',
    `student_name` VARCHAR(50) NOT NULL COMMENT '学生姓名',
    `content` TEXT COMMENT '回复内容',
    `attachment_path` VARCHAR(500) COMMENT '附件存储路径',
    `attachment_name` VARCHAR(200) COMMENT '附件原文件名',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-未读, 1-已读',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '回复时间',
    INDEX `idx_message_id` (`message_id`),
    INDEX `idx_teacher_id` (`teacher_id`),
    INDEX `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师回复表';
