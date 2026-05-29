package com.xujc.mvcpro.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 学生选课实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentCourse {
    private Integer uid;          // 学生ID
    private String username;      // 学生姓名
    private String email;         // 学生邮箱
    private BigDecimal score;     // 成绩
    private Integer status;       // 选课状态
    private Timestamp createdAt;  // 选课时间
}
