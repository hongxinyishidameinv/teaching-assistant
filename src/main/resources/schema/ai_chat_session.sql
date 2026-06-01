-- AI对话历史表（按会话保存）
CREATE TABLE IF NOT EXISTS `ai_chat_session` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `user_name` VARCHAR(100) DEFAULT NULL COMMENT '用户名',
  `title` VARCHAR(500) DEFAULT NULL COMMENT '会话标题（第一个问题）',
  `conversation` JSON NOT NULL COMMENT '完整对话内容（JSON数组）',
  `message_count` INT DEFAULT 0 COMMENT '消息数量',
  `model` VARCHAR(50) DEFAULT NULL COMMENT '使用的AI模型',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话会话表';
