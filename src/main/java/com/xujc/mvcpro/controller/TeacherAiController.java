package com.xujc.mvcpro.controller;

import com.xujc.mvcpro.common.ApiResponse;
import com.xujc.mvcpro.pojo.AiChatHistory;
import com.xujc.mvcpro.pojo.User;
import com.xujc.mvcpro.service.AiChatHistoryService;
import com.xujc.mvcpro.service.AiChatService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 教师端AI问答控制器
 */
@RestController
@RequestMapping("/api/teacher/ai")
public class TeacherAiController {

    @Autowired
    private AiChatService aiChatService;
    
    @Autowired
    private AiChatHistoryService aiChatHistoryService;

    /**
     * 简单问答接口
     * POST /api/teacher/ai/chat
     * 
     * @param request 包含message和userId字段
     * @return AI回复
     */
    @PostMapping("/chat")
    public ApiResponse chat(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            String userIdStr = request.get("userId");
            
            if (message == null || message.trim().isEmpty()) {
                return ApiResponse.error(400, "消息不能为空");
            }
            
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                return ApiResponse.error(400, "请提供用户ID");
            }

            Integer userId = Integer.parseInt(userIdStr);
            System.out.println("=== AI聊天 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("消息: " + message);

            // 调用AI服务并保存历史
            String reply = ((com.xujc.mvcpro.service.impl.AiChatServiceImpl) aiChatService)
                    .chatWithUser(userId, "user_" + userId, message);
            System.out.println("AI回复: " + reply);
            System.out.println("=== AI聊天结束 ===");
            
            return ApiResponse.ok("获取成功", Map.of("reply", reply));
        } catch (NumberFormatException e) {
            System.err.println("用户ID格式错误: " + e.getMessage());
            return ApiResponse.error(400, "用户ID格式错误");
        } catch (Exception e) {
            System.err.println("AI问答失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "AI问答失败: " + e.getMessage());
        }
    }

    /**
     * 带上下文的对话接口
     * POST /api/teacher/ai/chat-with-history
     * 
     * @param request 包含messages字段（JSON数组格式）
     * @return AI回复
     */
    @PostMapping("/chat-with-history")
    public ApiResponse chatWithHistory(@RequestBody Map<String, String> request) {
        try {
            String messages = request.get("messages");
            if (messages == null || messages.trim().isEmpty()) {
                return ApiResponse.error(400, "对话历史不能为空");
            }

            String reply = aiChatService.chatWithHistory(messages);
            return ApiResponse.ok("获取成功", Map.of("reply", reply));
        } catch (Exception e) {
            System.err.println("AI对话失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "AI对话失败: " + e.getMessage());
        }
    }

    /**
     * 测试AI连接
     * GET /api/teacher/ai/test
     */
    @GetMapping("/test")
    public ApiResponse testConnection() {
        try {
            String reply = aiChatService.chat("你好，请简单回复测试一下。");
            return ApiResponse.ok("AI连接正常", Map.of("reply", reply));
        } catch (Exception e) {
            System.err.println("AI连接测试失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "AI连接测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取单条对话记录详情
     * GET /api/teacher/ai/history/detail/{id}
     */
    @GetMapping("/history/detail/{id}")
    public ApiResponse getHistoryDetail(@PathVariable Long id) {
        try {
            System.out.println("=== 获取历史记录详情 ===");
            System.out.println("记录ID: " + id);
            
            AiChatHistory history = aiChatHistoryService.getHistoryById(id);
            System.out.println("查询到的记录: " + history);
            
            if (history == null) {
                System.out.println("记录不存在");
                return ApiResponse.error(404, "记录不存在");
            }
            
            // 只返回需要的字段，避免序列化问题
            Map<String, Object> data = new HashMap<>();
            data.put("id", history.getId());
            data.put("userId", history.getUserId());
            data.put("userName", history.getUserName());
            data.put("question", history.getQuestion());
            data.put("answer", history.getAnswer());
            data.put("model", history.getModel());
            data.put("createTime", history.getCreateTime());
            
            System.out.println("返回数据: " + data);
            System.out.println("=== 获取历史记录详情结束 ===");
            
            return ApiResponse.ok("查询成功", data);
        } catch (Exception e) {
            System.err.println("获取历史记录详情失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取对话历史
     * GET /api/teacher/ai/history?userId=1&page=1&size=10
     */
    @GetMapping("/history")
    public ApiResponse getHistory(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            System.out.println("=== 开始获取对话历史 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("页码: " + page + ", 每页: " + size);
            
            if (userId == null) {
                System.out.println("错误: 未提供用户ID");
                return ApiResponse.error(400, "请提供用户ID");
            }
            
            ApiResponse result = aiChatHistoryService.getHistoryByUserId(userId, page, size);
            System.out.println("查询结果: " + result);
            System.out.println("=== 获取对话历史结束 ===");
            
            return result;
        } catch (Exception e) {
            System.err.println("获取对话历史失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "获取对话历史失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除对话记录
     * DELETE /api/teacher/ai/history/{id}?userId=1
     */
    @DeleteMapping("/history/{id}")
    public ApiResponse deleteHistory(
            @PathVariable Long id,
            @RequestParam Integer userId) {
        try {
            System.out.println("删除历史记录: id=" + id + ", userId=" + userId);
            
            if (userId == null) {
                return ApiResponse.error(400, "请提供用户ID");
            }
            
            aiChatHistoryService.deleteHistory(id, userId);
            return ApiResponse.ok("删除成功");
        } catch (Exception e) {
            System.err.println("删除对话记录失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 清空所有对话历史
     * DELETE /api/teacher/ai/history?userId=1
     */
    @DeleteMapping("/history")
    public ApiResponse clearHistory(@RequestParam Integer userId) {
        try {
            System.out.println("清空历史记录: userId=" + userId);
            
            if (userId == null) {
                return ApiResponse.error(400, "请提供用户ID");
            }
            
            aiChatHistoryService.clearHistory(userId);
            return ApiResponse.ok("清空成功");
        } catch (Exception e) {
            System.err.println("清空对话历史失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "清空失败: " + e.getMessage());
        }
    }
}
