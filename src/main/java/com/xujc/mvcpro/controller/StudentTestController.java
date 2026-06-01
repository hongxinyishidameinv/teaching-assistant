package com.xujc.mvcpro.controller;

import com.xujc.mvcpro.common.ApiResponse;
import com.xujc.mvcpro.mapper.StudentTestAnswerMapper;
import com.xujc.mvcpro.mapper.TestMapper;
import com.xujc.mvcpro.pojo.StudentTestAnswer;
import com.xujc.mvcpro.pojo.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    /**
     * 获取学生的错题本
     * GET /api/student/wrong-questions?studentId=xxx
     */
    @GetMapping("/wrong-questions")
    public ApiResponse getWrongQuestions(@RequestParam Integer studentId) {
        try {
            System.out.println("===== 获取学生错题本 =====");
            System.out.println("学生ID: " + studentId);

            // 获取学生的所有答题记录
            List<StudentTestAnswer> allAnswers = answerMapper.selectByStudentId(studentId);
            
            if (allAnswers == null || allAnswers.isEmpty()) {
                return ApiResponse.ok("暂无错题", new java.util.ArrayList<>());
            }

            // 按测试分组，获取错题
            Map<Long, List<StudentTestAnswer>> answersByTest = new HashMap<>();
            for (StudentTestAnswer answer : allAnswers) {
                Long testId = answer.getTestId();
                if (!answersByTest.containsKey(testId)) {
                    answersByTest.put(testId, new java.util.ArrayList<>());
                }
                answersByTest.get(testId).add(answer);
            }

            List<Map<String, Object>> wrongQuestionsList = new java.util.ArrayList<>();
            
            // 用于去重，key是questionId
            Map<Integer, Map<String, Object>> uniqueQuestions = new HashMap<>();

            // 遍历每个测试
            for (Map.Entry<Long, List<StudentTestAnswer>> entry : answersByTest.entrySet()) {
                Long testId = entry.getKey();
                List<StudentTestAnswer> testAnswers = entry.getValue();

                // 获取测试信息
                Test test = testMapper.selectById(testId);
                if (test == null) continue;

                // 遍历该测试的所有答题记录
                for (StudentTestAnswer answer : testAnswers) {
                    // 获取题目详情
                    Map<String, Object> question = testMapper.findQuestionById(answer.getQuestionId());
                    if (question == null) continue;
                    
                    Integer type = (Integer) question.get("type");
                    Integer questionId = answer.getQuestionId();
                    
                    // 判断题：如果这道题还没有添加过，就添加
                    boolean isWrong = false;
                    
                    if (type != null && type == 4) {
                        // 简答题：得分不满就收录（包括0分）
                        if (answer.getScore() != null && question.get("score") != null) {
                            BigDecimal actualScore = BigDecimal.valueOf(answer.getScore());
                            BigDecimal fullScore = (BigDecimal) question.get("score");
                            isWrong = actualScore.compareTo(fullScore) < 0; // 实际得分 < 满分
                        }
                    } else {
                        // 客观题（单选、多选、判断）：得分为0才收录
                        isWrong = answer.getScore() != null && answer.getScore() == 0 && answer.getStatus() == 1;
                    }
                    
                    if (isWrong && !uniqueQuestions.containsKey(questionId)) {
                        Map<String, Object> wrongQuestion = new HashMap<>();
                        wrongQuestion.put("testId", testId);
                        wrongQuestion.put("testName", test.getTitle());
                        wrongQuestion.put("questionId", questionId);
                        wrongQuestion.put("questionContent", question.get("content"));
                        wrongQuestion.put("options", question.get("options"));
                        wrongQuestion.put("correctAnswer", question.get("answer"));
                        wrongQuestion.put("studentAnswer", answer.getAnswer());
                        wrongQuestion.put("type", type);
                        wrongQuestion.put("score", question.get("score"));
                        wrongQuestion.put("isCorrect", false); // 错题本中的题目都是答错的
                        
                        uniqueQuestions.put(questionId, wrongQuestion);
                    }
                }
            }
            
            // 将去重后的题目添加到列表中
            wrongQuestionsList.addAll(uniqueQuestions.values());

            System.out.println("错题数量: " + wrongQuestionsList.size());
            return ApiResponse.ok("获取成功", wrongQuestionsList);
        } catch (Exception e) {
            System.err.println("获取错题本失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "获取错题本失败: " + e.getMessage());
        }
    }
}
