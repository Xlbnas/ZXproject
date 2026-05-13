package com.campus.textbook.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户个人环保贡献统计，对应t_user_eco表（论文表4-9）
 */
@Data
@TableName("t_user_eco")
public class UserEco {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;           // 用户ID
    private Integer totalBooks;       // 参与循环本数
    private BigDecimal totalSaved;    // 节省金额（元）
    private BigDecimal totalCo2;      // 减排量（kg）
}
