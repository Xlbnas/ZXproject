package com.campus.textbook.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

/**
 * 管理员实体，对应t_admin表（论文表4-2）
 */
@Data
@TableName("t_admin")
public class Admin {
    @TableId(type = IdType.AUTO)
    private Integer id;         // 管理员编号
    private String username;    // 登录账号
    @JsonIgnore
    private String password;    // 密码（BCrypt加密）
    private String realName;    // 姓名
    private String phone;       // 联系电话
    private String email;       // 电子邮箱
    private Date regTime;       // 创建时间
}
