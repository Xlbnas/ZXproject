package com.campus.textbook.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

/**
 * 用户实体，对应t_user表（论文表4-1）
 */
@Data
@TableName("t_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;         // 用户编号
    private String realName;    // 姓名
    private String username;    // 账号（登录用）
    @JsonIgnore
    private String password;    // 密码（BCrypt加密，返回给前端时不显示）
    private String major;       // 专业
    private Date regTime;       // 注册时间
    private String phone;       // 手机号
    private String email;       // 邮箱
    private Integer userType;   // 1-普通用户 2-管理员
    private Integer status;     // 0-禁用 1-正常
}
