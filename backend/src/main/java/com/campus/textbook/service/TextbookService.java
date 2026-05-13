package com.campus.textbook.service;

import com.campus.textbook.common.Result;
import com.campus.textbook.entity.Textbook;

import java.math.BigDecimal;

public interface TextbookService {

    /** 发布教材（论文5.1.2） */
    Result publishTextbook(Textbook request, Integer publisherId);

    /** 多条件检索教材（论文TC-SEARCH系列） */
    Result searchTextbooks(String keyword, String isbn,
                           BigDecimal minPrice, BigDecimal maxPrice,
                           int page, int pageSize);

    /** 查询单本教材详情 */
    Result getDetail(Integer bookId);

    /** 用户下架自己的教材 */
    Result offShelfByUser(Integer bookId, Integer userId);

    /** 查询我发布的教材 */
    Result getMyPublished(Integer userId);

    /** 管理员审核教材（论文5.2.2） */
    Result auditTextbook(Integer bookId, Integer auditStatus, String rejectReason);

    /** 管理员强制下架（论文5.2.2） */
    Result offShelfByAdmin(Integer bookId);

    /** 管理员查询教材列表（论文5.2.2） */
    Result listForAdmin(Integer auditStatus, String keyword, int page, int pageSize);
}
