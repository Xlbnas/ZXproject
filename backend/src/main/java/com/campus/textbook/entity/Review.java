package com.campus.textbook.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 评价信息实体，对应t_review表（论文表4-6）
 */
@Data
@TableName("t_review")
public class Review {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("transaction_id")
    private Integer transId;            // 交易ID
    private Integer reviewerId;         // 评价者ID
    @TableField("reviewed_id")
    private Integer revieweeId;         // 被评价者ID
    private Integer rating;             // 评分：1-5分
    private String content;             // 评价内容
    @TableField("create_time")
    private Date reviewTime;            // 评价时间
    private Date updateTime;            // 修改时间
}
