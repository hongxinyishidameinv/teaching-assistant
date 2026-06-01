package com.xujc.mvcpro.service;

import com.xujc.mvcpro.common.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 教师回复Service接口
 */
public interface TeacherMessageReplyService {
    
    /**
     * 教师回复学生消息
     */
    ApiResponse replyMessage(Long messageId, Integer teacherId, String teacherName, 
                            Integer studentId, String studentName, String content, MultipartFile attachment);
    
    /**
     * 查询某条消息的所有回复
     */
    ApiResponse getMessageReplies(Long messageId);
    
    /**
     * 更新回复内容
     */
    ApiResponse updateReply(Long replyId, String content, MultipartFile attachment);
    
    /**
     * 删除回复
     */
    ApiResponse deleteReply(Long replyId, Integer userId, String userType);
}
