package com.campus.textbook.service;

import com.campus.textbook.common.Result;

public interface ReviewService {

    /** 发布评价 */
    Result addReview(Integer transId, Integer reviewerId, Integer revieweeId,
                     Integer reviewType, String content, Integer rating);

    /** 查询我发出的评价（论文5.1.5） */
    Result getMyReviews(Integer userId, int page, int pageSize);

    /** 查询我收到的评价（论文5.1.5） */
    Result getReceivedReviews(Integer userId, int page, int pageSize);

    /** 修改评价（论文5.1.5） */
    Result updateReview(Integer reviewId, Integer userId, Integer rating, String content);

    /** 删除评价（论文5.1.5） */
    Result deleteReview(Integer reviewId, Integer userId);
}
