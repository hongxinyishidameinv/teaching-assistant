package com.xujc.mvcpro.mapper;

import com.xujc.mvcpro.pojo.TeacherMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 教师答疑消息Mapper
 */
@Mapper
public interface TeacherMessageMapper {
    
    /**
     * 插入消息
     */
    @Insert("INSERT INTO teacher_message(student_id, student_name, teacher_id, teacher_name, content, attachment_path, attachment_name, status) " +
            "VALUES(#{studentId}, #{studentName}, #{teacherId}, #{teacherName}, #{content}, #{attachmentPath}, #{attachmentName}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TeacherMessage message);
    
    /**
     * 查询学生发送的消息（分页）
     */
    @Select("SELECT * FROM teacher_message WHERE student_id = #{studentId} ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    List<TeacherMessage> selectByStudentId(@Param("studentId") Integer studentId, 
                                           @Param("offset") int offset, 
                                           @Param("size") int size);
    
    /**
     * 查询学生发送的消息总数
     */
    @Select("SELECT COUNT(*) FROM teacher_message WHERE student_id = #{studentId}")
    int countByStudentId(@Param("studentId") Integer studentId);
    
    /**
     * 查询教师收到的消息（分页）
     */
    @Select("SELECT * FROM teacher_message WHERE teacher_id = #{teacherId} ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    List<TeacherMessage> selectByTeacherId(@Param("teacherId") Integer teacherId, 
                                           @Param("offset") int offset, 
                                           @Param("size") int size);
    
    /**
     * 查询教师收到的消息总数
     */
    @Select("SELECT COUNT(*) FROM teacher_message WHERE teacher_id = #{teacherId}")
    int countByTeacherId(@Param("teacherId") Integer teacherId);
    
    /**
     * 查询教师未读消息数量
     */
    @Select("SELECT COUNT(*) FROM teacher_message WHERE teacher_id = #{teacherId} AND status = 0")
    int countUnreadByTeacherId(@Param("teacherId") Integer teacherId);
    
    /**
     * 根据ID查询消息
     */
    @Select("SELECT * FROM teacher_message WHERE id = #{id}")
    TeacherMessage selectById(@Param("id") Long id);
    
    /**
     * 更新消息状态为已读
     */
    @Update("UPDATE teacher_message SET status = 1 WHERE id = #{id}")
    int updateStatusToRead(@Param("id") Long id);
    
    /**
     * 删除消息
     */
    @Delete("DELETE FROM teacher_message WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    /**
     * 搜索教师（根据姓名）
     */
    @Select("SELECT uid, username, email FROM user WHERE type = 1 AND username LIKE CONCAT('%', #{keyword}, '%') LIMIT 20")
    List<com.xujc.mvcpro.pojo.User> searchTeachers(@Param("keyword") String keyword);
}
