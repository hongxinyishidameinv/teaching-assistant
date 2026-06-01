package com.xujc.mvcpro.mapper;

import com.xujc.mvcpro.pojo.Course;
import com.xujc.mvcpro.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface CourseMapper {

    @Select("select c.id as cid, c.course_name as courseName, c.credit as credits, c.hours, c.teacher_id as teacherId, u.username as teacherName, c.status, c.description, c.course_code as courseCode from course c left join user u on c.teacher_id = u.uid limit #{offset}, #{pageSize}")
    List<Course> findCoursesByPage(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    @Select("select count(*) from course")
    int countCourses();

    @Select("<script>select c.id as cid, c.course_name as courseName, c.credit as credits, c.hours, c.teacher_id as teacherId, u.username as teacherName, c.status, c.description, c.course_code as courseCode from course c left join user u on c.teacher_id = u.uid where 1=1" +
            "<if test='keyword != null and keyword != \"%\"'> and (c.course_name like #{keyword} or u.username like #{keyword})</if>" +
            "<if test='status != null'> and c.status = #{status}</if>" +
            " limit #{offset}, #{pageSize}</script>")
    List<Course> searchCoursesByPage(@Param("keyword") String keyword, @Param("status") Integer status, 
                                     @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    @Select("<script>select count(*) from course where 1=1" +
            "<if test='keyword != null and keyword != \"%\"'> and (course_name like #{keyword} or exists (select 1 from user u where u.uid = course.teacher_id and u.username like #{keyword}))</if>" +
            "<if test='status != null'> and status = #{status}</if></script>")
    int countSearchCourses(@Param("keyword") String keyword, @Param("status") Integer status);

    @Select("select c.id as cid, c.course_name as courseName, c.credit as credits, c.hours, c.teacher_id as teacherId, u.username as teacherName, c.status, c.description, c.course_code as courseCode from course c left join user u on c.teacher_id = u.uid where c.id = #{cid}")
    Course findById(@Param("cid") Integer cid);

    @Insert("insert into course(course_name, credit, hours, teacher_id, status, description, course_code) " +
            "values(#{courseName}, #{credits}, #{hours}, #{teacherId}, #{status}, #{description}, #{courseCode})")
    int insertCourse(Course course);

    @Update("update course set course_name=#{courseName}, credit=#{credits}, hours=#{hours}, " +
            "teacher_id=#{teacherId}, status=#{status}, description=#{description} where id=#{cid}")
    int updateCourse(Course course);

    @Delete("delete from course where id=#{cid}")
    int deleteCourse(@Param("cid") Integer cid);

    @Delete("<script>delete from course where id in (<foreach collection='cids' item='cid' separator=','>#{cid}</foreach>)</script>")
    int batchDeleteCourses(@Param("cids") List<Integer> cids);

    @Update("update course set status=#{status} where id=#{cid}")
    int updateCourseStatus(@Param("cid") Integer cid, @Param("status") Integer status);

    @Select("select uid, username, password, email, type, status from user")
    List<User> findAllTeachers();

    @Select("select count(*) from student_course where course_id = #{cid}")
    int countStudentCourses(@Param("cid") Integer cid);

    /**
     * 根据教师ID查询课程列表
     */
    @Select("select c.id as cid, c.course_name as courseName, c.credit as credits, c.hours, " +
            "c.teacher_id as teacherId, u.username as teacherName, c.status, c.description, c.course_code as courseCode " +
            "from course c left join user u on c.teacher_id = u.uid " +
            "where c.teacher_id = #{teacherId}")
    List<Course> findCoursesByTeacherId(@Param("teacherId") Integer teacherId);

    /**
     * 查询课程的学生列表（分页）
     */
    @Select("select u.uid, u.username, u.email, sc.score, sc.status, sc.created_at as createdAt " +
            "from student_course sc join user u on sc.student_id = u.uid " +
            "where sc.course_id = #{courseId} and sc.status = 1 " +
            "limit #{offset}, #{pageSize}")
    List<com.xujc.mvcpro.pojo.StudentCourse> findStudentsByCourseId(
            @Param("courseId") Integer courseId,
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize);

    /**
     * 查询课程的学生总数
     */
    @Select("select count(*) from student_course sc " +
            "where sc.course_id = #{courseId} and sc.status = 1")
    int countStudentsByCourseId(@Param("courseId") Integer courseId);

    /**
     * 根据学生ID查询该学生的选课列表
     */
    @Select("select c.id as cid, c.course_name as courseName, c.credit as credits, c.hours, " +
            "c.teacher_id as teacherId, u.username as teacherName, c.status, c.description, c.course_code as courseCode " +
            "from student_course sc " +
            "join course c on sc.course_id = c.id " +
            "left join user u on c.teacher_id = u.uid " +
            "where sc.student_id = #{studentId} and sc.status = 1")
    List<Course> findCoursesByStudentId(@Param("studentId") Integer studentId);

    /**
     * 获取所有课程列表
     */
    @Select("select c.id as cid, c.course_name as courseName, c.credit as credits, c.hours, " +
            "c.teacher_id as teacherId, u.username as teacherName, c.status, c.description, c.course_code as courseCode " +
            "from course c left join user u on c.teacher_id = u.uid " +
            "where c.status = 1")
    List<Course> findAllCourses();

    /**
     * 根据加课码查询课程
     */
    @Select("select c.id as cid, c.course_name as courseName, c.credit as credits, c.hours, " +
            "c.teacher_id as teacherId, u.username as teacherName, c.status, c.description, c.course_code as courseCode " +
            "from course c left join user u on c.teacher_id = u.uid " +
            "where c.course_code = #{courseCode} and c.status = 1")
    Course findByCourseCode(@Param("courseCode") String courseCode);

    /**
     * 检查学生是否已选某课程
     */
    @Select("select count(*) from student_course " +
            "where student_id = #{studentId} and course_id = #{courseId} and status = 1")
    int countStudentCourse(@Param("studentId") Integer studentId, @Param("courseId") Integer courseId);

    /**
     * 插入选课记录
     */
    @Insert("insert into student_course(student_id, course_id, status) " +
            "values(#{studentId}, #{courseId}, 1)")
    int insertStudentCourse(@Param("studentId") Integer studentId, @Param("courseId") Integer courseId);

    /**
     * 删除选课记录（退课）
     */
    @Delete("delete from student_course " +
            "where student_id = #{studentId} and course_id = #{courseId}")
    int deleteStudentCourse(@Param("studentId") Integer studentId, @Param("courseId") Integer courseId);

    /**
     * 统计教师的课程数量
     */
    @Select("select count(*) from course where teacher_id = #{teacherId} and status = 1")
    int countCoursesByTeacherId(@Param("teacherId") Integer teacherId);

    /**
     * 统计教师所有课程的学生总数
     */
    @Select("select count(*) from student_course sc " +
            "join course c on sc.course_id = c.id " +
            "where c.teacher_id = #{teacherId} and sc.status = 1")
    int countStudentsByTeacherId(@Param("teacherId") Integer teacherId);

    /**
     * 统计学生已选课程数量
     */
    @Select("select count(*) from student_course where student_id = #{studentId} and status = 1")
    int countCoursesByStudentId(@Param("studentId") Integer studentId);

    /**
     * 根据状态统计课程数量
     */
    @Select("select count(*) from course where status = #{status}")
    int countByStatus(@Param("status") Integer status);

    /**
     * 获取最近课程数据（包含选课人数）
     */
    @Select("select c.id as cid, c.course_name as courseName, c.credit as credits, " +
            "c.hours, u.username as teacherName, c.status, " +
            "(select count(*) from student_course sc where sc.course_id = c.id and sc.status = 1) as studentCount " +
            "from course c left join user u on c.teacher_id = u.uid " +
            "order by c.id desc limit 5")
    List<Map<String, Object>> getRecentCoursesWithStudentCount();
}