package com.xujc.mvcpro.service.impl;

import com.xujc.mvcpro.common.ApiResponse;
import com.xujc.mvcpro.mapper.TeacherMessageMapper;
import com.xujc.mvcpro.mapper.TeacherMessageReplyMapper;
import com.xujc.mvcpro.pojo.TeacherMessage;
import com.xujc.mvcpro.pojo.TeacherMessageReply;
import com.xujc.mvcpro.service.TeacherMessageReplyService;
import com.xujc.mvcpro.service.TeacherMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
 * 教师回复Service实现类
 */
@Service
public class TeacherMessageReplyServiceImpl implements TeacherMessageReplyService {

    @Autowired
    private TeacherMessageReplyMapper teacherMessageReplyMapper;
    
    @Autowired
    private TeacherMessageMapper teacherMessageMapper;
    
    // 附件存储目录（使用绝对路径）
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/teacher-messages/";

    @Override
    public ApiResponse replyMessage(Long messageId, Integer teacherId, String teacherName,
                                   Integer studentId, String studentName, String content, MultipartFile attachment) {
        try {
            System.out.println("=== 开始回复消息 ===");
            System.out.println("消息ID: " + messageId);
            System.out.println("教师ID: " + teacherId);
            System.out.println("学生ID: " + studentId);
            System.out.println("内容: " + content);
            
            if (content == null || content.trim().isEmpty()) {
                return ApiResponse.error(400, "回复内容不能为空");
            }
            
            // 验证消息是否存在
            TeacherMessage message = teacherMessageMapper.selectById(messageId);
            if (message == null) {
                return ApiResponse.error(404, "消息不存在");
            }
            
            // 创建回复
            TeacherMessageReply reply = new TeacherMessageReply();
            reply.setMessageId(messageId);
            reply.setTeacherId(teacherId);
            reply.setTeacherName(teacherName);
            reply.setStudentId(studentId);
            reply.setStudentName(studentName);
            reply.setContent(content);
            reply.setStatus(0); // 未读
            
            // 处理附件
            if (attachment != null && !attachment.isEmpty()) {
                System.out.println("开始处理附件...");
                String attachmentPath = saveAttachment(attachment);
                reply.setAttachmentPath(attachmentPath);
                reply.setAttachmentName(attachment.getOriginalFilename());
                System.out.println("附件保存成功: " + attachmentPath);
            }
            
            teacherMessageReplyMapper.insert(reply);
            
            // 更新原消息状态为已回复
            teacherMessageMapper.updateStatusToRead(messageId);
            
            System.out.println("回复成功: replyId=" + reply.getId());
            System.out.println("=== 回复消息结束 ===");
            
            return ApiResponse.ok("回复成功", reply);
        } catch (Exception e) {
            System.err.println("回复消息失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "回复失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse getMessageReplies(Long messageId) {
        try {
            List<TeacherMessageReply> replies = teacherMessageReplyMapper.selectByMessageId(messageId);
            return ApiResponse.ok("查询成功", replies);
        } catch (Exception e) {
            System.err.println("查询回复失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "查询失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse updateReply(Long replyId, String content, MultipartFile attachment) {
        try {
            System.out.println("=== 开始更新回复 ===");
            System.out.println("回复ID: " + replyId);
            System.out.println("新内容: " + content);
            
            if (content == null || content.trim().isEmpty()) {
                return ApiResponse.error(400, "回复内容不能为空");
            }
            
            // 查询回复是否存在
            TeacherMessageReply reply = teacherMessageReplyMapper.selectById(replyId);
            if (reply == null) {
                return ApiResponse.error(404, "回复不存在");
            }
            
            // 更新内容
            reply.setContent(content);
            
            // 处理新附件
            if (attachment != null && !attachment.isEmpty()) {
                System.out.println("开始处理新附件...");
                String attachmentPath = saveAttachment(attachment);
                reply.setAttachmentPath(attachmentPath);
                reply.setAttachmentName(attachment.getOriginalFilename());
                System.out.println("新附件保存成功: " + attachmentPath);
            }
            
            int result = teacherMessageReplyMapper.updateReply(reply);
            
            if (result > 0) {
                System.out.println("更新回复成功");
                System.out.println("=== 更新回复结束 ===");
                return ApiResponse.ok("更新成功", reply);
            } else {
                return ApiResponse.error(404, "更新失败");
            }
        } catch (Exception e) {
            System.err.println("更新回复失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "更新失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse deleteReply(Long replyId, Integer userId, String userType) {
        try {
            TeacherMessageReply reply = teacherMessageReplyMapper.selectById(replyId);
            if (reply == null) {
                return ApiResponse.error(404, "回复不存在");
            }
            
            // 验证权限：只能删除自己的回复
            if ("teacher".equals(userType) && !reply.getTeacherId().equals(userId)) {
                return ApiResponse.error(403, "无权限删除该回复");
            }
            
            teacherMessageReplyMapper.deleteById(replyId);
            
            // 删除附件文件
            if (reply.getAttachmentPath() != null) {
                try {
                    Files.deleteIfExists(Paths.get(reply.getAttachmentPath()));
                } catch (IOException e) {
                    System.err.println("删除附件文件失败: " + e.getMessage());
                }
            }
            
            return ApiResponse.ok("删除成功");
        } catch (Exception e) {
            System.err.println("删除回复失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "删除失败: " + e.getMessage());
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
