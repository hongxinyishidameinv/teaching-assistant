package com.xujc.mvcpro.service;

import com.xujc.mvcpro.common.ApiResponse;
import com.xujc.mvcpro.pojo.TeacherMessage;
import org.springframework.web.multipart.MultipartFile;

/**
 * 教师答疑消息Service
 */
public interface TeacherMessageService {
    
    /**
     * 学生发送消息给教师（支持附件）
     */
    ApiResponse sendMessage(Integer studentId, String studentName, Integer teacherId, String teacherName, 
                           String content, MultipartFile attachment);
    
    /**
     * 查询学生发送的消息历史
     */
    ApiResponse getStudentMessages(Integer studentId, Integer page, Integer size);
    
    /**
     * 查询教师收到的消息
     */
    ApiResponse getTeacherMessages(Integer teacherId, Integer page, Integer size);
    
    /**
     * 获取消息详情
     */
    ApiResponse getMessageById(Long id);
    
    /**
     * 标记消息为已读
     */
    ApiResponse markAsRead(Long id);
    
    /**
     * 搜索教师
     */
    ApiResponse searchTeachers(String keyword);
    
    /**
     * 删除消息
     */
    ApiResponse deleteMessage(Long id, Integer userId, String userType);
    
    /**
     * 查询教师未读消息数量
     */
    ApiResponse getUnreadCount(Integer teacherId);
}
