package com.xujc.mvcpro.service.impl;

import com.xujc.mvcpro.common.ApiResponse;
import com.xujc.mvcpro.mapper.TeacherMessageMapper;
import com.xujc.mvcpro.pojo.TeacherMessage;
import com.xujc.mvcpro.pojo.User;
import com.xujc.mvcpro.service.TeacherMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 教师答疑消息Service实现
 */
@Service
public class TeacherMessageServiceImpl implements TeacherMessageService {

    @Autowired
    private TeacherMessageMapper teacherMessageMapper;
    
    // 附件存储目录（使用绝对路径）
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/teacher-messages/";

    @Override
    public ApiResponse sendMessage(Integer studentId, String studentName, Integer teacherId, String teacherName, 
                                   String content, MultipartFile attachment) {
        try {
            System.out.println("=== 开始发送消息 ===");
            System.out.println("学生ID: " + studentId);
            System.out.println("教师ID: " + teacherId);
            System.out.println("内容: " + content);
            System.out.println("附件: " + (attachment != null ? attachment.getOriginalFilename() : "无"));
            
            if (content == null || content.trim().isEmpty()) {
                return ApiResponse.error(400, "消息内容不能为空");
            }
            
            TeacherMessage message = new TeacherMessage();
            message.setStudentId(studentId);
            message.setStudentName(studentName);
            message.setTeacherId(teacherId);
            message.setTeacherName(teacherName);
            message.setContent(content);
            message.setStatus(0); // 未读
            
            // 处理附件
            if (attachment != null && !attachment.isEmpty()) {
                System.out.println("开始处理附件...");
                String attachmentPath = saveAttachment(attachment);
                message.setAttachmentPath(attachmentPath);
                message.setAttachmentName(attachment.getOriginalFilename());
                System.out.println("附件保存成功: " + attachmentPath);
            }
            
            teacherMessageMapper.insert(message);
            
            System.out.println("消息发送成功: messageId=" + message.getId());
            System.out.println("=== 发送消息结束 ===");
            
            return ApiResponse.ok("发送成功", message);
        } catch (Exception e) {
            System.err.println("发送消息失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "发送失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse getStudentMessages(Integer studentId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<TeacherMessage> messages = teacherMessageMapper.selectByStudentId(studentId, offset, size);
        int total = teacherMessageMapper.countByStudentId(studentId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("messages", messages);
        data.put("total", total);
        data.put("page", page);
        data.put("size", size);
        data.put("totalPages", (int) Math.ceil((double) total / size));
        
        return ApiResponse.ok("查询成功", data);
    }

    @Override
    public ApiResponse getTeacherMessages(Integer teacherId, Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<TeacherMessage> messages = teacherMessageMapper.selectByTeacherId(teacherId, offset, size);
        int total = teacherMessageMapper.countByTeacherId(teacherId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("messages", messages);
        data.put("total", total);
        data.put("page", page);
        data.put("size", size);
        data.put("totalPages", (int) Math.ceil((double) total / size));
        
        return ApiResponse.ok("查询成功", data);
    }

    @Override
    public ApiResponse getMessageById(Long id) {
        TeacherMessage message = teacherMessageMapper.selectById(id);
        if (message == null) {
            return ApiResponse.error(404, "消息不存在");
        }
        return ApiResponse.ok("查询成功", message);
    }

    @Override
    public ApiResponse markAsRead(Long id) {
        int result = teacherMessageMapper.updateStatusToRead(id);
        if (result > 0) {
            return ApiResponse.ok("标记成功");
        } else {
            return ApiResponse.error(404, "消息不存在");
        }
    }

    @Override
    public ApiResponse searchTeachers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ApiResponse.error(400, "搜索关键词不能为空");
        }
        
        List<User> teachers = teacherMessageMapper.searchTeachers(keyword.trim());
        return ApiResponse.ok("查询成功", teachers);
    }

    @Override
    public ApiResponse deleteMessage(Long id, Integer userId, String userType) {
        TeacherMessage message = teacherMessageMapper.selectById(id);
        if (message == null) {
            return ApiResponse.error(404, "消息不存在");
        }
        
        // 验证权限：只能删除自己的消息
        if ("student".equals(userType) && !message.getStudentId().equals(userId)) {
            return ApiResponse.error(403, "无权限删除该消息");
        }
        if ("teacher".equals(userType) && !message.getTeacherId().equals(userId)) {
            return ApiResponse.error(403, "无权限删除该消息");
        }
        
        teacherMessageMapper.deleteById(id);
        
        // 删除附件文件
        if (message.getAttachmentPath() != null) {
            try {
                Files.deleteIfExists(Paths.get(message.getAttachmentPath()));
            } catch (IOException e) {
                System.err.println("删除附件文件失败: " + e.getMessage());
            }
        }
        
        return ApiResponse.ok("删除成功");
    }
    
    @Override
    public ApiResponse getUnreadCount(Integer teacherId) {
        try {
            int unreadCount = teacherMessageMapper.countUnreadByTeacherId(teacherId);
            Map<String, Object> data = new HashMap<>();
            data.put("unreadCount", unreadCount);
            return ApiResponse.ok("查询成功", data);
        } catch (Exception e) {
            System.err.println("查询未读消息数量失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存附件文件
     */
    private String saveAttachment(MultipartFile file) throws IOException {
        System.out.println("保存附件，文件名: " + file.getOriginalFilename());
        System.out.println("文件大小: " + file.getSize() + " bytes");
        
        // 创建上传目录（使用绝对路径）
        Path uploadPath = Paths.get(UPLOAD_DIR);
        System.out.println("上传目录: " + uploadPath.toAbsolutePath());
        
        if (!Files.exists(uploadPath)) {
            System.out.println("创建上传目录...");
            Files.createDirectories(uploadPath);
        }
        
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString() + extension;
        System.out.println("新文件名: " + newFilename);
        
        // 保存文件
        Path targetPath = uploadPath.resolve(newFilename);
        System.out.println("目标路径: " + targetPath.toAbsolutePath());
        
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        // 返回相对路径（用于URL访问）
        String relativePath = "/uploads/teacher-messages/" + newFilename;
        System.out.println("附件保存成功，URL路径: " + relativePath);
        
        return relativePath;
    }
}
