package com.xujc.mvcpro.controller;

import com.xujc.mvcpro.common.ApiResponse;
import com.xujc.mvcpro.common.ResponseCode;
import com.xujc.mvcpro.pojo.User;
import com.xujc.mvcpro.service.UserService;
import com.xujc.mvcpro.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ApiResponse login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        
        if (username == null || username.isEmpty()) {
            return ApiResponse.error(ResponseCode.BAD_REQUEST, "用户名不能为空");
        }
        if (password == null || password.isEmpty()) {
            return ApiResponse.error(ResponseCode.BAD_REQUEST, "密码不能为空");
        }
        
        User user = userService.login(username, password);
        
        // 生成JWT Token（type从String转为Integer）
        Integer userType = Integer.parseInt(user.getType());
        String token = jwtUtil.generateToken(user.getUid(), user.getUsername(), userType);
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("uid", user.getUid());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("type", user.getType());
        userInfo.put("token", token); // 返回Token
        
        return ApiResponse.ok("登录成功", userInfo);
    }

    @PostMapping("/logout")
    public ApiResponse logout(HttpServletRequest request) {
        // JWT是无状态的，服务端不需要做任何处理
        // 前端需要清除本地存储的Token
        return ApiResponse.ok("退出成功");
    }

    @PostMapping("/register")
    public ApiResponse register(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return ApiResponse.error(ResponseCode.BAD_REQUEST, "用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return ApiResponse.error(ResponseCode.BAD_REQUEST, "密码不能为空");
        }
        
        userService.register(user);
        return ApiResponse.ok("注册成功，请登录");
    }
}
