package com.campus.textbook.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 全局环保统计实体，对应t_eco_stats表（论文表4-9）
 * 表中只有id=1的一行数据，全局统计
 */
@Data
@TableName("t_eco_stats")
public class EcoStats {
    @TableId(type = IdType.AUTO)
    private Integer id;                    // 统计ID
    private Integer totalTransactions;     // 累计交易本数
    private BigDecimal totalSavedAmount;   // 累计节省金额
    private BigDecimal totalCo2Reduction;  // 累计减碳量(kg)
    private Date lastUpdateTime;           // 最后更新时间
}
