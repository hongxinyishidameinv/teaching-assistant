package com.xujc.mvcpro.controller;

import com.xujc.mvcpro.common.ApiResponse;
import com.xujc.mvcpro.common.PageResult;
import com.xujc.mvcpro.common.ResponseCode;
import com.xujc.mvcpro.pojo.User;
import com.xujc.mvcpro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ApiResponse getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users == null || users.isEmpty()) {
            return ApiResponse.ok("暂无用户数据", List.of());
        }
        return ApiResponse.ok("获取用户列表成功", users);
    }

    @DeleteMapping("/{uid}")
    public ApiResponse deleteUser(@PathVariable Integer uid) {
        boolean result = userService.deleteUser(uid);
        if (result) {
            return ApiResponse.ok("删除成功", null);
        }
        return ApiResponse.error(ResponseCode.valueOf("删除失败"), null);
    }

    @PostMapping
    public ApiResponse addUser(@RequestBody User user) {
        boolean result = userService.addUser(user);
        if (result) {
            return ApiResponse.ok("添加成功", user);
        }
        return ApiResponse.error(ResponseCode.valueOf("添加失败"), null);
    }

    @PutMapping
    public ApiResponse updateUser(@RequestBody User user) {
        boolean result = userService.updateUser(user);
        if (result) {
            return ApiResponse.ok("更新成功", user);
        }
        return ApiResponse.error(ResponseCode.valueOf("更新失败"), null);
    }

    @GetMapping("/search")
    public ApiResponse searchUsers(@RequestParam String keyword) {
        List<User> users = userService.searchUsers(keyword);
        if (users == null || users.isEmpty()) {
            return ApiResponse.ok("未找到匹配的用户", List.of());
        }
        return ApiResponse.ok("搜索成功", users);
    }

    @GetMapping("/page")
    public ApiResponse getUsersByPage(@RequestParam(defaultValue = "1") int pageNum, 
                                      @RequestParam(defaultValue = "15") int pageSize) {
        PageResult<User> pageResult = userService.getUsersByPage(pageNum, pageSize);
        return ApiResponse.ok("获取分页数据成功", pageResult);
    }

    @GetMapping("/search/page")
    public ApiResponse searchUsersByPage(@RequestParam String keyword,
                                         @RequestParam(defaultValue = "1") int pageNum, 
                                         @RequestParam(defaultValue = "15") int pageSize) {
        PageResult<User> pageResult = userService.searchUsersByPage(keyword, pageNum, pageSize);
        return ApiResponse.ok("搜索成功", pageResult);
    }

    @DeleteMapping("/batch")
    public ApiResponse batchDeleteUsers(@RequestBody List<Integer> uids) {
        boolean result = userService.batchDeleteUsers(uids);
        if (result) {
            return ApiResponse.ok("批量删除成功", null);
        }
        return ApiResponse.error(ResponseCode.INTERNAL_ERROR, "批量删除失败");
    }

    @PutMapping("/status")
    public ApiResponse updateUserStatus(@RequestParam Integer uid, @RequestParam Integer status) {
        try {
            System.out.println("接收到状态更新请求: uid=" + uid + ", status=" + status);
            
            boolean result = userService.updateUserStatus(uid, status);
            
            if (result) {
                return ApiResponse.ok(status == 1 ? "启用成功" : "禁用成功", null);
            }
            return ApiResponse.error(ResponseCode.INTERNAL_ERROR, "状态更新失败");
        } catch (Exception e) {
            System.out.println("状态更新异常: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(ResponseCode.INTERNAL_ERROR, "状态更新失败: " + e.getMessage());
        }
    }

}
