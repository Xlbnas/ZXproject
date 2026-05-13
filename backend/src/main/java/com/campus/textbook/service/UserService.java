package com.campus.textbook.service;

import com.campus.textbook.common.Result;
import com.campus.textbook.entity.User;

import java.util.Map;

public interface UserService {

    /** 用户注册（论文5.1.1） */
    Result register(String username, String password, String realName,
                    String phone, String email, String major);

    /** 用户/管理员登录（论文5.1.1） */
    Result login(String username, String password);

    /** 修改个人信息（论文5.1.1） */
    Result updateProfile(Integer userId, User params);

    /** 修改密码 */
    Result changePassword(Integer userId, String oldPwd, String newPwd);

    /** 根据ID查询用户 */
    User getById(Integer id);
}
