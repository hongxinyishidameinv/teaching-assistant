package com.xujc.mvcpro.controller;

import com.xujc.mvcpro.common.ApiResponse;
import com.xujc.mvcpro.mapper.CourseMapper;
import com.xujc.mvcpro.pojo.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生端课程管理控制器
 */
@RestController
@RequestMapping("/api/student")
public class StudentCourseController {

    @Autowired
    private CourseMapper courseMapper;

    /**
     * 获取学生的选课列表
     * GET /api/student/courses?studentId=xxx
     * 
     * 根据学生ID查询该学生所选的全部课程，包括课程详情、授课教师、课时信息等
     */
    @GetMapping("/courses")
    public ApiResponse getStudentCourses(@RequestParam(required = false) Integer studentId) {
        try {
            System.out.println("获取学生选课列表 - studentId: " + studentId);
            
            // 默认使用学生ID=9（陈昊）进行测试
            if (studentId == null) {
                studentId = 9;
            }
            
            List<Course> courses = courseMapper.findCoursesByStudentId(studentId);
            System.out.println("查询结果 - courses count: " + (courses != null ? courses.size() : 0));
            
            return ApiResponse.ok("获取成功", courses);
        } catch (Exception e) {
            System.err.println("获取选课列表失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "获取选课列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有可选课程列表（供学生选课使用）
     * GET /api/student/available-courses?studentId=xxx
     * 
     * 返回所有课程，并标记该学生是否已选
     */
    @GetMapping("/available-courses")
    public ApiResponse getAvailableCourses(@RequestParam(required = false) Integer studentId) {
        try {
            System.out.println("获取可选课程列表 - studentId: " + studentId);
            
            // 默认使用学生ID=9（陈昊）进行测试
            if (studentId == null) {
                studentId = 9;
            }
            
            // 获取所有课程
            List<Course> allCourses = courseMapper.findAllCourses();
            System.out.println("所有课程数: " + (allCourses != null ? allCourses.size() : 0));
            
            // 获取学生已选课程
            List<Course> selectedCourses = courseMapper.findCoursesByStudentId(studentId);
            
            // 创建已选课程ID集合
            Map<Integer, Boolean> selectedMap = new HashMap<>();
            if (selectedCourses != null) {
                for (Course course : selectedCourses) {
                    selectedMap.put(course.getCid(), true);
                }
            }
            
            // 为每个课程添加已选标记
            for (Course course : allCourses) {
                course.setSelected(selectedMap.containsKey(course.getCid()));
            }
            
            return ApiResponse.ok("获取成功", allCourses);
        } catch (Exception e) {
            System.err.println("获取可选课程列表失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "获取可选课程列表失败: " + e.getMessage());
        }
    }

    /**
     * 学生选课
     * POST /api/student/select-course
     * 
     * 学生选择一门课程，将记录插入student_course表
     */
    @PostMapping("/select-course")
    public ApiResponse selectCourse(@RequestBody Map<String, Integer> params) {
        try {
            Integer studentId = params.get("studentId");
            Integer courseId = params.get("courseId");
            
            System.out.println("学生选课 - studentId: " + studentId + ", courseId: " + courseId);
            
            // 默认使用学生ID=9（陈昊）进行测试
            if (studentId == null) {
                studentId = 9;
            }
            
            if (courseId == null) {
                return ApiResponse.error(null, "课程ID不能为空");
            }
            
            // 检查是否已选该课程
            int count = courseMapper.countStudentCourse(studentId, courseId);
            if (count > 0) {
                return ApiResponse.error(null, "您已在该课程班");
            }
            
            // 插入选课记录
            int result = courseMapper.insertStudentCourse(studentId, courseId);
            if (result > 0) {
                return ApiResponse.ok("选课成功", null);
            } else {
                return ApiResponse.error(null, "选课失败");
            }
        } catch (Exception e) {
            System.err.println("选课失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "选课失败: " + e.getMessage());
        }
    }

    /**
     * 学生退课
     * POST /api/student/drop-course
     * 
     * 学生退出一门课程，删除student_course表中的记录
     */
    @PostMapping("/drop-course")
    public ApiResponse dropCourse(@RequestBody Map<String, Integer> params) {
        try {
            Integer studentId = params.get("studentId");
            Integer courseId = params.get("courseId");
            
            System.out.println("学生退课 - studentId: " + studentId + ", courseId: " + courseId);
            
            // 默认使用学生ID=9（陈昊）进行测试
            if (studentId == null) {
                studentId = 9;
            }
            
            if (courseId == null) {
                return ApiResponse.error(null, "课程ID不能为空");
            }
            
            // 检查是否已选该课程
            int count = courseMapper.countStudentCourse(studentId, courseId);
            if (count == 0) {
                return ApiResponse.error(null, "您未选该课程");
            }
            
            // 删除选课记录
            int result = courseMapper.deleteStudentCourse(studentId, courseId);
            if (result > 0) {
                return ApiResponse.ok("退课成功", null);
            } else {
                return ApiResponse.error(null, "退课失败");
            }
        } catch (Exception e) {
            System.err.println("退课失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "退课失败: " + e.getMessage());
        }
    }
}
