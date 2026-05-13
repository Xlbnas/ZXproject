package com.campus.textbook.service.impl;

import com.campus.textbook.common.PageResult;
import com.campus.textbook.common.Result;
import com.campus.textbook.entity.Textbook;
import com.campus.textbook.mapper.TextbookMapper;
import com.campus.textbook.service.TextbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class TextbookServiceImpl implements TextbookService {

    @Autowired
    private TextbookMapper textbookMapper;

    /**
     * 发布教材（论文5.1.2核心代码）
     * 校验ISBN格式，保存教材信息，初始审核状态为待审核
     */
    @Override
    public Result publishTextbook(Textbook request, Integer publisherId) {
        if (request == null || publisherId == null) {
            return Result.error("参数错误");
        }
        if (!StringUtils.hasText(request.getBookName())) {
            return Result.error("书名不能为空");
        }
        if (!StringUtils.hasText(request.getIsbn())) {
            return Result.error("ISBN号不能为空");
        }
        if (request.getIsbn().length() != 13) {
            return Result.error("ISBN号格式不正确");
        }

        request.setPublisherId(publisherId);
        request.setPublishTime(new Date());
        request.setAuditStatus(0);  // 待审核
        request.setStatus(0);       // 暂不上架，等待审核通过

        int rows = textbookMapper.insert(request);
        if (rows > 0) {
            return Result.success("发布成功，等待管理员审核");
        }
        return Result.error("发布失败，请稍后重试");
    }

    @Override
    public Result searchTextbooks(String keyword, String isbn,
                                  BigDecimal minPrice, BigDecimal maxPrice,
                                  int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;
        int offset = (page - 1) * pageSize;
        List<Textbook> list = textbookMapper.searchTextbooks(keyword, isbn, minPrice, maxPrice, offset, pageSize);
        int total = textbookMapper.countSearchTextbooks(keyword, isbn, minPrice, maxPrice);
        if (list.isEmpty()) {
            return Result.success("暂无相关教材", new PageResult(list, total, page, pageSize));
        }
        return Result.success(new PageResult(list, total, page, pageSize));
    }

    @Override
    public Result getDetail(Integer bookId) {
        Textbook book = textbookMapper.selectById(bookId);
        if (book == null) {
            return Result.error("教材不存在");
        }
        return Result.success(book);
    }

    @Override
    public Result offShelfByUser(Integer bookId, Integer userId) {
        Textbook book = textbookMapper.selectById(bookId);
        if (book == null) return Result.error("教材不存在");
        if (!book.getPublisherId().equals(userId)) {
            return Result.error("只能下架自己发布的教材");
        }
        book.setStatus(0);
        textbookMapper.updateById(book);
        return Result.success("下架成功");
    }

    @Override
    public Result getMyPublished(Integer userId) {
        List<Textbook> list = textbookMapper.selectByPublisherId(userId);
        return Result.success(list);
    }

    /**
     * 管理员审核教材（论文5.2.2核心代码）
     * status=1通过，status=2驳回
     */
    @Override
    public Result auditTextbook(Integer bookId, Integer auditStatus, String rejectReason) {
        Textbook book = textbookMapper.selectById(bookId);
        if (book == null) return Result.error("教材不存在");
        if (book.getAuditStatus() != 0) return Result.error("该教材已审核，请勿重复操作");

        book.setAuditStatus(auditStatus);
        if (auditStatus == 2 && StringUtils.hasText(rejectReason)) {
            book.setRejectReason(rejectReason);
        }
        if (auditStatus == 1) {
            book.setStatus(1);  // 审核通过后上架
        }
        textbookMapper.updateById(book);
        return Result.success(auditStatus == 1 ? "审核通过" : "已驳回");
    }

    /**
     * 管理员强制下架（论文5.2.2核心代码）
     */
    @Override
    public Result offShelfByAdmin(Integer bookId) {
        Textbook book = textbookMapper.selectById(bookId);
        if (book == null) return Result.error("教材不存在");
        if (book.getAuditStatus() != 1) return Result.error("只有已上架的教材才能下架");
        book.setStatus(0);
        textbookMapper.updateById(book);
        return Result.success("下架成功");
    }

    @Override
    public Result listForAdmin(Integer auditStatus, String keyword, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;
        int offset = (page - 1) * pageSize;
        List<Textbook> list = textbookMapper.selectForAdmin(auditStatus, keyword, offset, pageSize);
        int total = textbookMapper.countForAdmin(auditStatus, keyword);
        return Result.success(new PageResult(list, total, page, pageSize));
    }
}
