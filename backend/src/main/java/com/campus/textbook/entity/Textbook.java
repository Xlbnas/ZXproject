package com.campus.textbook.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 教材实体，对应t_textbook表（论文表4-3）
 */
@Data
@TableName("t_textbook")
public class Textbook {
    @TableId(type = IdType.AUTO)
    private Integer id;               // 教材编号
    private String isbn;              // ISBN号（13位）
    private String bookName;          // 书名
    private String author;            // 作者
    private String publisher;         // 出版社
    private String edition;           // 版次
    private String subject;           // 科目/专业
    private String description;       // 教材描述
    private BigDecimal expectedPrice; // 出售/期望价格
    private String imageUrl;          // 图片展示URL
    private BigDecimal originalPrice; // 原价
    // 扩展字段（论文5.1.2中使用）
    private Integer publisherId;      // 发布者用户ID
    private Date publishTime;         // 发布时间
    private String conditionDesc;     // 品相：全新/良好/一般/有笔记
    private String transType;         // 交易方式：赠送/低价转让
    private Integer auditStatus;      // 审核状态：0-待审核 1-通过 2-驳回
    private Integer status;           // 上架状态：0-下架 1-上架
    private String rejectReason;      // 驳回原因
}
