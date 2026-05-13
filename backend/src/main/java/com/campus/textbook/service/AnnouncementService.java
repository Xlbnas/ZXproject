package com.campus.textbook.service;

import com.campus.textbook.common.Result;

public interface AnnouncementService {

    /** 查询公告列表（按时间倒序） */
    Result listAnnouncements(int page, int pageSize);

    /** 发布公告（论文5.2.4） */
    Result publish(String title, String content, Integer adminId);

    /** 编辑公告（论文5.2.4） */
    Result update(Integer id, String title, String content);

    /** 删除公告（论文5.2.4） */
    Result delete(Integer id);
}
