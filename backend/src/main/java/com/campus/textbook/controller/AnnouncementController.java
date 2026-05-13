package com.campus.textbook.controller;

import com.campus.textbook.common.Result;
import com.campus.textbook.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 公告接口（普通用户查看公告）
 */
@RestController
@RequestMapping("/api/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /**
     * 获取公告列表（首页展示，公开接口）
     */
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "5") int pageSize) {
        return announcementService.listAnnouncements(page, pageSize);
    }
}
