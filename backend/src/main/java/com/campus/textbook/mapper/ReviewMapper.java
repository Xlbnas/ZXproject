package com.campus.textbook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.textbook.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    /** 查询我发出的评价（论文5.1.5） */
    List<Review> selectByReviewerId(@Param("reviewerId") Integer reviewerId,
                                    @Param("offset") int offset,
                                    @Param("pageSize") int pageSize);

    int countByReviewerId(@Param("reviewerId") Integer reviewerId);

    /** 查询我收到的评价（论文5.1.5） */
    List<Review> selectByRevieweeId(@Param("revieweeId") Integer revieweeId,
                                    @Param("offset") int offset,
                                    @Param("pageSize") int pageSize);

    int countByRevieweeId(@Param("revieweeId") Integer revieweeId);
}
