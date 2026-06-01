package com.xujc.mvcpro.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 教师答疑消息实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherMessage {
    private Long id;              // 消息ID
    private Integer studentId;    // 学生ID
    private String studentName;   // 学生姓名
    private Integer teacherId;    // 教师ID
    private String teacherName;   // 教师姓名
    private String content;       // 消息内容
    private String attachmentPath; // 附件存储路径
    private String attachmentName; // 附件原文件名
    private Integer status;       // 状态: 0-未读, 1-已读
    private Timestamp createTime; // 发送时间
}
