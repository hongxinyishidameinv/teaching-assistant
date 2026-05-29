package com.xujc.mvcpro.service;

import com.xujc.mvcpro.common.PageResult;
import com.xujc.mvcpro.common.ResponseCode;
import com.xujc.mvcpro.exception.BusinessException;
import com.xujc.mvcpro.mapper.CourseMapper;
import com.xujc.mvcpro.pojo.Course;
import com.xujc.mvcpro.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private com.xujc.mvcpro.mapper.UserMapper userMapper;

    public PageResult<Course> getCoursesByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Course> courses = courseMapper.findCoursesByPage(offset, pageSize);
        int total = courseMapper.countCourses();
        int pages = (int) Math.ceil((double) total / pageSize);
        return new PageResult<>(courses, pageNum, pageSize, total, pages);
    }

    public PageResult<Course> searchCoursesByPage(String keyword, Integer status, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        String likeKeyword = "%" + keyword + "%";
        List<Course> courses = courseMapper.searchCoursesByPage(likeKeyword, status, offset, pageSize);
        int total = courseMapper.countSearchCourses(likeKeyword, status);
        int pages = (int) Math.ceil((double) total / pageSize);
        return new PageResult<>(courses, pageNum, pageSize, total, pages);
    }

    public Course getCourseById(Integer cid) {
        return courseMapper.findById(cid);
    }

    public boolean addCourse(Course course) {
        course.setStatus(1);
        int result = courseMapper.insertCourse(course);
        return result > 0;
    }

    public boolean updateCourse(Course course) {
        Course existingCourse = courseMapper.findById(course.getCid());
        if (existingCourse == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND, "课程不存在");
        }
        int result = courseMapper.updateCourse(course);
        return result > 0;
    }

    public boolean deleteCourse(Integer cid) {
        Course existingCourse = courseMapper.findById(cid);
        if (existingCourse == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND, "课程不存在");
        }
        int result = courseMapper.deleteCourse(cid);
        return result > 0;
    }

    public boolean batchDeleteCourses(List<Integer> cids) {
        if (cids == null || cids.isEmpty()) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "请选择要删除的课程");
        }
        int result = courseMapper.batchDeleteCourses(cids);
        return result > 0;
    }

    public boolean updateCourseStatus(Integer cid, Integer status) {
        Course existingCourse = courseMapper.findById(cid);
        if (existingCourse == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND, "课程不存在");
        }
        int result = courseMapper.updateCourseStatus(cid, status);
        return result > 0;
    }

    public List<User> getAllTeachers() {
        List<User> allUsers = userMapper.findAllUsers();
        return allUsers.stream().filter(u -> "1".equals(u.getType())).collect(java.util.stream.Collectors.toList());
    }
}