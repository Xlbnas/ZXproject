package com.campus.textbook.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 收藏信息实体，对应t_favorite表（论文表4-6）
 */
@Data
@TableName("t_favorite")
public class Favorite {
    @TableId(type = IdType.AUTO)
    private Integer id;      // 收藏ID
    private Integer userId;  // 用户ID
    private Integer bookId;  // 教材ID
    private Date favTime;    // 收藏时间
}
