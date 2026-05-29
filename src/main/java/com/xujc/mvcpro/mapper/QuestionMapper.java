package com.xujc.mvcpro.mapper;

import com.xujc.mvcpro.pojo.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuestionMapper {
    int insert(Question question);
    int update(Question question);
    int deleteById(Long id);
    int batchDelete(@Param("ids") List<Long> ids);
    Question selectById(Long id);
    List<Question> selectByCourseId(Long courseId);
    List<Question> selectByKnowledgePoint(String knowledgePoint);
    
    List<Question> selectPage(
            @Param("courseId") Long courseId,
            @Param("knowledgePoint") String knowledgePoint,
            @Param("offset") int offset,
            @Param("size") int size
    );
    
    int count(
            @Param("courseId") Long courseId,
            @Param("knowledgePoint") String knowledgePoint
    );
    
    List<Question> selectByIds(@Param("ids") List<Long> ids);
}
