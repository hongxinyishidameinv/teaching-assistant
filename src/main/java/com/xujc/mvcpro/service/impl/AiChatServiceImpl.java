package com.xujc.mvcpro.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xujc.mvcpro.config.ZhiPuAiConfig;
import com.xujc.mvcpro.service.AiChatHistoryService;
import com.xujc.mvcpro.service.AiChatService;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * AI问答服务实现类 - 调用智谱AI API
 */
@Service
public class AiChatServiceImpl implements AiChatService {

    private final ZhiPuAiConfig zhiPuAiConfig;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    
    @Autowired(required = false)
    private AiChatHistoryService aiChatHistoryService;

    // 使用构造函数注入
    public AiChatServiceImpl(ZhiPuAiConfig zhiPuAiConfig) {
        this.zhiPuAiConfig = zhiPuAiConfig;
        // 优化超时配置：连接10秒，读取120秒（给AI充足时间），写入10秒
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)  // 增加到120秒，避免超时
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
        
        System.out.println("=== AI服务初始化 ===");
        System.out.println("API Key: " + (zhiPuAiConfig.getKey() != null ? zhiPuAiConfig.getKey().substring(0, 20) + "..." : "null"));
        System.out.println("API URL: " + zhiPuAiConfig.getUrl());
        System.out.println("API Model: " + zhiPuAiConfig.getModel());
        System.out.println("超时配置: 连接10s, 读取120s, 写入10s");
        System.out.println("提示: 使用glm-4-flash模型，响应速度更快");
        System.out.println("===================");
    }

    @Override
    public String chat(String message) {
        return chatWithUser(null, null, message);
    }
    
    /**
     * 带用户信息的对话（用于保存历史）
     */
    public String chatWithUser(Integer userId, String userName, String message) {
        System.out.println("=== 开始AI对话 ===");
        System.out.println("收到消息: " + message);
        
        try {
            // 检查配置
            if (zhiPuAiConfig == null) {
                throw new RuntimeException("AI配置未初始化");
            }
            if (zhiPuAiConfig.getKey() == null || zhiPuAiConfig.getKey().isEmpty()) {
                throw new RuntimeException("API Key未配置");
            }
            
            // 构建请求体
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", zhiPuAiConfig.getModel());
            
            ArrayNode messagesArray = requestBody.putArray("messages");
            ObjectNode userMessage = messagesArray.addObject();
            userMessage.put("role", "user");
            userMessage.put("content", message);

            System.out.println("请求体: " + requestBody.toString());

            // 发送请求
            String response = sendRequest(requestBody.toString());
            
            // 解析响应
            String reply = parseResponse(response);
            System.out.println("AI回复: " + reply);
            System.out.println("=== AI对话结束 ===");
            
            // 保存历史记录
            if (aiChatHistoryService != null && userId != null) {
                aiChatHistoryService.saveHistory(userId, userName, message, reply, zhiPuAiConfig.getModel());
            }
            
            return reply;
        } catch (Exception e) {
            System.err.println("AI对话失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("AI对话失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String chatWithHistory(String messagesJson) {
        try {
            // 构建请求体
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", zhiPuAiConfig.getModel());
            
            // 解析传入的messages JSON
            JsonNode messagesNode = objectMapper.readTree(messagesJson);
            requestBody.set("messages", messagesNode);

            // 发送请求
            String response = sendRequest(requestBody.toString());
            
            // 解析响应
            return parseResponse(response);
        } catch (Exception e) {
            System.err.println("AI对话失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("AI对话失败: " + e.getMessage());
        }
    }

    /**
     * 发送HTTP请求到智谱AI
     */
    private String sendRequest(String jsonBody) throws IOException {
        System.out.println("发送AI请求: " + jsonBody);
        System.out.println("API URL: " + zhiPuAiConfig.getUrl());
        
        // OkHttp 4.x 使用不同的方法签名
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody, mediaType);

        Request request = new Request.Builder()
                .url(zhiPuAiConfig.getUrl())
                .header("Authorization", "Bearer " + zhiPuAiConfig.getKey())
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        System.out.println("请求头 Authorization: Bearer " + zhiPuAiConfig.getKey().substring(0, 20) + "...");

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "无响应";
            System.out.println("响应状态码: " + response.code());
            System.out.println("响应内容: " + responseBody);
            
            if (!response.isSuccessful()) {
                throw new IOException("请求失败: HTTP " + response.code() + " - " + responseBody);
            }
            
            return responseBody;
        }
    }

    /**
     * 解析智谱AI的响应
     */
    private String parseResponse(String jsonResponse) throws Exception {
        JsonNode root = objectMapper.readTree(jsonResponse);
        
        // 检查是否有错误
        if (root.has("error")) {
            String errorMessage = root.path("error").path("message").asText("未知错误");
            throw new RuntimeException("API错误: " + errorMessage);
        }
        
        // 提取回复内容
        if (root.has("choices") && root.path("choices").isArray()) {
            JsonNode firstChoice = root.path("choices").get(0);
            if (firstChoice.has("message")) {
                return firstChoice.path("message").path("content").asText("");
            }
        }
        
        throw new RuntimeException("无法解析AI响应");
    }
}
