package com.xujc.mvcpro.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI对话历史实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiChatHistory {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 用户名
     */
    private String userName;
    
    /**
     * 用户问题
     */
    private String question;
    
    /**
     * AI回答
     */
    private String answer;
    
    /**
     * 使用的AI模型
     */
    private String model;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
