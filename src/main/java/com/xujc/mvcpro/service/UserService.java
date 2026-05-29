package com.xujc.mvcpro.service;

import com.xujc.mvcpro.common.PageResult;
import com.xujc.mvcpro.common.ResponseCode;
import com.xujc.mvcpro.exception.BusinessException;
import com.xujc.mvcpro.mapper.UserMapper;
import com.xujc.mvcpro.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User login(String username, String password) {
        User user = userMapper.checkLoginUser(username, password);
        if (user == null) {
            throw new BusinessException(ResponseCode.UNAUTHORIZED, "用户名或密码错误");
        }
        // 检查用户状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "您的账号已被禁用，请联系管理员");
        }
        return user;
    }

    public boolean checkUsernameExists(String username) {
        User user = userMapper.findByUsername(username);
        return user != null;
    }

    public boolean register(User user) {
        if (checkUsernameExists(user.getUsername())) {
            throw new BusinessException(ResponseCode.CONFLICT, "用户名已存在");
        }
        user.setType("2");
        user.setStatus(1);
        int result = userMapper.insertUser(user);
        return result > 0;
    }

    public List<User> getAllUsers() {
        return userMapper.findAllUsers();
    }

    /**
     * 删除用户
     * @param uid 用户ID
     * @return 是否删除成功
     */
    public boolean deleteUser(Integer uid) {
        // 检查用户是否存在
        User existingUser = userMapper.findById(uid);
        if (existingUser == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND, "用户不存在");
        }
        int result = userMapper.deleteUser(uid);
        return result > 0;
    }

    public boolean addUser(User user) {
        if (checkUsernameExists(user.getUsername())) {
            throw new BusinessException(ResponseCode.CONFLICT, "用户名已存在");
        }
        int result = userMapper.insertUser(user);
        return result > 0;
    }

    public boolean updateUser(User user) {
        // 检查用户是否存在
        User existingUser = userMapper.findByIdWithStatus(user.getUid());
        if (existingUser == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND, "用户不存在");
        }
        // 如果用户名改变，检查新用户名是否已存在
        if (!existingUser.getUsername().equals(user.getUsername()) && checkUsernameExists(user.getUsername())) {
            throw new BusinessException(ResponseCode.CONFLICT, "用户名已存在");
        }
        int result = userMapper.updateUser(user);
        return result > 0;
    }

    public boolean batchDeleteUsers(List<Integer> uids) {
        if (uids == null || uids.isEmpty()) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "请选择要删除的用户");
        }
        int result = userMapper.batchDeleteUsers(uids);
        return result > 0;
    }

    public boolean updateUserStatus(Integer uid, Integer status) {
        if (uid == null || status == null) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "参数不完整");
        }
        User existingUser = userMapper.findByIdWithStatus(uid);
        if (existingUser == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND, "用户不存在");
        }
        // 获取用户类型，处理 null 情况
        String userType = existingUser.getType();
        if (userType == null) {
            userType = "2"; // 默认视为学生
        }
        // 管理员不能被禁用
        if ("0".equals(userType) && status == 0) {
            throw new BusinessException(ResponseCode.FORBIDDEN, "管理员账号不能被禁用");
        }
        int result = userMapper.updateUserStatus(uid, status);
        return result > 0;
    }

    public List<User> searchUsers(String keyword) {
        return userMapper.searchUsers("%" + keyword + "%");
    }

    public PageResult<User> getUsersByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<User> users = userMapper.findUsersByPage(offset, pageSize);
        int total = userMapper.countUsers();
        int pages = (int) Math.ceil((double) total / pageSize);
        return new PageResult<>(users, pageNum, pageSize, total, pages);
    }

    public PageResult<User> searchUsersByPage(String keyword, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<User> users = userMapper.searchUsersByPage("%" + keyword + "%", offset, pageSize);
        int total = userMapper.countSearchUsers("%" + keyword + "%");
        int pages = (int) Math.ceil((double) total / pageSize);
        return new PageResult<>(users, pageNum, pageSize, total, pages);
    }
}
