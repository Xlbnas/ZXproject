package com.campus.textbook.service;

import com.campus.textbook.common.Result;

public interface EcoService {

    /** 获取全局环保统计（论文5.1.6） */
    Result getGlobalStats();

    /** 获取用户个人贡献（论文TC-ECO-002） */
    Result getPersonalStats(Integer userId);
}
