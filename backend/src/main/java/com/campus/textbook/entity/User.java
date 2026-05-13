package com.campus.textbook.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
    private Integer id;
    private String username;
    @JsonIgnore
    private String password;
    private String realName;
    private String studentId;     // 学号
    private String phone;
    private String email;
    private String avatar;
    private String major;         // 专业
    private Integer userType;     // 1-普通用户 2-管理员
    private Integer status;       // 0-禁用 1-正常
    @TableField("create_time")
    private Date regTime;         // 注册时间
}
