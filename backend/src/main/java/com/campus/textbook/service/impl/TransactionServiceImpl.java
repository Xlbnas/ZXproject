package com.campus.textbook.service.impl;

import com.campus.textbook.common.PageResult;
import com.campus.textbook.common.Result;
import com.campus.textbook.entity.*;
import com.campus.textbook.mapper.*;
import com.campus.textbook.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionMapper transactionMapper;
    @Autowired
    private TextbookMapper textbookMapper;
    @Autowired
    private EcoStatsMapper ecoStatsMapper;
    @Autowired
    private UserEcoMapper userEcoMapper;

    /**
     * 发起交易（论文5.1.3核心代码）
     * 校验教材状态、不能买自己的教材、不能重复购买
     */
    @Override
    public Result createTransaction(Integer bookId, Integer requesterId) {
        Textbook book = textbookMapper.selectById(bookId);
        if (book == null) return Result.error("教材不存在");
        if (book.getStatus() != 1) return Result.error("教材不可购买");
        if (book.getPublisherId().equals(requesterId)) return Result.error("不能购买自己的教材");

        // 检查是否已经发起过未完成的交易（TC-TRADE-004）
        int activeCount = transactionMapper.countActiveByBookAndRequester(bookId, requesterId);
        if (activeCount > 0) {
            return Result.error("您已购买过该教材");
        }

        Transaction trans = new Transaction();
        trans.setBookId(bookId);
        trans.setRequesterId(requesterId);
        trans.setOwnerId(book.getPublisherId());
        trans.setTransTime(new Date());
        trans.setTransStatus(1);  // 1-待确认

        int rows = transactionMapper.insert(trans);
        return rows > 0 ? Result.success("交易创建成功", trans) : Result.error("创建失败");
    }

    /**
     * 持有者确认交易（论文5.1.3）
     * 只有持有者才能确认，状态从1变为2
     */
    @Override
    public Result confirmTransaction(Integer transId, Integer userId) {
        Transaction trans = transactionMapper.selectById(transId);
        if (trans == null) return Result.error("交易不存在");
        if (!trans.getOwnerId().equals(userId)) {
            return Result.error("只有教材持有者才能确认交易");
        }
        if (trans.getTransStatus() != 1) return Result.error("当前状态不可确认");
        trans.setTransStatus(2);  // 已确认
        trans.setConfirmTime(new Date());
        transactionMapper.updateById(trans);
        return Result.success("确认成功");
    }

    /**
     * 我的订单列表（论文5.1.4核心代码）
     */
    @Override
    public Result getMyOrders(Integer userId, Integer status, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;
        int offset = (page - 1) * pageSize;
        List<Transaction> list = transactionMapper.selectByUserAndStatus(userId, status, offset, pageSize);
        int total = transactionMapper.countByUserAndStatus(userId, status);
        return Result.success(new PageResult(list, total, page, pageSize));
    }

    /**
     * 取消订单（论文5.1.4核心代码）
     * 只有待确认状态可以取消
     */
    @Override
    public Result cancelOrder(Integer transId, Integer userId) {
        Transaction trans = transactionMapper.selectById(transId);
        if (trans == null) return Result.error("订单不存在");
        if (!trans.getRequesterId().equals(userId) && !trans.getOwnerId().equals(userId)) {
            return Result.error("无权操作");
        }
        if (trans.getTransStatus() != 1) {
            return Result.error("当前状态不可取消");
        }
        trans.setTransStatus(4);  // 已取消
        trans.setCompleteTime(new Date());
        transactionMapper.updateById(trans);
        return Result.success("取消成功");
    }

    /**
     * 确认完成（论文5.1.4核心代码）
     * 只有已确认状态才能确认完成，完成后更新环保统计
     */
    @Override
    @Transactional
    public Result confirmComplete(Integer transId, Integer userId) {
        Transaction trans = transactionMapper.selectById(transId);
        if (trans == null) return Result.error("订单不存在");
        if (!trans.getRequesterId().equals(userId) && !trans.getOwnerId().equals(userId)) {
            return Result.error("无权操作");
        }
        if (trans.getTransStatus() != 2) {
            return Result.error("当前状态不可确认完成");
        }
        trans.setTransStatus(3);  // 已完成
        trans.setCompleteTime(new Date());
        transactionMapper.updateById(trans);

        // 完成后更新环保统计（论文5.1.6）
        updateEcoStats(transId);

        return Result.success("确认完成");
    }

    /**
     * 交易完成后更新环保统计（论文5.1.6核心代码）
     * 节省金额 = 原价 - 期望价格（最低0）
     * 减碳量 = 0.18 kg（行业标准）
     */
    private void updateEcoStats(Integer transactionId) {
        Transaction trans = transactionMapper.selectById(transactionId);
        if (trans == null || trans.getTransStatus() != 3) return;
        Textbook book = textbookMapper.selectById(trans.getBookId());
        if (book == null) return;

        // 计算节省金额
        BigDecimal originalPrice = book.getOriginalPrice();
        if (originalPrice == null) originalPrice = new BigDecimal("35");  // 默认新书均价35元
        BigDecimal dealPrice = book.getExpectedPrice();
        if (dealPrice == null) dealPrice = BigDecimal.ZERO;
        BigDecimal saved = originalPrice.subtract(dealPrice);
        if (saved.compareTo(BigDecimal.ZERO) < 0) saved = BigDecimal.ZERO;

        BigDecimal co2 = new BigDecimal("0.18");  // 每本减碳0.18kg

        // 更新全局统计（id=1的那行）
        EcoStats stats = ecoStatsMapper.selectById(1);
        if (stats != null) {
            stats.setTotalTransactions(stats.getTotalTransactions() + 1);
            stats.setTotalSavedAmount(stats.getTotalSavedAmount().add(saved));
            stats.setTotalCo2Reduction(stats.getTotalCo2Reduction().add(co2));
            stats.setLastUpdateTime(new Date());
            ecoStatsMapper.updateById(stats);
        }

        // 更新卖家（持有者）的个人贡献
        UserEco userEco = userEcoMapper.selectByUserId(trans.getOwnerId());
        if (userEco != null) {
            userEco.setSellCount(userEco.getSellCount() + 1);
            userEco.setSavedAmount(userEco.getSavedAmount().add(saved));
            userEco.setCo2Reduction(userEco.getCo2Reduction().add(co2));
            userEcoMapper.updateById(userEco);
        }
    }

    @Override
    public Result listAllTransactions(Integer status, String keyword, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;
        int offset = (page - 1) * pageSize;
        List<Transaction> list = transactionMapper.selectByCondition(status, keyword, offset, pageSize);
        int total = transactionMapper.countByCondition(status, keyword);
        return Result.success(new PageResult(list, total, page, pageSize));
    }

    /**
     * 管理员强制取消（论文5.2.3核心代码）
     */
    @Override
    public Result cancelByAdmin(Integer transId, String reason) {
        Transaction trans = transactionMapper.selectById(transId);
        if (trans == null) return Result.error("交易不存在");
        trans.setTransStatus(4);  // 已取消
        if (StringUtils.hasText(reason)) {
            trans.setRemark(reason);
        }
        transactionMapper.updateById(trans);
        return Result.success("取消成功");
    }

    /**
     * 管理员标记纠纷（论文5.2.3核心代码）
     */
    @Override
    public Result markDispute(Integer transId) {
        Transaction trans = transactionMapper.selectById(transId);
        if (trans == null) return Result.error("交易不存在");
        trans.setTransStatus(5);  // 纠纷中
        transactionMapper.updateById(trans);
        return Result.success("已标记异常");
    }
}
