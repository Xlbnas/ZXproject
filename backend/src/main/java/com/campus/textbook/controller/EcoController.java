package com.campus.textbook.controller;

import com.campus.textbook.common.Result;
import com.campus.textbook.service.EcoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 环保统计接口（论文5.1.6）
 */
@RestController
@RequestMapping("/api/eco")
public class EcoController {

    @Autowired
    private EcoService ecoService;

    /**
     * 全局环保统计（首页面板，TC-ECO-001、TC-ECO-004）
     * 公开接口，不需要登录
     */
    @GetMapping("/global")
    public Result globalStats() {
        return ecoService.getGlobalStats();
    }

    /**
     * 个人贡献统计（个人中心，TC-ECO-002）
     */
    @GetMapping("/personal")
    public Result personalStats(Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        return ecoService.getPersonalStats(userId);
    }
}
