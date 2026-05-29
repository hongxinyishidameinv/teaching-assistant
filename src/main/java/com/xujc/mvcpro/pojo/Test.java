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
public class Test {
    private Long id;
    private String title;
    private Long courseId;
    private BigDecimal totalScore;
    private Integer duration;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    private Integer status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    private String courseName;
    private Integer questionCount;
    
    // 学生端使用的字段
    private Boolean submitted;
    private Integer studentScore;
    private Boolean allGraded;
}
