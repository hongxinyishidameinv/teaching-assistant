package com.xujc.mvcpro.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private Integer cid;          // 课程ID
    private String courseName;    // 课程名称
    private Double credits;       // 学分
    private Integer hours;        // 课时
    private Integer teacherId;    // 授课教师ID
    private String teacherName;   // 授课教师姓名（用于展示）
    private Integer status;       // 课程状态：0-禁用，1-启用
    private String description;   // 课程描述
    private Boolean selected;     // 是否已选（用于选课页面）
}