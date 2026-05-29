package com.xujc.mvcpro.mapper;

import com.xujc.mvcpro.pojo.Notice;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NoticeMapper {

    @Insert("INSERT INTO notice(title, content, top, status, create_time, update_time) " +
            "VALUES(#{title}, #{content}, #{top}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notice notice);

    @Update("UPDATE notice SET title=#{title}, content=#{content}, top=#{top}, status=#{status}, update_time=#{updateTime} WHERE id=#{id}")
    int updateById(Notice notice);

    @Delete("DELETE FROM notice WHERE id=#{id}")
    int deleteById(Long id);

    @Select("SELECT id, title, content, top, status, create_time as createTime, update_time as updateTime FROM notice WHERE id=#{id}")
    Notice selectById(Long id);

    @Select("SELECT id, title, content, top, status, create_time as createTime, update_time as updateTime FROM notice ORDER BY top DESC, create_time DESC")
    List<Notice> selectAll();

    @Select("<script>SELECT id, title, content, top, status, create_time as createTime, update_time as updateTime FROM notice WHERE 1=1" +
            "<if test='title != null and title != \"\"'> AND title LIKE CONCAT('%', #{title}, '%')</if>" +
            "<if test='top != null'> AND top = #{top}</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            " ORDER BY top DESC, create_time DESC LIMIT #{offset}, #{size}</script>")
    List<Notice> selectByPage(@Param("title") String title, @Param("top") Integer top, @Param("status") Integer status,
                              @Param("offset") Integer offset, @Param("size") Integer size);

    @Select("<script>SELECT COUNT(*) FROM notice WHERE 1=1" +
            "<if test='title != null and title != \"\"'> AND title LIKE CONCAT('%', #{title}, '%')</if>" +
            "<if test='top != null'> AND top = #{top}</if>" +
            "<if test='status != null'> AND status = #{status}</if></script>")
    int countByCondition(@Param("title") String title, @Param("top") Integer top, @Param("status") Integer status);
}