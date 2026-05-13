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
            result.put("totalBooks", stats.getTotalTransactions());
            result.put("totalSaved", stats.getTotalSavedAmount());
            result.put("totalCO2", stats.getTotalCo2Reduction());
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
            result.put("sellCount", eco.getSellCount());
            result.put("savedAmount", eco.getSavedAmount());
            result.put("co2Reduction", eco.getCo2Reduction());
        } else {
            result.put("sellCount", 0);
            result.put("savedAmount", new BigDecimal("0.00"));
            result.put("co2Reduction", new BigDecimal("0.00"));
        }
        return Result.success(result);
    }
}
