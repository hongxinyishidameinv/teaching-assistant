package com.xujc.mvcpro.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private Long id;
    private Long courseId;
    private String knowledgePoint;
    private Integer type;
    private String content;
    private String options;
    private String answer;
    private BigDecimal score;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    private String courseName;
}
