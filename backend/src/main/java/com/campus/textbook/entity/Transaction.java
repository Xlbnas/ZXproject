package com.campus.textbook.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易/订单实体，对应t_transaction表（论文表4-4）
 * 交易状态：1-待确认 2-已确认 3-已完成 4-已取消 5-纠纷中
 */
@Data
@TableName("t_transaction")
public class Transaction {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField("textbook_id")
    private Integer bookId;             // 教材ID
    private Integer requesterId;        // 需求者ID（买家）
    private Integer ownerId;            // 持有者ID（卖家）
    @TableField("trans_price")
    private BigDecimal transPrice;      // 成交价格
    private Integer transStatus;        // 交易状态
    private String remark;              // 备注
    private Date confirmTime;           // 确认时间
    private Date completeTime;          // 完成时间
    @TableField("create_time")
    private Date transTime;             // 发起交易时间
}
