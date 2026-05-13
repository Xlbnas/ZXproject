package com.campus.textbook.service.impl;

import com.campus.textbook.common.PageResult;
import com.campus.textbook.common.Result;
import com.campus.textbook.entity.Review;
import com.campus.textbook.mapper.ReviewMapper;
import com.campus.textbook.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public Result addReview(Integer transId, Integer reviewerId, Integer revieweeId,
                            Integer reviewType, String content, Integer rating) {
        Review review = new Review();
        review.setTransId(transId);
        review.setReviewerId(reviewerId);
        review.setRevieweeId(revieweeId);
        review.setContent(content);
        review.setRating(rating != null ? rating : 5);
        review.setReviewTime(new Date());
        reviewMapper.insert(review);
        return Result.success("评价成功");
    }

    /**
     * 查询我发出的评价（论文5.1.5核心代码）
     */
    @Override
    public Result getMyReviews(Integer userId, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;
        int offset = (page - 1) * pageSize;
        List<Review> list = reviewMapper.selectByReviewerId(userId, offset, pageSize);
        int total = reviewMapper.countByReviewerId(userId);
        return Result.success(new PageResult(list, total, page, pageSize));
    }

    /**
     * 查询我收到的评价（论文5.1.5核心代码）
     */
    @Override
    public Result getReceivedReviews(Integer userId, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;
        int offset = (page - 1) * pageSize;
        List<Review> list = reviewMapper.selectByRevieweeId(userId, offset, pageSize);
        int total = reviewMapper.countByRevieweeId(userId);
        return Result.success(new PageResult(list, total, page, pageSize));
    }

    /**
     * 修改评价（论文5.1.5核心代码）
     */
    @Override
    public Result updateReview(Integer reviewId, Integer userId, Integer rating, String content) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) return Result.error("评价不存在");
        if (!review.getReviewerId().equals(userId)) return Result.error("无权修改");
        review.setRating(rating);
        review.setContent(content);
        reviewMapper.updateById(review);
        return Result.success("修改成功");
    }

    /**
     * 删除评价（论文5.1.5核心代码）
     */
    @Override
    public Result deleteReview(Integer reviewId, Integer userId) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) return Result.error("评价不存在");
        if (!review.getReviewerId().equals(userId)) return Result.error("无权删除");
        reviewMapper.deleteById(reviewId);
        return Result.success("删除成功");
    }
}
