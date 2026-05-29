package com.xujc.mvcpro.service;

import com.xujc.mvcpro.pojo.Question;
import com.xujc.mvcpro.pojo.Course;
import com.xujc.mvcpro.mapper.CourseMapper;
import com.xujc.mvcpro.mapper.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionService {
    
    @Autowired
    private QuestionMapper questionMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    public Map<String, Object> getPage(Long courseId, String knowledgePoint, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Question> questions = questionMapper.selectPage(courseId, knowledgePoint, offset, pageSize);
        int total = questionMapper.count(courseId, knowledgePoint);
        
        for (Question q : questions) {
            if (q.getCourseId() != null) {
                Course course = courseMapper.findById(q.getCourseId().intValue());
                if (course != null) {
                    q.setCourseName(course.getCourseName());
                }
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", questions);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("pages", (total + pageSize - 1) / pageSize);
        return result;
    }
    
    public Question getById(Long id) {
        Question question = questionMapper.selectById(id);
        if (question != null && question.getCourseId() != null) {
            Course course = courseMapper.findById(question.getCourseId().intValue());
            if (course != null) {
                question.setCourseName(course.getCourseName());
            }
        }
        return question;
    }
    
    @Transactional
    public int add(Question question) {
        return questionMapper.insert(question);
    }
    
    @Transactional
    public int update(Question question) {
        return questionMapper.update(question);
    }
    
    @Transactional
    public int delete(Long id) {
        return questionMapper.deleteById(id);
    }
    
    @Transactional
    public int batchDelete(List<Long> ids) {
        return questionMapper.batchDelete(ids);
    }
    
    public List<Question> getByCourseId(Long courseId) {
        List<Question> questions = questionMapper.selectByCourseId(courseId);
        for (Question q : questions) {
            if (q.getCourseId() != null) {
                Course course = courseMapper.findById(q.getCourseId().intValue());
                if (course != null) {
                    q.setCourseName(course.getCourseName());
                }
            }
        }
        return questions;
    }
}
