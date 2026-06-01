package com.xujc.mvcpro.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 智谱AI配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "zhipuai.api")
public class ZhiPuAiConfig {
    
    /**
     * API密钥
     */
    private String key;
    
    /**
     * API请求地址
     */
    private String url;
    
    /**
     * 使用的模型
     */
    private String model;
}
