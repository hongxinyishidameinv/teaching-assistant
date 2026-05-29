package com.xujc.mvcpro.mapper;

import com.xujc.mvcpro.pojo.TestQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TestQuestionMapper {
    int insert(TestQuestion testQuestion);
    int deleteById(Long id);
    int deleteByTestId(Long testId);
    List<TestQuestion> selectByTestId(Long testId);
    List<TestQuestion> selectByTestIds(@Param("testIds") List<Long> testIds);
    int batchInsert(@Param("list") List<TestQuestion> list);
}
