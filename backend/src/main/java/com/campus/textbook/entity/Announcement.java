package com.campus.textbook.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 公告信息实体，对应t_announcement表（论文表4-8）
 */
@Data
@TableName("t_announcement")
public class Announcement {
    @TableId(type = IdType.AUTO)
    private Integer id;          // 公告ID
    private String title;        // 标题
    private String content;      // 内容
    private Integer publisherId; // 发布者ID（管理员）
    private Date publishTime;    // 发布时间
    private Date updateTime;     // 修改时间
}
