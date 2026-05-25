package com.xujc.mvcpro.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户实体类
 * 使用Lombok注解简化代码
 */
@Data                    // 自动生成getter、setter、toString、equals、hashCode
@NoArgsConstructor       // 自动生成无参构造方法
@AllArgsConstructor      // 自动生成全参构造方法
public class User {
    private Integer uid;          // 用户ID
    private String username;     // 用户名
    private String password;     // 密码
    private String email;        // 邮箱
    private String type;         // 用户类型：0-管理员，1-教师，2-学生
    private Integer status;      // 账号状态：0-禁用，1-启用
}
