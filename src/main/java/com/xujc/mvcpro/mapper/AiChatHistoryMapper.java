package com.xujc.mvcpro.mapper;

import com.xujc.mvcpro.pojo.AiChatHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI对话历史Mapper
 */
@Mapper
public interface AiChatHistoryMapper {
    
    /**
     * 插入对话记录
     */
    @Insert("INSERT INTO ai_chat_history (user_id, user_name, question, answer, model) " +
            "VALUES (#{userId}, #{userName}, #{question}, #{answer}, #{model})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AiChatHistory history);
    
    /**
     * 根据用户ID查询对话历史（分页）
     */
    @Select("SELECT * FROM ai_chat_history WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    List<AiChatHistory> selectByUserId(@Param("userId") Integer userId, 
                                       @Param("offset") int offset, 
                                       @Param("size") int size);
    
    /**
     * 统计用户的对话记录数
     */
    @Select("SELECT COUNT(*) FROM ai_chat_history WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据ID查询对话记录
     */
    @Select("SELECT * FROM ai_chat_history WHERE id = #{id}")
    AiChatHistory selectById(@Param("id") Long id);
    
    /**
     * 删除用户的对话记录
     */
    @Delete("DELETE FROM ai_chat_history WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Integer userId);
    
    /**
     * 删除单条对话记录
     */
    @Delete("DELETE FROM ai_chat_history WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
