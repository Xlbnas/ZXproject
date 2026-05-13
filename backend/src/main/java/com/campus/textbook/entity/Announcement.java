package com.campus.textbook.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 公告信息实体，对应t_announcement表（论文表4-7）
 */
@Data
@TableName("t_announcement")
public class Announcement {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String title;
    private String content;
    @TableField("admin_id")
    private Integer publisherId;        // 发布管理员ID
    @TableField("create_time")
    private Date publishTime;           // 发布时间
    private Date updateTime;
}
