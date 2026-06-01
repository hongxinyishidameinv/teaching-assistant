package com.xujc.mvcpro.service;

import com.xujc.mvcpro.common.ApiResponse;
import com.xujc.mvcpro.pojo.AiChatHistory;

/**
 * AI对话历史Service
 */
public interface AiChatHistoryService {
    
    /**
     * 保存对话记录
     */
    void saveHistory(Integer userId, String userName, String question, String answer, String model);
    
    /**
     * 分页查询对话历史
     */
    ApiResponse getHistoryByUserId(Integer userId, Integer page, Integer size);
    
    /**
     * 根据ID查询对话记录
     */
    AiChatHistory getHistoryById(Long id);
    
    /**
     * 删除单条历史记录
     */
    void deleteHistory(Long id, Integer userId);
    
    /**
     * 清空用户所有历史记录
     */
    void clearHistory(Integer userId);
}
