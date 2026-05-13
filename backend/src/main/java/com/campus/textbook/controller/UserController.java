package com.campus.textbook.controller;

import com.campus.textbook.common.Result;
import com.campus.textbook.entity.User;
import com.campus.textbook.service.UserService;
import com.campus.textbook.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户相关接口（注册、登录、个人信息）
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 用户注册（论文5.1.1，TC-LOGIN系列前置）
     */
    @PostMapping("/register")
    public Result register(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String realName = params.get("realName");
        String phone = params.get("phone");
        String email = params.get("email");
        String major = params.get("major");
        return userService.register(username, password, realName, phone, email, major);
    }

    /**
     * 用户/管理员登录（论文5.1.1，对应TC-LOGIN-001~004）
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        return userService.login(username, password);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result getUserInfo(Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        User user = userService.getById(userId);
        if (user == null) return Result.error("用户不存在");
        return Result.success(user);
    }

    /**
     * 修改个人信息（论文5.1.1）
     */
    @PutMapping("/update")
    public Result updateProfile(@RequestBody User params, Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        return userService.updateProfile(userId, params);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result changePassword(@RequestBody Map<String, String> params, Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        String oldPwd = params.get("oldPassword");
        String newPwd = params.get("newPassword");
        return userService.changePassword(userId, oldPwd, newPwd);
    }
}
