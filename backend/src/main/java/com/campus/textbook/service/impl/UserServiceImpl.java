package com.campus.textbook.service.impl;

import com.campus.textbook.common.Result;
import com.campus.textbook.entity.Admin;
import com.campus.textbook.entity.User;
import com.campus.textbook.entity.UserEco;
import com.campus.textbook.mapper.AdminMapper;
import com.campus.textbook.mapper.UserEcoMapper;
import com.campus.textbook.mapper.UserMapper;
import com.campus.textbook.service.UserService;
import com.campus.textbook.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private UserEcoMapper userEcoMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 用户注册（论文5.1.1核心代码）
     * 检查用户名是否存在，不存在则插入新用户
     */
    @Override
    public Result register(String username, String password, String realName,
                           String phone, String email, String major) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return Result.error("用户名和密码不能为空");
        }
        if (!StringUtils.hasText(realName)) {
            return Result.error("姓名不能为空");
        }
        // 检查用户名是否已存在
        User existing = userMapper.selectByUsername(username);
        if (existing != null) {
            return Result.error("该用户已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));  // BCrypt加密
        user.setRealName(realName);
        user.setPhone(phone);
        user.setEmail(email);
        user.setMajor(major);
        user.setUserType(1);    // 默认普通用户
        user.setStatus(1);      // 默认正常
        user.setRegTime(new Date());
        userMapper.insert(user);

        // 初始化用户环保贡献记录
        UserEco eco = new UserEco();
        eco.setUserId(user.getId());
        eco.setSellCount(0);
        eco.setSavedAmount(new java.math.BigDecimal("0.00"));
        eco.setCo2Reduction(new java.math.BigDecimal("0.00"));
        userEcoMapper.insert(eco);

        return Result.success("注册成功");
    }

    /**
     * 登录（论文5.1.1核心代码）
     * 支持普通用户和管理员登录，根据userType返回对应Token
     */
    @Override
    public Result login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return Result.error("学号和密码不能为空");
        }

        // 先查普通用户
        User user = userMapper.selectByUsername(username);
        if (user != null) {
            if (user.getStatus() == 0) {
                return Result.error("该账号已被禁用，请联系管理员");
            }
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return Result.error("密码错误");
            }
            String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getUserType());
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userType", user.getUserType());
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("realName", user.getRealName());
            return Result.success("登录成功", data);
        }

        // 再查管理员表
        Admin admin = adminMapper.selectByUsername(username);
        if (admin != null) {
            if (!passwordEncoder.matches(password, admin.getPassword())) {
                return Result.error("密码错误");
            }
            // 管理员userType=2
            String token = jwtUtils.generateToken(admin.getId(), admin.getUsername(), 2);
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userType", 2);
            data.put("userId", admin.getId());
            data.put("username", admin.getUsername());
            data.put("realName", admin.getRealName());
            return Result.success("登录成功", data);
        }

        return Result.error("用户名不存在");
    }

    /**
     * 修改个人信息（论文5.1.1）
     */
    @Override
    public Result updateProfile(Integer userId, User params) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 只允许修改这几个字段
        if (StringUtils.hasText(params.getRealName())) {
            user.setRealName(params.getRealName());
        }
        if (StringUtils.hasText(params.getPhone())) {
            user.setPhone(params.getPhone());
        }
        if (StringUtils.hasText(params.getEmail())) {
            user.setEmail(params.getEmail());
        }
        if (StringUtils.hasText(params.getMajor())) {
            user.setMajor(params.getMajor());
        }
        int rows = userMapper.updateById(user);
        if (rows > 0) {
            return Result.success("更新成功", user);
        }
        return Result.error("更新失败");
    }

    @Override
    public Result changePassword(Integer userId, String oldPwd, String newPwd) {
        User user = userMapper.selectById(userId);
        if (user == null) return Result.error("用户不存在");
        if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
            return Result.error("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPwd));
        userMapper.updateById(user);
        return Result.success("密码修改成功");
    }

    @Override
    public User getById(Integer id) {
        return userMapper.selectById(id);
    }
}
