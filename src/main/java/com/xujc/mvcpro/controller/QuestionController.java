package com.xujc.mvcpro.controller;

import com.xujc.mvcpro.pojo.Question;
import com.xujc.mvcpro.service.QuestionService;
import com.xujc.mvcpro.common.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/question")
public class QuestionController {
    
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);
    
    @Autowired
    private QuestionService questionService;
    
    @GetMapping("/page")
    public ApiResponse getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String knowledgePoint
    ) {
        try {
            Map<String, Object> page = questionService.getPage(courseId, knowledgePoint, pageNum, pageSize);
            return ApiResponse.ok(page);
        } catch (Exception e) {
            logger.error("获取题目列表失败", e);
            return ApiResponse.error(500, "获取题目列表失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Long id) {
        try {
            Question question = questionService.getById(id);
            if (question != null) {
                return ApiResponse.ok(question);
            }
            return ApiResponse.error(500, "题目不存在");
        } catch (Exception e) {
            logger.error("获取题目失败", e);
            return ApiResponse.error(500, "获取题目失败：" + e.getMessage());
        }
    }
    
    @PostMapping
    public ApiResponse add(@RequestBody Question question) {
        try {
            logger.info("接收到添加题目请求: {}", question);
            if (question.getCourseId() == null) {
                return ApiResponse.error(400, "课程ID不能为空");
            }
            int result = questionService.add(question);
            if (result > 0) {
                logger.info("题目添加成功");
                return ApiResponse.ok("添加成功");
            }
            return ApiResponse.error(500, "添加失败");
        } catch (Exception e) {
            logger.error("添加题目失败", e);
            return ApiResponse.error(500, "添加失败：" + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable Long id, @RequestBody Question question) {
        try {
            question.setId(id);
            int result = questionService.update(question);
            if (result > 0) {
                return ApiResponse.ok("更新成功");
            }
            return ApiResponse.error(500, "更新失败");
        } catch (Exception e) {
            logger.error("更新题目失败", e);
            return ApiResponse.error(500, "更新失败：" + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable Long id) {
        try {
            int result = questionService.delete(id);
            if (result > 0) {
                return ApiResponse.ok("删除成功");
            }
            return ApiResponse.error(500, "删除失败");
        } catch (Exception e) {
            logger.error("删除题目失败", e);
            return ApiResponse.error(500, "删除失败：" + e.getMessage());
        }
    }
    
    @DeleteMapping("/batch")
    public ApiResponse batchDelete(@RequestBody List<Long> ids) {
        try {
            int result = questionService.batchDelete(ids);
            if (result > 0) {
                return ApiResponse.ok("批量删除成功");
            }
            return ApiResponse.error(500, "批量删除失败");
        } catch (Exception e) {
            logger.error("批量删除题目失败", e);
            return ApiResponse.error(500, "批量删除失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/course/{courseId}")
    public ApiResponse getByCourseId(@PathVariable Long courseId) {
        try {
            return ApiResponse.ok(questionService.getByCourseId(courseId));
        } catch (Exception e) {
            logger.error("获取课程题目失败", e);
            return ApiResponse.error(500, "获取课程题目失败：" + e.getMessage());
        }
    }
}
