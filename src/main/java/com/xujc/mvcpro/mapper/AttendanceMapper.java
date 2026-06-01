package com.xujc.mvcpro.mapper;

import com.xujc.mvcpro.pojo.Attendance;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AttendanceMapper {

    @Insert("INSERT INTO attendance (course_id, student_id, attendance_date, status, remark, create_time) VALUES (#{courseId}, #{studentId}, #{attendanceDate}, #{status}, #{remark}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Attendance attendance);

    @Update("UPDATE attendance SET status = #{status}, remark = #{remark} WHERE id = #{id}")
    void update(Attendance attendance);

    @Delete("DELETE FROM attendance WHERE id = #{id}")
    void deleteById(Long id);

    @Select("SELECT * FROM attendance WHERE id = #{id}")
    Attendance selectById(Long id);

    @Select("SELECT * FROM attendance")
    List<Attendance> selectAll();
    
    @Select("SELECT * FROM attendance WHERE course_id = #{courseId} AND attendance_date = #{attendanceDate}")
    List<Attendance> selectByCourseAndDate(@Param("courseId") Long courseId, @Param("attendanceDate") LocalDate attendanceDate);

    @Select("SELECT a.*, c.course_name AS courseName, u.username AS studentName, u.uid AS studentNo FROM attendance a LEFT JOIN course c ON a.course_id = c.id LEFT JOIN user u ON a.student_id = u.uid WHERE (a.course_id = #{courseId} OR #{courseId} IS NULL OR #{courseId} = 0) AND (a.student_id = #{studentId} OR #{studentId} IS NULL OR #{studentId} = 0) AND (u.username LIKE CONCAT('%', #{studentName}, '%') OR #{studentName} IS NULL OR #{studentName} = '') AND (a.attendance_date >= #{startDate} OR #{startDate} IS NULL) AND (a.attendance_date <= #{endDate} OR #{endDate} IS NULL) AND (a.status = #{status} OR #{status} IS NULL OR #{status} = 0) ORDER BY a.attendance_date DESC, a.create_time DESC LIMIT #{offset}, #{size}")
    List<Attendance> selectPage(
            @Param("courseId") Long courseId,
            @Param("studentId") Integer studentId,
            @Param("studentName") String studentName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") Integer status,
            @Param("offset") int offset,
            @Param("size") int size
    );

    @Select("SELECT COUNT(*) FROM attendance a LEFT JOIN user u ON a.student_id = u.uid WHERE (a.course_id = #{courseId} OR #{courseId} IS NULL OR #{courseId} = 0) AND (a.student_id = #{studentId} OR #{studentId} IS NULL OR #{studentId} = 0) AND (u.username LIKE CONCAT('%', #{studentName}, '%') OR #{studentName} IS NULL OR #{studentName} = '') AND (a.attendance_date >= #{startDate} OR #{startDate} IS NULL) AND (a.attendance_date <= #{endDate} OR #{endDate} IS NULL) AND (a.status = #{status} OR #{status} IS NULL OR #{status} = 0)")
    int count(
            @Param("courseId") Long courseId,
            @Param("studentId") Integer studentId,
            @Param("studentName") String studentName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") Integer status
    );

    @Delete("DELETE FROM attendance WHERE course_id = #{courseId} AND attendance_date = #{attendanceDate}")
    int deleteByCourseAndDate(@Param("courseId") Long courseId, @Param("attendanceDate") LocalDate attendanceDate);

    @Select("SELECT a.*, c.course_name AS courseName, u.username AS studentName, u.uid AS studentNo FROM attendance a LEFT JOIN course c ON a.course_id = c.id LEFT JOIN user u ON a.student_id = u.uid WHERE a.student_id = #{studentId} AND (a.course_id = #{courseId} OR #{courseId} IS NULL OR #{courseId} = 0) AND (a.attendance_date >= #{startDate} OR #{startDate} IS NULL) AND (a.attendance_date <= #{endDate} OR #{endDate} IS NULL) AND (a.status = #{status} OR #{status} IS NULL OR #{status} = 0) ORDER BY a.attendance_date DESC, a.create_time DESC LIMIT #{offset}, #{size}")
    List<Attendance> selectByStudentId(
            @Param("studentId") Integer studentId,
            @Param("courseId") Long courseId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") Integer status,
            @Param("offset") int offset,
            @Param("size") int size
    );

    @Select("SELECT COUNT(*) FROM attendance a WHERE a.student_id = #{studentId} AND (a.course_id = #{courseId} OR #{courseId} IS NULL OR #{courseId} = 0) AND (a.attendance_date >= #{startDate} OR #{startDate} IS NULL) AND (a.attendance_date <= #{endDate} OR #{endDate} IS NULL) AND (a.status = #{status} OR #{status} IS NULL OR #{status} = 0)")
    int countByStudentId(
            @Param("studentId") Integer studentId,
            @Param("courseId") Long courseId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") Integer status
    );

    @Select("SELECT COUNT(*) FROM attendance a WHERE a.student_id = #{studentId} AND a.status = #{status}")
    int countByStudentIdAndStatus(@Param("studentId") Integer studentId, @Param("status") Integer status);
}
