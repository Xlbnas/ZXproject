package com.campus.textbook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.textbook.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
    // MyBatis-Plus BaseMapper已提供基础CRUD，公告功能不需要额外自定义SQL
}
