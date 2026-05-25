package com.xujc.mvcpro.controller;

import com.xujc.mvcpro.common.ApiResponse;
import com.xujc.mvcpro.common.ResponseCode;
import com.xujc.mvcpro.pojo.User;
import com.xujc.mvcpro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

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
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("uid", user.getUid());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("type", user.getType());
        return ApiResponse.ok("登录成功", userInfo);
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
