package com.xujc.mvcpro.controller;

import com.xujc.mvcpro.common.ApiResponse;
import com.xujc.mvcpro.pojo.User;
import com.xujc.mvcpro.service.CourseService;
import com.xujc.mvcpro.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/profile")
    public ApiResponse getProfile(HttpServletRequest request) {
        // 从请求属性中获取用户信息（JWT拦截器设置的）
        Integer uid = (Integer) request.getAttribute("uid");
        String username = (String) request.getAttribute("username");
        Integer type = (Integer) request.getAttribute("type");
        
        if (uid == null || username == null) {
            return ApiResponse.error(null, "请先登录");
        }
        
        // 从数据库获取完整用户信息（包括邮箱）
        User user = userService.getUserById(uid);
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", uid);
        profile.put("username", username);
        profile.put("email", user != null ? user.getEmail() : "");
        profile.put("type", type);
        
        // 添加统计信息
        if (type == 1) {
            // 教师：课程数量和学生数量
            int courseCount = courseService.countCoursesByTeacherId(uid);
            int studentCount = courseService.countStudentsByTeacherId(uid);
            profile.put("courseCount", courseCount);
            profile.put("studentCount", studentCount);
        } else if (type == 2) {
            // 学生：课程数量
            int courseCount = courseService.countCoursesByStudentId(uid);
            profile.put("courseCount", courseCount);
        }
        
        String typeName = switch (type) {
            case 0 -> "管理员";
            case 1 -> "教师";
            case 2 -> "学生";
            default -> "未知";
        };
        profile.put("typeName", typeName);
        
        return ApiResponse.ok("获取成功", profile);
    }

    /**
     * 更新用户信息（用户名、邮箱）
     */
    @PutMapping("/profile")
    public ApiResponse updateProfile(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Integer uid = (Integer) request.getAttribute("uid");
        String username = (String) request.getAttribute("username");
        if (uid == null || username == null) {
            return ApiResponse.error(null, "请先登录");
        }
        
        // 直接创建用户对象进行更新
        User user = new User();
        user.setUid(uid);
        
        String newUsername = params.get("username");
        String email = params.get("email");
        
        if (newUsername != null && !newUsername.trim().isEmpty()) {
            user.setUsername(newUsername);
        }
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email);
        }
        
        userService.updateUser(user);
        
        return ApiResponse.ok("更新成功", null);
    }

    /**
     * 修改密码（需要验证原密码）
     */
    @PutMapping("/password")
    public ApiResponse updatePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Integer uid = (Integer) request.getAttribute("uid");
        String username = (String) request.getAttribute("username");
        if (uid == null || username == null) {
            return ApiResponse.error(null, "请先登录");
        }
        
        // 从数据库获取用户信息以验证原密码
        User user = userService.getUserById(uid);
        if (user == null) {
            return ApiResponse.error(null, "用户不存在");
        }
        
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        if (oldPassword == null || newPassword == null) {
            return ApiResponse.error(null, "参数不能为空");
        }
        
        // 验证原密码
        if (!user.getPassword().equals(oldPassword)) {
            return ApiResponse.error(null, "原密码错误");
        }
        
        // 更新密码
        user.setPassword(newPassword);
        userService.updateUser(user);
        
        return ApiResponse.ok("密码修改成功", null);
    }
}