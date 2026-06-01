package com.xujc.mvcpro.controller;

import com.xujc.mvcpro.common.ApiResponse;
import com.xujc.mvcpro.mapper.CourseMapper;
import com.xujc.mvcpro.pojo.Course;
import com.xujc.mvcpro.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student/attendance")
public class StudentAttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private CourseMapper courseMapper;

    @GetMapping("/courses/{studentId}")
    public ApiResponse getStudentCourses(@PathVariable Integer studentId) {
        try {
            List<Course> courses = courseMapper.findCoursesByStudentId(studentId);
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

    @GetMapping("/list")
    public ApiResponse getStudentAttendanceList(
            @RequestParam Integer studentId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;

            List<com.xujc.mvcpro.pojo.Attendance> list = attendanceService.getStudentAttendanceList(studentId, courseId, start, end, status, page, size);
            int count = attendanceService.getStudentAttendanceCount(studentId, courseId, start, end, status);
            Map<String, Integer> stats = attendanceService.getStudentAttendanceStats(studentId);

            Map<String, Object> result = new HashMap<>();
            result.put("list", list);
            result.put("total", count);
            result.put("page", page);
            result.put("size", size);
            result.put("stats", stats);

            return ApiResponse.ok("获取成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error(500, "查询考勤记录失败: " + e.getMessage());
        }
    }
}