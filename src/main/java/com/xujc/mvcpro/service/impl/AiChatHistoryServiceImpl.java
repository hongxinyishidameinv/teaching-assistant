package com.xujc.mvcpro.service.impl;

import com.xujc.mvcpro.common.PageResult;
import com.xujc.mvcpro.mapper.AiChatHistoryMapper;
import com.xujc.mvcpro.pojo.AiChatHistory;
import com.xujc.mvcpro.service.AiChatHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI对话历史Service实现
 */
@Service
public class AiChatHistoryServiceImpl implements AiChatHistoryService {

    @Autowired
    private AiChatHistoryMapper aiChatHistoryMapper;

    @Override
    public void saveHistory(Integer userId, String userName, String question, String answer, String model) {
        try {
            AiChatHistory history = new AiChatHistory();
            history.setUserId(userId);
            history.setUserName(userName);
            history.setQuestion(question);
            history.setAnswer(answer);
            history.setModel(model);
            
            aiChatHistoryMapper.insert(history);
        } catch (Exception e) {
            System.err.println("保存对话历史失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public com.xujc.mvcpro.common.ApiResponse getHistoryByUserId(Integer userId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<AiChatHistory> histories = aiChatHistoryMapper.selectByUserId(userId, offset, size);
        int total = aiChatHistoryMapper.countByUserId(userId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("histories", histories);
        data.put("total", total);
        data.put("page", page);
        data.put("size", size);
        data.put("totalPages", (int) Math.ceil((double) total / size));
        
        return com.xujc.mvcpro.common.ApiResponse.ok("查询成功", data);
    }

    @Override
    public AiChatHistory getHistoryById(Long id) {
        return aiChatHistoryMapper.selectById(id);
    }

    @Override
    public void deleteHistory(Long id, Integer userId) {
        AiChatHistory history = aiChatHistoryMapper.selectById(id);
        if (history == null) {
            throw new RuntimeException("记录不存在");
        }
        if (!history.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除该记录");
        }
        aiChatHistoryMapper.deleteById(id);
    }

    @Override
    public void clearHistory(Integer userId) {
        aiChatHistoryMapper.deleteByUserId(userId);
    }
}
