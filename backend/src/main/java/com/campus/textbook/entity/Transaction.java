package com.campus.textbook.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 交易/订单实体，对应t_transaction表（论文表4-4和表4-5合并）
 * 交易状态：1-待确认 2-已确认 3-已完成 4-已取消 5-纠纷中
 */
@Data
@TableName("t_transaction")
public class Transaction {
    @TableId(type = IdType.AUTO)
    private Integer id;           // 交易编号
    private Integer bookId;       // 教材ID
    private Integer requesterId;  // 需求者ID（买家）
    private Integer ownerId;      // 持有者ID（卖家）
    private Date transTime;       // 发起交易时间（下单时间）
    private Integer transStatus;  // 交易状态：1-待确认 2-已确认 3-已完成 4-已取消 5-纠纷中
    private Date confirmTime;     // 确认订单时间
    private Date completeTime;    // 交易完成时间
    private String remark;        // 备注/取消原因
}
