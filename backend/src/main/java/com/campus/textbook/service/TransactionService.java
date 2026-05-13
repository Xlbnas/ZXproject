package com.campus.textbook.service;

import com.campus.textbook.common.Result;

public interface TransactionService {

    /** 发起交易（论文5.1.3） */
    Result createTransaction(Integer bookId, Integer requesterId);

    /** 持有者确认交易（论文5.1.3） */
    Result confirmTransaction(Integer transId, Integer userId);

    /** 我的订单列表（论文5.1.4） */
    Result getMyOrders(Integer userId, Integer status, int page, int pageSize);

    /** 取消订单（论文5.1.4） */
    Result cancelOrder(Integer transId, Integer userId);

    /** 确认完成（论文5.1.4），并触发环保统计更新 */
    Result confirmComplete(Integer transId, Integer userId);

    /** 管理员查看所有交易（论文5.2.3） */
    Result listAllTransactions(Integer status, String keyword, int page, int pageSize);

    /** 管理员强制取消（论文5.2.3） */
    Result cancelByAdmin(Integer transId, String reason);

    /** 管理员标记纠纷（论文5.2.3） */
    Result markDispute(Integer transId);
}
