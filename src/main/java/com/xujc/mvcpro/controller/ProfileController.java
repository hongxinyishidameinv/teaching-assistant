package com.xujc.mvcpro.controller;

import com.xujc.mvcpro.common.ApiResponse;
import com.xujc.mvcpro.pojo.User;
import com.xujc.mvcpro.service.UserService;
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

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/profile")
    public ApiResponse getProfile(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ApiResponse.error(null, "请先登录");
        }
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getUid());
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        profile.put("type", user.getType());
        profile.put("status", user.getStatus());
        
        String typeName = switch (user.getType()) {
            case "0" -> "管理员";
            case "1" -> "教师";
            case "2" -> "学生";
            default -> "未知";
        };
        profile.put("typeName", typeName);
        
        return ApiResponse.ok("获取成功", profile);
    }

    /**
     * 更新用户信息（用户名、邮箱）
     */
    @PutMapping("/profile")
    public ApiResponse updateProfile(@RequestBody Map<String, String> params, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ApiResponse.error(null, "请先登录");
        }
        
        String username = params.get("username");
        String email = params.get("email");
        
        if (username != null && !username.trim().isEmpty()) {
            user.setUsername(username);
        }
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email);
        }
        
        userService.updateUser(user);
        
        // 更新session中的用户信息
        session.setAttribute("user", user);
        
        return ApiResponse.ok("更新成功", null);
    }

    /**
     * 修改密码（需要验证原密码）
     */
    @PutMapping("/password")
    public ApiResponse updatePassword(@RequestBody Map<String, String> params, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ApiResponse.error(null, "请先登录");
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