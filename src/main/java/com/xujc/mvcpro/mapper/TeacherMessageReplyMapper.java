package com.xujc.mvcpro.mapper;

import com.xujc.mvcpro.pojo.TeacherMessageReply;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 教师回复Mapper
 */
@Mapper
public interface TeacherMessageReplyMapper {
    
    /**
     * 插入回复
     */
    @Insert("INSERT INTO teacher_message_reply(message_id, teacher_id, teacher_name, student_id, student_name, content, attachment_path, attachment_name, status) " +
            "VALUES(#{messageId}, #{teacherId}, #{teacherName}, #{studentId}, #{studentName}, #{content}, #{attachmentPath}, #{attachmentName}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TeacherMessageReply reply);
    
    /**
     * 查询某条消息的所有回复
     */
    @Select("SELECT * FROM teacher_message_reply WHERE message_id = #{messageId} ORDER BY create_time ASC")
    List<TeacherMessageReply> selectByMessageId(@Param("messageId") Long messageId);
    
    /**
     * 查询学生收到的回复（分页）
     */
    @Select("SELECT * FROM teacher_message_reply WHERE student_id = #{studentId} ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    List<TeacherMessageReply> selectByStudentId(@Param("studentId") Integer studentId, 
                                                 @Param("offset") int offset, 
                                                 @Param("size") int size);
    
    /**
     * 查询学生收到的回复总数
     */
    @Select("SELECT COUNT(*) FROM teacher_message_reply WHERE student_id = #{studentId}")
    int countByStudentId(@Param("studentId") Integer studentId);
    
    /**
     * 查询教师发送的回复（分页）
     */
    @Select("SELECT * FROM teacher_message_reply WHERE teacher_id = #{teacherId} ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    List<TeacherMessageReply> selectByTeacherId(@Param("teacherId") Integer teacherId, 
                                                 @Param("offset") int offset, 
                                                 @Param("size") int size);
    
    /**
     * 查询教师发送的回复总数
     */
    @Select("SELECT COUNT(*) FROM teacher_message_reply WHERE teacher_id = #{teacherId}")
    int countByTeacherId(@Param("teacherId") Integer teacherId);
    
    /**
     * 根据ID查询回复
     */
    @Select("SELECT * FROM teacher_message_reply WHERE id = #{id}")
    TeacherMessageReply selectById(@Param("id") Long id);
    
    /**
     * 更新回复状态为已读
     */
    @Update("UPDATE teacher_message_reply SET status = 1 WHERE id = #{id}")
    int updateStatusToRead(@Param("id") Long id);
    
    /**
     * 删除回复
     */
    @Delete("DELETE FROM teacher_message_reply WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    /**
     * 更新回复内容
     */
    @Update("UPDATE teacher_message_reply SET content = #{content}, attachment_path = #{attachmentPath}, " +
            "attachment_name = #{attachmentName} WHERE id = #{id}")
    int updateReply(TeacherMessageReply reply);
    
    /**
     * 查询某条消息的最新回复
     */
    @Select("SELECT * FROM teacher_message_reply WHERE message_id = #{messageId} ORDER BY create_time DESC LIMIT 1")
    TeacherMessageReply selectLatestByMessageId(@Param("messageId") Long messageId);
}
