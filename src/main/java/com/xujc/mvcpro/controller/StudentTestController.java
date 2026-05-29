package com.xujc.mvcpro.controller;

import com.xujc.mvcpro.common.ApiResponse;
import com.xujc.mvcpro.mapper.StudentTestAnswerMapper;
import com.xujc.mvcpro.mapper.TestMapper;
import com.xujc.mvcpro.pojo.StudentTestAnswer;
import com.xujc.mvcpro.pojo.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentTestController {

    @Autowired
    private TestMapper testMapper;

    @Autowired
    private StudentTestAnswerMapper answerMapper;

    @GetMapping("/tests")
    public ApiResponse getStudentTests(@RequestParam(required = false) Integer studentId) {
        try {
            System.out.println("===== 获取学生测试列表 =====");
            System.out.println("请求参数 studentId: " + studentId);

            if (studentId == null) {
                studentId = 21;
                System.out.println("使用默认学生ID: " + studentId);
            }

            System.out.println("开始查询数据库...");
            
            List<Test> allTests = testMapper.selectPage(null, 0, 100);
            System.out.println("所有测试数量: " + (allTests != null ? allTests.size() : 0));
            
            // 获取学生的答题记录
            Map<Long, Map<String, Object>> studentAnswers = new HashMap<>();
            List<StudentTestAnswer> answers = answerMapper.selectByStudentId(studentId);
            for (StudentTestAnswer answer : answers) {
                Long testId = answer.getTestId();
                if (!studentAnswers.containsKey(testId)) {
                    Map<String, Object> testResult = new HashMap<>();
                    testResult.put("submitted", true);
                    testResult.put("score", 0);
                    testResult.put("allGraded", true);
                    studentAnswers.put(testId, testResult);
                }
                Map<String, Object> testResult = studentAnswers.get(testId);
                if (answer.getScore() != null) {
                    int currentScore = (Integer) testResult.get("score");
                    testResult.put("score", currentScore + answer.getScore());
                }
                if (answer.getStatus() == 0) {
                    testResult.put("allGraded", false);
                }
            }

            // 将答题记录添加到测试列表中
            if (allTests != null) {
                for (Test test : allTests) {
                    Map<String, Object> result = studentAnswers.get(test.getId());
                    if (result != null) {
                        test.setSubmitted((Boolean) result.get("submitted"));
                        test.setStudentScore((Integer) result.get("score"));
                        test.setAllGraded((Boolean) result.get("allGraded"));
                    } else {
                        test.setSubmitted(false);
                        test.setStudentScore(0);
                        test.setAllGraded(false);
                    }
                    System.out.println("测试: " + test.getId() + " - " + test.getTitle() + " - 课程ID: " + test.getCourseId() + " - 已提交: " + test.getSubmitted() + " - 得分: " + test.getStudentScore());
                }
            }

            System.out.println("返回所有测试数据");
            return ApiResponse.ok("获取成功", allTests);
        } catch (Exception e) {
            System.err.println("===== 获取测试列表失败 =====");
            System.err.println("错误信息: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "获取测试列表失败: " + e.getMessage());
        }
    }

    @Transactional
    @PostMapping("/submit-test/{testId}")
    public ApiResponse submitTest(@PathVariable Integer testId, @RequestBody Map<String, Object> params) {
        try {
            Integer studentId = (Integer) params.get("studentId");
            List<Map<String, Object>> answers = (List<Map<String, Object>>) params.get("answers");

            System.out.println("学生提交测试 - testId: " + testId + ", studentId: " + studentId);
            System.out.println("答案数量: " + (answers != null ? answers.size() : 0));

            if (studentId == null) {
                studentId = 21;
            }

            if (testId == null) {
                return ApiResponse.error(null, "测试ID不能为空");
            }

            Test test = testMapper.selectById(Long.valueOf(testId));
            if (test == null) {
                return ApiResponse.error(null, "测试不存在");
            }

            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            java.time.LocalDateTime startTime = test.getStartTime();
            java.time.LocalDateTime endTime = test.getEndTime();

            if (startTime != null && now.isBefore(startTime)) {
                return ApiResponse.error(null, "测试尚未开始");
            }

            if (endTime != null && now.isAfter(endTime)) {
                return ApiResponse.error(null, "测试已结束");
            }

            List<StudentTestAnswer> existingAnswers = answerMapper.selectByTestIdAndStudentId(Long.valueOf(testId), studentId);
            if (existingAnswers != null && !existingAnswers.isEmpty()) {
                return ApiResponse.error(null, "您已提交过该测试");
            }

            int autoScore = 0;
            List<StudentTestAnswer> answerList = new java.util.ArrayList<>();

            if (answers != null) {
                for (Map<String, Object> answer : answers) {
                    Integer questionId = (Integer) answer.get("questionId");
                    String userAnswer = (String) answer.get("answer");

                    Map<String, Object> question = testMapper.findQuestionById(questionId);
                    if (question != null) {
                        String correctAnswer = (String) question.get("answer");
                        Integer type = (Integer) question.get("type");
                        Object scoreObj = question.get("score");
                        int score = 0;
                        if (scoreObj instanceof java.math.BigDecimal) {
                            score = ((java.math.BigDecimal) scoreObj).intValue();
                        } else if (scoreObj instanceof Integer) {
                            score = (Integer) scoreObj;
                        }

                        StudentTestAnswer sta = new StudentTestAnswer();
                        sta.setStudentId(studentId);
                        sta.setTestId(Long.valueOf(testId));
                        sta.setQuestionId(questionId);
                        sta.setAnswer(userAnswer);

                        if (type == 4) {
                            sta.setScore(0);
                            sta.setStatus(0);
                        } else {
                            if (correctAnswer != null && correctAnswer.equals(userAnswer)) {
                                sta.setScore(score);
                                autoScore += score;
                            } else {
                                sta.setScore(0);
                            }
                            sta.setStatus(1);
                        }
                        answerList.add(sta);
                    }
                }
            }

            if (!answerList.isEmpty()) {
                answerMapper.batchInsert(answerList);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("testId", testId);
            result.put("studentId", studentId);
            result.put("autoScore", autoScore);
            result.put("submitTime", java.time.LocalDateTime.now());

            System.out.println("测试得分: " + autoScore);

            return ApiResponse.ok("提交成功", result);
        } catch (Exception e) {
            System.err.println("提交测试失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "提交测试失败: " + e.getMessage());
        }
    }

    @GetMapping("/test-result/{testId}")
    public ApiResponse getTestResult(@PathVariable Integer testId, @RequestParam(required = false) Integer studentId) {
        try {
            if (studentId == null) {
                studentId = 21;
            }

            List<StudentTestAnswer> answers = answerMapper.selectByTestIdAndStudentId(Long.valueOf(testId), studentId);
            
            int totalScore = 0;
            boolean allGraded = true;
            for (StudentTestAnswer a : answers) {
                if (a.getScore() != null) {
                    totalScore += a.getScore();
                }
                if (a.getStatus() == 0) {
                    allGraded = false;
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("answers", answers);
            result.put("totalScore", totalScore);
            result.put("allGraded", allGraded);

            return ApiResponse.ok("获取成功", result);
        } catch (Exception e) {
            System.err.println("获取测试结果失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "获取测试结果失败: " + e.getMessage());
        }
    }
}
