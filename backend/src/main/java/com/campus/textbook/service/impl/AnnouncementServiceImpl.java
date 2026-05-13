package com.campus.textbook.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.textbook.common.PageResult;
import com.campus.textbook.common.Result;
import com.campus.textbook.entity.Announcement;
import com.campus.textbook.mapper.AnnouncementMapper;
import com.campus.textbook.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public Result listAnnouncements(int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;
        // MyBatis-Plus分页（简单实现，不引入分页插件）
        QueryWrapper<Announcement> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        List<Announcement> list = announcementMapper.selectList(wrapper);
        long total = list.size();
        // 手动分页
        int offset = (page - 1) * pageSize;
        int end = Math.min(offset + pageSize, list.size());
        List<Announcement> paged = offset >= list.size() ? List.of() : list.subList(offset, end);
        return Result.success(new PageResult(paged, total, page, pageSize));
    }

    /**
     * 发布公告（论文5.2.4核心代码）
     */
    @Override
    public Result publish(String title, String content, Integer adminId) {
        if (!StringUtils.hasText(title) || !StringUtils.hasText(content)) {
            return Result.error("标题和内容不能为空");
        }
        Announcement ann = new Announcement();
        ann.setTitle(title);
        ann.setContent(content);
        ann.setPublisherId(adminId);
        ann.setPublishTime(new Date());
        ann.setUpdateTime(new Date());
        int rows = announcementMapper.insert(ann);
        return rows > 0 ? Result.success("发布成功") : Result.error("发布失败");
    }

    /**
     * 编辑公告（论文5.2.4核心代码）
     */
    @Override
    public Result update(Integer id, String title, String content) {
        Announcement ann = announcementMapper.selectById(id);
        if (ann == null) return Result.error("公告不存在");
        if (StringUtils.hasText(title)) ann.setTitle(title);
        if (StringUtils.hasText(content)) ann.setContent(content);
        ann.setUpdateTime(new Date());
        int rows = announcementMapper.updateById(ann);
        return rows > 0 ? Result.success("修改成功") : Result.error("修改失败");
    }

    /**
     * 删除公告（论文5.2.4核心代码）
     */
    @Override
    public Result delete(Integer id) {
        int rows = announcementMapper.deleteById(id);
        return rows > 0 ? Result.success("删除成功") : Result.error("删除失败");
    }
}
