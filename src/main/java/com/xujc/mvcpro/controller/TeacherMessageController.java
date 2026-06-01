package com.xujc.mvcpro.controller;

import com.xujc.mvcpro.common.ApiResponse;
import com.xujc.mvcpro.pojo.User;
import com.xujc.mvcpro.service.TeacherMessageReplyService;
import com.xujc.mvcpro.service.TeacherMessageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 教师答疑控制器
 * 支持学生端和教师端
 */
@RestController
@RequestMapping("/api/message")
public class TeacherMessageController {

    @Autowired
    private TeacherMessageService teacherMessageService;
    
    @Autowired
    private TeacherMessageReplyService teacherMessageReplyService;

    // ==================== 学生端API ====================
    
    /**
     * 学生搜索教师
     * GET /api/message/student/search?keyword=xxx
     */
    @GetMapping("/student/search")
    public ApiResponse searchTeachers(@RequestParam String keyword) {
        return teacherMessageService.searchTeachers(keyword);
    }
    
    /**
     * 学生发送消息给教师
     * POST /api/message/student/send
     */
    @PostMapping("/student/send")
    public ApiResponse sendMessage(
            @RequestParam Integer teacherId,
            @RequestParam String teacherName,
            @RequestParam String content,
            @RequestParam(required = false) MultipartFile attachment,
            @RequestParam Integer userId,
            @RequestParam String userName) {
        
        System.out.println("=== Controller接收请求 ===");
        System.out.println("teacherId: " + teacherId);
        System.out.println("teacherName: " + teacherName);
        System.out.println("content: " + content);
        System.out.println("userId: " + userId);
        System.out.println("userName: " + userName);
        System.out.println("attachment: " + (attachment != null ? attachment.getOriginalFilename() : "null"));
        
        try {
            return teacherMessageService.sendMessage(
                    userId, 
                    userName, 
                    teacherId, 
                    teacherName, 
                    content, 
                    attachment
            );
        } catch (Exception e) {
            System.err.println("Controller异常: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "发送失败: " + e.getMessage());
        }
    }
    
    /**
     * 学生查看自己的消息历史
     * GET /api/message/student/history?page=1&size=10
     */
    @GetMapping("/student/history")
    public ApiResponse getStudentHistory(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        return teacherMessageService.getStudentMessages(userId, page, size);
    }
    
    /**
     * 学生删除自己的消息
     * DELETE /api/message/student/{id}
     */
    @DeleteMapping("/student/{id}")
    public ApiResponse deleteStudentMessage(
            @PathVariable Long id, 
            @RequestParam Integer userId) {
        
        return teacherMessageService.deleteMessage(id, userId, "student");
    }
    
    /**
     * 学生查看消息详情
     * GET /api/message/student/{id}
     */
    @GetMapping("/student/{id}")
    public ApiResponse getStudentMessageDetail(
            @PathVariable Long id, 
            @RequestParam Integer userId) {
        
        // 获取消息详情
        ApiResponse messageResponse = teacherMessageService.getMessageById(id);
        if (messageResponse.getCode() != 200) {
            return messageResponse;
        }
        
        // 验证权限：只能查看自己的消息
        com.xujc.mvcpro.pojo.TeacherMessage message = (com.xujc.mvcpro.pojo.TeacherMessage) messageResponse.getData();
        if (!message.getStudentId().equals(userId)) {
            return ApiResponse.error(403, "无权限查看该消息");
        }
        
        // 获取该消息的所有回复
        ApiResponse repliesResponse = teacherMessageReplyService.getMessageReplies(id);
        
        // 合并数据
        Map<String, Object> data = new HashMap<>();
        data.put("message", messageResponse.getData());
        data.put("replies", repliesResponse.getData());
        
        return ApiResponse.ok("查询成功", data);
    }

    // ==================== 教师端API ====================
    
    /**
     * 教师查看收到的消息
     * GET /api/message/teacher/inbox?page=1&size=10
     */
    @GetMapping("/teacher/inbox")
    public ApiResponse getTeacherInbox(
            @RequestParam Integer teacherId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        return teacherMessageService.getTeacherMessages(teacherId, page, size);
    }
    
    /**
     * 教师查询未读消息数量
     * GET /api/message/teacher/unread-count
     */
    @GetMapping("/teacher/unread-count")
    public ApiResponse getUnreadCount(@RequestParam Integer teacherId) {
        return teacherMessageService.getUnreadCount(teacherId);
    }
    
    /**
     * 教师查看消息详情
     * GET /api/message/teacher/{id}
     */
    @GetMapping("/teacher/{id}")
    public ApiResponse getMessageDetail(
            @PathVariable Long id, 
            @RequestParam Integer teacherId) {
        
        // 先标记为已读
        teacherMessageService.markAsRead(id);
        
        // 获取消息详情
        ApiResponse messageResponse = teacherMessageService.getMessageById(id);
        if (messageResponse.getCode() != 200) {
            return messageResponse;
        }
        
        // 获取该消息的所有回复
        ApiResponse repliesResponse = teacherMessageReplyService.getMessageReplies(id);
        
        // 合并数据
        Map<String, Object> data = new HashMap<>();
        data.put("message", messageResponse.getData());
        data.put("replies", repliesResponse.getData());
        
        return ApiResponse.ok("查询成功", data);
    }
    
    /**
     * 教师回复学生消息
     * POST /api/message/teacher/reply
     */
    @PostMapping("/teacher/reply")
    public ApiResponse replyMessage(
            @RequestParam Long messageId,
            @RequestParam Integer studentId,
            @RequestParam String studentName,
            @RequestParam String content,
            @RequestParam(required = false) MultipartFile attachment,
            @RequestParam Integer teacherId,
            @RequestParam String teacherName) {
        
        return teacherMessageReplyService.replyMessage(
                messageId, 
                teacherId, 
                teacherName, 
                studentId, 
                studentName, 
                content, 
                attachment
        );
    }
    
    /**
     * 教师更新回复内容
     * POST /api/message/teacher/update-reply
     */
    @PostMapping("/teacher/update-reply")
    public ApiResponse updateReply(
            @RequestParam Long replyId,
            @RequestParam String content,
            @RequestParam(required = false) MultipartFile attachment) {
        
        return teacherMessageReplyService.updateReply(replyId, content, attachment);
    }
    
    /**
     * 教师删除消息
     * DELETE /api/message/teacher/{id}
     */
    @DeleteMapping("/teacher/{id}")
    public ApiResponse deleteTeacherMessage(
            @PathVariable Long id, 
            @RequestParam Integer teacherId) {
        
        return teacherMessageService.deleteMessage(id, teacherId, "teacher");
    }
}
