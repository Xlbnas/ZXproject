package com.campus.textbook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.textbook.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {

    /** 查询用户收藏的教材ID列表 */
    List<Favorite> selectByUserId(@Param("userId") Integer userId,
                                  @Param("offset") int offset,
                                  @Param("pageSize") int pageSize);

    int countByUserId(@Param("userId") Integer userId);

    /** 查询某用户是否收藏了某本教材 */
    Favorite selectByUserAndBook(@Param("userId") Integer userId,
                                  @Param("bookId") Integer bookId);
}
