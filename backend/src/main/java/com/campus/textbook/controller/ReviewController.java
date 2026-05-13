package com.campus.textbook.controller;

import com.campus.textbook.common.Result;
import com.campus.textbook.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 评价相关接口（论文5.1.5）
 */
@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 发布评价
     */
    @PostMapping("/add")
    public Result add(@RequestBody Map<String, Object> params, Authentication auth) {
        Integer reviewerId = (Integer) auth.getPrincipal();
        Integer transId   = (Integer) params.get("transId");
        Integer revieweeId = (Integer) params.get("revieweeId");
        Integer reviewType = (Integer) params.get("reviewType");
        String content    = (String) params.get("content");
        Integer rating    = (Integer) params.get("rating");
        return reviewService.addReview(transId, reviewerId, revieweeId, reviewType, content, rating);
    }

    /**
     * 查询我发出的评价（论文5.1.5）
     */
    @GetMapping("/my-sent")
    public Result mySent(@RequestParam(defaultValue = "1") int page,
                         @RequestParam(defaultValue = "10") int pageSize,
                         Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        return reviewService.getMyReviews(userId, page, pageSize);
    }

    /**
     * 查询我收到的评价（论文5.1.5）
     */
    @GetMapping("/my-received")
    public Result myReceived(@RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "10") int pageSize,
                             Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        return reviewService.getReceivedReviews(userId, page, pageSize);
    }

    /**
     * 修改评价（论文5.1.5）
     */
    @PutMapping("/update/{id}")
    public Result update(@PathVariable Integer id,
                         @RequestBody Map<String, Object> params,
                         Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        Integer rating = (Integer) params.get("rating");
        String content = (String) params.get("content");
        return reviewService.updateReview(id, userId, rating, content);
    }

    /**
     * 删除评价（论文5.1.5）
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id, Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        return reviewService.deleteReview(id, userId);
    }
}
