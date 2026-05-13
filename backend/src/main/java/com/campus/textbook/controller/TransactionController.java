package com.campus.textbook.controller;

import com.campus.textbook.common.Result;
import com.campus.textbook.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 交易/订单相关接口（论文5.1.3、5.1.4）
 */
@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * 发起交易（TC-TRADE-001~004）
     */
    @PostMapping("/create")
    public Result create(@RequestBody Map<String, Integer> params, Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        Integer bookId = params.get("bookId");
        return transactionService.createTransaction(bookId, userId);
    }

    /**
     * 持有者确认交易
     */
    @PutMapping("/confirm/{id}")
    public Result confirm(@PathVariable Integer id, Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        return transactionService.confirmTransaction(id, userId);
    }

    /**
     * 我的订单列表（TC-ORDER-001~002）
     */
    @GetMapping("/my")
    public Result myOrders(@RequestParam(required = false) Integer status,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int pageSize,
                           Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        return transactionService.getMyOrders(userId, status, page, pageSize);
    }

    /**
     * 取消订单（TC-ORDER-003~004）
     */
    @PutMapping("/cancel/{id}")
    public Result cancel(@PathVariable Integer id, Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        return transactionService.cancelOrder(id, userId);
    }

    /**
     * 确认完成（TC-ORDER-005~006），触发环保统计更新
     */
    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable Integer id, Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        return transactionService.confirmComplete(id, userId);
    }
}
