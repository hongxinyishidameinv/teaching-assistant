package com.xujc.mvcpro.mapper;

import com.xujc.mvcpro.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from user where username = #{username} and password = #{password}")
    User checkLoginUser(@Param("username") String username, @Param("password") String password);

    @Select("select * from user where username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("select * from user")
    List<User> findAllUsers();

    @Select("select * from user where uid = #{uid}")
    User findById(@Param("uid") Integer uid);

    @Insert("insert into user(username, password, email, type) values(#{username}, #{password}, #{email}, #{type})")
    @Options(useGeneratedKeys = true, keyProperty = "uid")
    int insertUser(User user);

    /**
     * 根据ID删除用户
     * @param uid 用户ID
     * @return 影响的行数
     */
    @Delete("delete from user where uid=#{uid}")
    int deleteUser(@Param("uid") Integer uid);

    @Update("update user set username=#{username}, password=#{password}, email=#{email}, type=#{type}, status=#{status} where uid=#{uid}")
    int updateUser(User user);

    @Delete("<script>delete from user where uid in (<foreach collection='uids' item='uid' separator=','>#{uid}</foreach>)</script>")
    int batchDeleteUsers(@Param("uids") List<Integer> uids);

    @Update("update user set status=#{status} where uid=#{uid}")
    int updateUserStatus(@Param("uid") Integer uid, @Param("status") Integer status);

    @Select("select * from user where username like #{keyword}")
    List<User> searchUsers(@Param("keyword") String keyword);

    @Select("select * from user limit #{offset}, #{pageSize}")
    List<User> findUsersByPage(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    @Select("select count(*) from user")
    int countUsers();

    @Select("select * from user where username like #{keyword} limit #{offset}, #{pageSize}")
    List<User> searchUsersByPage(@Param("keyword") String keyword, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    @Select("select count(*) from user where username like #{keyword}")
    int countSearchUsers(@Param("keyword") String keyword);

    @Select("select * from user where uid = #{uid}")
    User findByIdWithStatus(@Param("uid") Integer uid);
}