package com.xujc.mvcpro.service.impl;

import com.xujc.mvcpro.mapper.CourseMapper;
import com.xujc.mvcpro.mapper.UserMapper;
import com.xujc.mvcpro.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 用户总数
        int userTotal = userMapper.countUsers();
        stats.put("userTotal", userTotal);
        
        // 课程总数
        int courseTotal = courseMapper.countCourses();
        stats.put("courseTotal", courseTotal);
        
        // 教师总数（type=1）
        int teacherTotal = userMapper.countByType("1");
        stats.put("teacherTotal", teacherTotal);
        
        // 学生总数（type=2）
        int studentTotal = userMapper.countByType("2");
        stats.put("studentTotal", studentTotal);
        
        return stats;
    }
}