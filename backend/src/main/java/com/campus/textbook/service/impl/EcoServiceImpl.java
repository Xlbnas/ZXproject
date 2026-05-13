package com.campus.textbook.service.impl;

import com.campus.textbook.common.Result;
import com.campus.textbook.entity.EcoStats;
import com.campus.textbook.entity.UserEco;
import com.campus.textbook.mapper.EcoStatsMapper;
import com.campus.textbook.mapper.UserEcoMapper;
import com.campus.textbook.service.EcoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class EcoServiceImpl implements EcoService {

    @Autowired
    private EcoStatsMapper ecoStatsMapper;
    @Autowired
    private UserEcoMapper userEcoMapper;

    /**
     * 获取全局环保统计（论文5.1.6核心代码）
     */
    @Override
    public Result getGlobalStats() {
        EcoStats stats = ecoStatsMapper.selectById(1);
        Map<String, Object> result = new HashMap<>();
        if (stats != null) {
            result.put("totalBooks", stats.getTotalBooks());
            result.put("totalSaved", stats.getTotalSaved());
            result.put("totalCO2", stats.getTotalCo2());
        } else {
            result.put("totalBooks", 0);
            result.put("totalSaved", new BigDecimal("0.00"));
            result.put("totalCO2", new BigDecimal("0.00"));
        }
        return Result.success(result);
    }

    /**
     * 获取用户个人贡献（TC-ECO-002）
     */
    @Override
    public Result getPersonalStats(Integer userId) {
        UserEco eco = userEcoMapper.selectByUserId(userId);
        Map<String, Object> result = new HashMap<>();
        if (eco != null) {
            result.put("totalBooks", eco.getTotalBooks());
            result.put("totalSaved", eco.getTotalSaved());
            result.put("totalCO2", eco.getTotalCo2());
        } else {
            result.put("totalBooks", 0);
            result.put("totalSaved", new BigDecimal("0.00"));
            result.put("totalCO2", new BigDecimal("0.00"));
        }
        return Result.success(result);
    }
}
