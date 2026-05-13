package com.campus.textbook.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户个人环保贡献统计，对应t_user_eco表
 */
@Data
@TableName("t_user_eco")
public class UserEco {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;              // 用户ID
    private Integer sellCount;           // 卖出教材数量
    private BigDecimal savedAmount;      // 帮助他人节省金额
    private BigDecimal co2Reduction;     // 个人减碳量(kg)
}
