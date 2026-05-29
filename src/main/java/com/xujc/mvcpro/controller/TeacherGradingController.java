package com.xujc.mvcpro.controller;

import com.xujc.mvcpro.common.ApiResponse;
import com.xujc.mvcpro.mapper.StudentTestAnswerMapper;
import com.xujc.mvcpro.mapper.TestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
public class TeacherGradingController {

    @Autowired
    private StudentTestAnswerMapper answerMapper;

    @Autowired
    private TestMapper testMapper;

    @GetMapping("/test-results/{testId}")
    public ApiResponse getTestResults(@PathVariable Long testId) {
        try {
            List<Map<String, Object>> results = answerMapper.selectTestResults(testId);
            
            // 获取测试信息以获取课程ID
            var test = testMapper.selectById(testId);
            int totalStudents = 0;
            int submittedCount = 0;
            int ungradedCount = 0;
            
            if (test != null && test.getCourseId() != null) {
                // 获取课程学生总数
                totalStudents = testMapper.countCourseStudents(test.getCourseId());
                // 获取已提交人数
                submittedCount = answerMapper.countSubmittedStudents(testId);
                // 获取未批改人数（存在status=0的记录的学生）
                ungradedCount = answerMapper.countUngradedStudents(testId);
            }
            
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("students", results);
            result.put("totalStudents", totalStudents);
            result.put("submittedCount", submittedCount);
            result.put("ungradedCount", ungradedCount);
            
            return ApiResponse.ok("获取成功", result);
        } catch (Exception e) {
            System.err.println("获取测试结果失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "获取测试结果失败: " + e.getMessage());
        }
    }

    @GetMapping("/test-answers/{testId}/{studentId}")
    public ApiResponse getStudentAnswers(@PathVariable Long testId, @PathVariable Integer studentId) {
        try {
            List<Map<String, Object>> answers = new java.util.ArrayList<>();
            var studentAnswers = answerMapper.selectByTestIdAndStudentId(testId, studentId);
            
            for (var sa : studentAnswers) {
                Map<String, Object> answerInfo = new java.util.HashMap<>();
                answerInfo.put("id", sa.getId());
                answerInfo.put("questionId", sa.getQuestionId());
                answerInfo.put("answer", sa.getAnswer());
                answerInfo.put("score", sa.getScore());
                answerInfo.put("status", sa.getStatus());
                
                var question = testMapper.findQuestionById(sa.getQuestionId());
                if (question != null) {
                    answerInfo.put("content", question.get("content"));
                    answerInfo.put("type", question.get("type"));
                    answerInfo.put("options", question.get("options"));
                    answerInfo.put("correctAnswer", question.get("answer"));
                    answerInfo.put("maxScore", question.get("score"));
                }
                answers.add(answerInfo);
            }
            
            return ApiResponse.ok("获取成功", answers);
        } catch (Exception e) {
            System.err.println("获取学生答案失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "获取学生答案失败: " + e.getMessage());
        }
    }

    @Transactional
    @PostMapping("/grade-answer/{answerId}")
    public ApiResponse gradeAnswer(@PathVariable Long answerId, @RequestBody Map<String, Object> params) {
        try {
            Integer score = (Integer) params.get("score");
            if (score == null) {
                return ApiResponse.error(null, "分数不能为空");
            }

            answerMapper.updateScore(answerId, score);
            return ApiResponse.ok("批改成功");
        } catch (Exception e) {
            System.err.println("批改失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "批改失败: " + e.getMessage());
        }
    }

    @GetMapping("/test-submit-count/{testId}")
    public ApiResponse getSubmitCount(@PathVariable Long testId) {
        try {
            int count = answerMapper.countSubmittedStudents(testId);
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("count", count);
            return ApiResponse.ok("获取成功", result);
        } catch (Exception e) {
            System.err.println("获取提交人数失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "获取提交人数失败: " + e.getMessage());
        }
    }
}
