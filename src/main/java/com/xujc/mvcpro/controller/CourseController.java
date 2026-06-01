package com.xujc.mvcpro.controller;

import com.xujc.mvcpro.common.ApiResponse;
import com.xujc.mvcpro.common.PageResult;
import com.xujc.mvcpro.pojo.Course;
import com.xujc.mvcpro.pojo.User;
import com.xujc.mvcpro.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/page")
    public ApiResponse getCoursesByPage(@RequestParam(defaultValue = "1") int pageNum,
                                        @RequestParam(defaultValue = "15") int pageSize) {
        PageResult<Course> pageResult = courseService.getCoursesByPage(pageNum, pageSize);
        return ApiResponse.ok("获取分页数据成功", pageResult);
    }

    @GetMapping("/search/page")
    public ApiResponse searchCoursesByPage(@RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) Integer status,
                                           @RequestParam(defaultValue = "1") int pageNum,
                                           @RequestParam(defaultValue = "15") int pageSize) {
        if (keyword == null) {
            keyword = "";
        }
        PageResult<Course> pageResult = courseService.searchCoursesByPage(keyword, status, pageNum, pageSize);
        return ApiResponse.ok("搜索成功", pageResult);
    }

    @GetMapping("/{cid}")
    public ApiResponse getCourseById(@PathVariable Integer cid) {
        Course course = courseService.getCourseById(cid);
        if (course == null) {
            return ApiResponse.error(404, "课程不存在");
        }
        return ApiResponse.ok("查询成功", course);
    }

    @PostMapping
    public ApiResponse addCourse(@RequestBody Course course) {
        course.setCourseCode(courseService.generateUniqueCourseCode());
        boolean success = courseService.addCourse(course);
        if (success) {
            return ApiResponse.ok("添加成功", null);
        }
        return ApiResponse.error(500, "添加失败");
    }

    @GetMapping("/code/{courseCode}")
    public ApiResponse getCourseByCode(@PathVariable String courseCode) {
        Course course = courseService.getCourseByCode(courseCode);
        if (course == null) {
            return ApiResponse.error(404, "加课码无效");
        }
        return ApiResponse.ok("查询成功", course);
    }

    @PutMapping
    public ApiResponse updateCourse(@RequestBody Course course) {
        boolean success = courseService.updateCourse(course);
        if (success) {
            return ApiResponse.ok("更新成功", null);
        }
        return ApiResponse.error(500, "更新失败");
    }

    @DeleteMapping("/{cid}")
    public ApiResponse deleteCourse(@PathVariable Integer cid) {
        boolean success = courseService.deleteCourse(cid);
        if (success) {
            return ApiResponse.ok("删除成功", null);
        }
        return ApiResponse.error(500, "删除失败");
    }

    @DeleteMapping("/batch")
    public ApiResponse batchDeleteCourses(@RequestBody Map<String, List<Integer>> request) {
        List<Integer> cids = request.get("cids");
        boolean success = courseService.batchDeleteCourses(cids);
        if (success) {
            return ApiResponse.ok("批量删除成功", null);
        }
        return ApiResponse.error(500, "批量删除失败");
    }

    @PutMapping("/status")
    public ApiResponse updateCourseStatus(@RequestBody Map<String, Integer> request) {
        Integer cid = request.get("cid");
        Integer status = request.get("status");
        boolean success = courseService.updateCourseStatus(cid, status);
        if (success) {
            String message = status == 1 ? "启用成功" : "禁用成功";
            return ApiResponse.ok(message, null);
        }
        return ApiResponse.error(500, "操作失败");
    }

    @GetMapping("/teachers")
    public ApiResponse getAllTeachers() {
        List<User> teachers = courseService.getAllTeachers();
        return ApiResponse.ok("查询成功", teachers);
    }
}