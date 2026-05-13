package com.campus.textbook.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 全局环保统计实体，对应t_eco_stats表（论文表4-8）
 * 表中只有id=1的一行数据，全局统计
 */
@Data
@TableName("t_eco_stats")
public class EcoStats {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer totalBooks;         // 累计循环教材本数
    private BigDecimal totalSaved;      // 累计节省金额（元）
    private BigDecimal totalCo2;        // 累计减少碳排放（kg）
    private Date updateTime;            // 最后更新时间
}
