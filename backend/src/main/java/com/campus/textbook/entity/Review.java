package com.campus.textbook.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 评价信息实体，对应t_review表（论文表4-7）
 */
@Data
@TableName("t_review")
public class Review {
    @TableId(type = IdType.AUTO)
    private Integer id;           // 评价ID
    private Integer transId;      // 交易ID
    private Integer reviewerId;   // 评价者ID
    private Integer revieweeId;   // 被评价者ID
    private Integer reviewType;   // 评价类型：1-对用户 2-对教材
    private String content;       // 评价内容
    private Integer rating;       // 评分：1-5分
    private Date reviewTime;      // 评价时间
}
