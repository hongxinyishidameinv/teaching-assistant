package com.xujc.mvcpro.controller;

import com.xujc.mvcpro.common.ApiResponse;
import com.xujc.mvcpro.mapper.CourseMapper;
import com.xujc.mvcpro.pojo.Course;
import com.xujc.mvcpro.pojo.StudentCourse;
import com.xujc.mvcpro.pojo.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师端课程管理控制器
 */
@RestController
@RequestMapping("/api/teacher")
public class TeacherCourseController {

    @Autowired
    private CourseMapper courseMapper;

    /**
     * 测试接口
     */
    @GetMapping("/test")
    public String test() {
        return "{\"code\":200,\"message\":\"测试成功\",\"data\":\"Hello World\"}";
    }
    
    /**
     * 测试数据库连接
     */
    @GetMapping("/test-db")
    public ApiResponse testDb() {
        try {
            System.out.println("测试数据库连接...");
            // 查询课程表数量
            int courseCount = courseMapper.countCourses();
            System.out.println("课程表记录数: " + courseCount);
            
            // 查询学生选课表数量（测试表是否存在）
            int studentCount = courseMapper.countStudentCourses(1);
            System.out.println("课程1的学生数: " + studentCount);
            
            return ApiResponse.ok("数据库连接成功", courseCount);
        } catch (Exception e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "数据库连接失败: " + e.getMessage());
        }
    }

    /**
     * 获取教师的课程列表
     * GET /api/teacher/courses?teacherId=xxx
     * 
     * 优先从请求参数获取teacherId，如果没有则从Session获取当前登录用户
     */
    @GetMapping("/courses")
    public ApiResponse getTeacherCourses(
            @RequestParam(required = false) Integer teacherId,
            HttpSession session) {
        
        // 优先使用请求参数中的teacherId
        if (teacherId == null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                teacherId = user.getUid();
            } else {
                // 默认使用教师ID=2（苏泽荫）进行测试
                teacherId = 2;
            }
        }
        
        List<Course> courses = courseMapper.findCoursesByTeacherId(teacherId);
        return ApiResponse.ok("获取成功", courses);
    }

    /**
     * 获取课程的学生列表（分页）
     * GET /api/teacher/courses/{courseId}/students?page=1&size=10
     */
    @GetMapping("/courses/{courseId}/students")
    public ApiResponse getCourseStudents(
            @PathVariable Integer courseId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            System.out.println("获取课程学生列表 - courseId: " + courseId + ", page: " + page + ", size: " + size);
            
            int offset = (page - 1) * size;
            List<StudentCourse> students = courseMapper.findStudentsByCourseId(courseId, offset, size);
            int total = courseMapper.countStudentsByCourseId(courseId);
            
            System.out.println("查询结果 - students count: " + (students != null ? students.size() : 0) + ", total: " + total);
            
            Map<String, Object> result = new HashMap<>();
            result.put("students", students);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            result.put("totalPages", (int) Math.ceil((double) total / size));
            
            return ApiResponse.ok("获取成功", result);
        } catch (Exception e) {
            System.err.println("获取学生列表失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "获取学生列表失败: " + e.getMessage());
        }
    }
}
