package com.campus.textbook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.textbook.entity.UserEco;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserEcoMapper extends BaseMapper<UserEco> {

    UserEco selectByUserId(@Param("userId") Integer userId);
}
