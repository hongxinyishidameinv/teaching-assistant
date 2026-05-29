package com.xujc.mvcpro.controller;

import com.xujc.mvcpro.common.ApiResponse;
import com.xujc.mvcpro.mapper.AttendanceMapper;
import com.xujc.mvcpro.mapper.CourseMapper;
import com.xujc.mvcpro.pojo.Attendance;
import com.xujc.mvcpro.pojo.Course;
import com.xujc.mvcpro.pojo.StudentCourse;
import com.xujc.mvcpro.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teacher/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private AttendanceMapper attendanceMapper;

    // 获取教师的课程列表
    @GetMapping("/courses/{teacherId}")
    public ApiResponse getTeacherCourses(@PathVariable Integer teacherId) {
        try {
            List<Course> courses = courseMapper.findCoursesByTeacherId(teacherId);
            List<Map<String, Object>> result = courses.stream().map(c -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", c.getCid());
                map.put("course_name", c.getCourseName());
                return map;
            }).collect(Collectors.toList());
            return ApiResponse.ok("获取成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "获取课程失败: " + e.getMessage());
        }
    }

    // 获取课程学生列表
    @GetMapping("/students/{courseId}")
    public ApiResponse getCourseStudents(@PathVariable Long courseId) {
        try {
            List<StudentCourse> students = courseMapper.findStudentsByCourseId(courseId.intValue(), 0, Integer.MAX_VALUE);
            List<Map<String, Object>> result = students.stream().map(s -> {
                Map<String, Object> map = new HashMap<>();
                map.put("uid", s.getUid());
                map.put("username", s.getUsername());
                return map;
            }).collect(Collectors.toList());
            return ApiResponse.ok("获取成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "获取学生列表失败: " + e.getMessage());
        }
    }

    // 获取指定日期的考勤记录
    @GetMapping("/today/{courseId}")
    public ApiResponse getTodayAttendance(@PathVariable Long courseId, @RequestParam(required = false) String date) {
        try {
            LocalDate attendanceDate = date != null ? LocalDate.parse(date) : LocalDate.now();
            List<Attendance> list = attendanceService.getTodayAttendance(courseId, attendanceDate);
            
            // 获取课程学生列表
            List<StudentCourse> studentList = courseMapper.findStudentsByCourseId(courseId.intValue(), 0, Integer.MAX_VALUE);
            List<Map<String, Object>> students = studentList.stream().map(s -> {
                Map<String, Object> map = new HashMap<>();
                map.put("uid", s.getUid());
                map.put("username", s.getUsername());
                return map;
            }).collect(Collectors.toList());
            
            // 合并考勤状态
            Map<Integer, Attendance> attendanceMap = new HashMap<>();
            for (Attendance a : list) {
                attendanceMap.put(a.getStudentId(), a);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("students", students);
            result.put("attendances", attendanceMap);
            
            return ApiResponse.ok("获取成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "获取考勤记录失败: " + e.getMessage());
        }
    }

    // 保存考勤记录
    @PostMapping("/save")
    public ApiResponse saveAttendance(@RequestBody Map<String, Object> params) {
        try {
            // 处理 courseId，可能是字符串或数字
            Object courseIdObj = params.get("courseId");
            Long courseId;
            if (courseIdObj instanceof Number) {
                courseId = ((Number) courseIdObj).longValue();
            } else if (courseIdObj instanceof String) {
                courseId = Long.parseLong((String) courseIdObj);
            } else {
                return ApiResponse.error(400, "courseId 参数错误");
            }
            
            String dateStr = (String) params.get("date");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> attendanceList = (List<Map<String, Object>>) params.get("attendanceList");
            
            LocalDate date = dateStr != null ? LocalDate.parse(dateStr) : LocalDate.now();
            
            attendanceService.saveAttendance(courseId, date, attendanceList);
            return ApiResponse.ok("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "保存考勤失败: " + e.getMessage());
        }
    }

    // 查询所有考勤记录（用于测试）
    @GetMapping("/all")
    public ApiResponse getAllAttendance() {
        try {
            List<Attendance> list = attendanceMapper.selectAll();
            return ApiResponse.ok("获取成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "查询失败: " + e.getMessage());
        }
    }
    
    // 查询考勤记录（分页）
    @GetMapping("/list")
    public ApiResponse getAttendanceList(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
            
            List<Attendance> list = attendanceService.getAttendanceList(courseId, studentId, studentName, start, end, status, page, size);
            int count = attendanceService.getAttendanceCount(courseId, studentId, studentName, start, end, status);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", list);
            result.put("total", count);
            result.put("page", page);
            result.put("size", size);
            
            return ApiResponse.ok("获取成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "查询考勤记录失败: " + e.getMessage());
        }
    }
}
