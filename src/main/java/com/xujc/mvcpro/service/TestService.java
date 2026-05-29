package com.xujc.mvcpro.service;

import com.xujc.mvcpro.pojo.Test;
import com.xujc.mvcpro.pojo.TestQuestion;
import com.xujc.mvcpro.pojo.Course;
import com.xujc.mvcpro.mapper.CourseMapper;
import com.xujc.mvcpro.mapper.TestMapper;
import com.xujc.mvcpro.mapper.TestQuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TestService {
    
    @Autowired
    private TestMapper testMapper;
    
    @Autowired
    private TestQuestionMapper testQuestionMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    public Map<String, Object> getPage(Long courseId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Test> tests = testMapper.selectPage(courseId, offset, pageSize);
        int total = testMapper.count(courseId);
        
        for (Test t : tests) {
            Course course = courseMapper.findById(t.getCourseId().intValue());
            if (course != null) {
                t.setCourseName(course.getCourseName());
            }
            List<TestQuestion> tqs = testQuestionMapper.selectByTestId(t.getId());
            t.setQuestionCount(tqs.size());
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", tests);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("pages", (total + pageSize - 1) / pageSize);
        return result;
    }
    
    public Test getById(Long id) {
        Test test = testMapper.selectById(id);
        if (test != null) {
            Course course = courseMapper.findById(test.getCourseId().intValue());
            if (course != null) {
                test.setCourseName(course.getCourseName());
            }
        }
        return test;
    }
    
    @Transactional
    public int add(Test test, List<TestQuestion> testQuestions) {
        // 先设置总分，防止为空
        BigDecimal totalScore = BigDecimal.ZERO;
        
        if (testQuestions != null && !testQuestions.isEmpty()) {
            for (TestQuestion tq : testQuestions) {
                totalScore = totalScore.add(tq.getScore());
            }
        }
        
        test.setTotalScore(totalScore);
        int result = testMapper.insert(test);
        
        if (testQuestions != null && !testQuestions.isEmpty()) {
            for (TestQuestion tq : testQuestions) {
                tq.setTestId(test.getId());
            }
            testQuestionMapper.batchInsert(testQuestions);
        }
        
        return result;
    }
    
    @Transactional
    public int update(Test test, List<TestQuestion> testQuestions) {
        // 删除原有题目关联
        testQuestionMapper.deleteByTestId(test.getId());
        
        BigDecimal totalScore = BigDecimal.ZERO;
        if (testQuestions != null && !testQuestions.isEmpty()) {
            for (TestQuestion tq : testQuestions) {
                tq.setTestId(test.getId());
                totalScore = totalScore.add(tq.getScore());
            }
            testQuestionMapper.batchInsert(testQuestions);
        }
        
        test.setTotalScore(totalScore);
        return testMapper.update(test);
    }
    
    @Transactional
    public int delete(Long id) {
        testQuestionMapper.deleteByTestId(id);
        return testMapper.deleteById(id);
    }
    
    public List<Map<String, Object>> getQuestionsByTestId(Long testId) {
        List<TestQuestion> testQuestions = testQuestionMapper.selectByTestId(testId);
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        
        for (TestQuestion tq : testQuestions) {
            Map<String, Object> questionInfo = new HashMap<>();
            questionInfo.put("questionId", tq.getQuestionId());
            questionInfo.put("score", tq.getScore().intValue());
            
            // 查询题目详情
            Map<String, Object> question = testMapper.findQuestionById(tq.getQuestionId().intValue());
            if (question != null) {
                questionInfo.put("type", question.get("type"));
                questionInfo.put("content", question.get("content"));
                questionInfo.put("options", question.get("options"));
                questionInfo.put("answer", question.get("answer"));
            }
            
            result.add(questionInfo);
        }
        
        return result;
    }
}
