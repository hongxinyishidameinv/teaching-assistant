package com.xujc.mvcpro.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Notice {
    private Long id;
    private String title;
    private String content;
    private Integer top;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}