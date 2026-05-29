package com.xujc.mvcpro.controller;

import com.xujc.mvcpro.pojo.Test;
import com.xujc.mvcpro.pojo.TestQuestion;
import com.xujc.mvcpro.service.TestService;
import com.xujc.mvcpro.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @Autowired
    private TestService testService;
    
    private java.time.LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        try {
            // 尝试解析带秒的格式
            return java.time.LocalDateTime.parse(dateTimeStr);
        } catch (Exception e) {
            try {
                // 尝试解析不带秒的格式
                return java.time.LocalDateTime.parse(dateTimeStr + ":00");
            } catch (Exception e2) {
                try {
                    // 尝试用DateTimeFormatter解析
                    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                    return java.time.LocalDateTime.parse(dateTimeStr, formatter);
                } catch (Exception e3) {
                    return null;
                }
            }
        }
    }
    
    @GetMapping("/page")
    public ApiResponse getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long courseId
    ) {
        Map<String, Object> page = testService.getPage(courseId, pageNum, pageSize);
        return ApiResponse.ok(page);
    }
    
    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Long id) {
        Test test = testService.getById(id);
        if (test != null) {
            return ApiResponse.ok(test);
        }
        return ApiResponse.error(500, "测试不存在");
    }
    
    @GetMapping("/{id}/questions")
    public ApiResponse getQuestions(@PathVariable Long id) {
        return ApiResponse.ok(testService.getQuestionsByTestId(id));
    }
    
    @PostMapping
    public ApiResponse add(
            @RequestBody Map<String, Object> params
    ) {
        Test test = new Test();
        test.setTitle((String) params.get("title"));
        test.setCourseId(((Number) params.get("courseId")).longValue());
        test.setDuration((Integer) params.get("duration"));
        
        if (params.get("startTime") != null && !params.get("startTime").equals("")) {
            test.setStartTime(parseDateTime((String) params.get("startTime")));
        }
        if (params.get("endTime") != null && !params.get("endTime").equals("")) {
            test.setEndTime(parseDateTime((String) params.get("endTime")));
        }
        
        Integer status = (Integer) params.get("status");
        test.setStatus(status != null ? status : 1);
        
        List<TestQuestion> testQuestions = null;
        if (params.get("questions") != null) {
            List<Map<String, Object>> questionList = (List<Map<String, Object>>) params.get("questions");
            testQuestions = questionList.stream().map(q -> {
                TestQuestion tq = new TestQuestion();
                tq.setQuestionId(((Number) q.get("questionId")).longValue());
                tq.setScore(java.math.BigDecimal.valueOf(((Number) q.get("score")).doubleValue()));
                return tq;
            }).toList();
        }
        
        int result = testService.add(test, testQuestions);
        if (result > 0) {
            return ApiResponse.ok("创建成功");
        }
        return ApiResponse.error(500, "创建失败");
    }
    
    @PutMapping("/{id}")
    public ApiResponse update(
            @PathVariable Long id,
            @RequestBody Map<String, Object> params
    ) {
        Test test = new Test();
        test.setId(id);
        test.setTitle((String) params.get("title"));
        test.setCourseId(((Number) params.get("courseId")).longValue());
        test.setDuration((Integer) params.get("duration"));
        
        if (params.get("startTime") != null && !params.get("startTime").equals("")) {
            test.setStartTime(java.time.LocalDateTime.parse((String) params.get("startTime")));
        }
        if (params.get("endTime") != null && !params.get("endTime").equals("")) {
            test.setEndTime(java.time.LocalDateTime.parse((String) params.get("endTime")));
        }
        
        Integer status = (Integer) params.get("status");
        test.setStatus(status != null ? status : 1);
        
        List<TestQuestion> testQuestions = null;
        if (params.get("questions") != null) {
            List<Map<String, Object>> questionList = (List<Map<String, Object>>) params.get("questions");
            testQuestions = questionList.stream().map(q -> {
                TestQuestion tq = new TestQuestion();
                tq.setQuestionId(((Number) q.get("questionId")).longValue());
                tq.setScore(java.math.BigDecimal.valueOf(((Number) q.get("score")).doubleValue()));
                return tq;
            }).toList();
        }
        
        int result = testService.update(test, testQuestions);
        if (result > 0) {
            return ApiResponse.ok("更新成功");
        }
        return ApiResponse.error(500, "更新失败");
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable Long id) {
        int result = testService.delete(id);
        if (result > 0) {
            return ApiResponse.ok("删除成功");
        }
        return ApiResponse.error(500, "删除失败");
    }
}
