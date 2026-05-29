package com.xujc.mvcpro.mapper;

import com.xujc.mvcpro.pojo.Test;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TestMapper {
    int insert(Test test);
    int update(Test test);
    int deleteById(Long id);
    Test selectById(Long id);
    
    List<Test> selectPage(
            @Param("courseId") Long courseId,
            @Param("offset") int offset,
            @Param("size") int size
    );
    
    int count(@Param("courseId") Long courseId);
    
    List<Test> selectByCourseId(Long courseId);
    
    /**
     * 根据学生ID查询测试列表
     */
    List<Test> findTestsByStudentId(@Param("studentId") Integer studentId);
    
    /**
     * 根据题目ID查询题目详情
     */
    Map<String, Object> findQuestionById(@Param("questionId") Integer questionId);
    
    /**
     * 统计课程学生数
     */
    int countCourseStudents(@Param("courseId") Long courseId);
}
