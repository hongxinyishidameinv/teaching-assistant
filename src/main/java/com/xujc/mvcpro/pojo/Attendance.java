package com.xujc.mvcpro.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
    private Long id;
    private Long courseId;
    private Integer studentId;
    private LocalDate attendanceDate;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    
    // 扩展字段
    private String courseName;
    private String studentName;
    private String studentNo;
}
