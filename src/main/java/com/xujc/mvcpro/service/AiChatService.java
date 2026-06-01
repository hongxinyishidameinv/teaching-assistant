package com.xujc.mvcpro.service;

/**
 * AI问答服务接口
 */
public interface AiChatService {
    
    /**
     * 发送消息到AI并获取回复
     * 
     * @param message 用户消息
     * @return AI回复内容
     */
    String chat(String message);
    
    /**
     * 发送带上下文的对话
     * 
     * @param messages 对话历史（JSON格式）
     * @return AI回复内容
     */
    String chatWithHistory(String messages);
}
