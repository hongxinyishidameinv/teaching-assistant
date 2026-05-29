package com.xujc.mvcpro.mapper;

import com.xujc.mvcpro.pojo.StudentTestAnswer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentTestAnswerMapper {
    void insert(StudentTestAnswer answer);
    void batchInsert(List<StudentTestAnswer> answers);
    void updateScore(Long id, Integer score);
    List<StudentTestAnswer> selectByTestId(Long testId);
    List<StudentTestAnswer> selectByTestIdAndStudentId(Long testId, Integer studentId);
    List<StudentTestAnswer> selectByStudentId(Integer studentId);
    List<Map<String, Object>> selectTestResults(Long testId);
    int countSubmittedStudents(Long testId);
    int countUngradedStudents(Long testId);
}
