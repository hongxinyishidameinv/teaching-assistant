package com.xujc.mvcpro.service.impl;

import com.xujc.mvcpro.mapper.CourseMapper;
import com.xujc.mvcpro.mapper.UserMapper;
import com.xujc.mvcpro.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
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
        
        // 管理员总数（type=0）
        int adminTotal = userMapper.countByType("0");
        stats.put("adminTotal", adminTotal);
        
        // 已启用课程数（status=1）
        int enabledCourseTotal = courseMapper.countByStatus(1);
        stats.put("enabledCourseTotal", enabledCourseTotal);
        
        // 已禁用课程数（status=0）
        int disabledCourseTotal = courseMapper.countByStatus(0);
        stats.put("disabledCourseTotal", disabledCourseTotal);
        
        // 获取课程状态分布
        Map<String, Object> courseStatusDistribution = new HashMap<>();
        courseStatusDistribution.put("enabled", enabledCourseTotal);
        courseStatusDistribution.put("disabled", disabledCourseTotal);
        stats.put("courseStatusDistribution", courseStatusDistribution);
        
        // 获取用户类型分布
        Map<String, Object> userTypeDistribution = new HashMap<>();
        userTypeDistribution.put("admin", adminTotal);
        userTypeDistribution.put("teacher", teacherTotal);
        userTypeDistribution.put("student", studentTotal);
        stats.put("userTypeDistribution", userTypeDistribution);
        
        // 获取最近课程数据（包含选课人数）
        List<Map<String, Object>> recentCourses = courseMapper.getRecentCoursesWithStudentCount();
        stats.put("recentCourses", recentCourses);
        
        return stats;
    }
}