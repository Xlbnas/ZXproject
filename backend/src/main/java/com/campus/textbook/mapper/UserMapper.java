package com.campus.textbook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.textbook.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /** 根据用户名查询用户 */
    User selectByUsername(@Param("username") String username);

    /** 查询用户列表（支持关键词搜索） */
    List<User> selectUserList(@Param("keyword") String keyword,
                              @Param("offset") int offset,
                              @Param("pageSize") int pageSize);

    /** 统计用户数量 */
    int countUserList(@Param("keyword") String keyword);
}
