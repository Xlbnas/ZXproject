package com.campus.textbook.controller;

import com.campus.textbook.common.Result;
import com.campus.textbook.entity.User;
import com.campus.textbook.mapper.UserMapper;
import com.campus.textbook.service.AnnouncementService;
import com.campus.textbook.service.TextbookService;
import com.campus.textbook.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员功能接口（论文5.2章，需要ADMIN角色）
 * 对应TC-ADMIN系列测试用例
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TextbookService textbookService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AnnouncementService announcementService;

    // ==================== 用户管理（论文5.2.1） ====================

    /**
     * 查询用户列表（TC-ADMIN-001）
     */
    @GetMapping("/user/list")
    public Result userList(@RequestParam(required = false) String keyword,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int pageSize) {
        int offset = (page - 1) * pageSize;
        java.util.List<User> list = userMapper.selectUserList(keyword, offset, pageSize);
        int total = userMapper.countUserList(keyword);
        return Result.success(new com.campus.textbook.common.PageResult(list, total, page, pageSize));
    }

    /**
     * 修改用户权限（TC-ADMIN-002：提升为管理员）
     * userType=1普通用户，userType=2管理员
     */
    @PutMapping("/user/type")
    public Result updateUserType(@RequestBody Map<String, Object> params) {
        Integer userId = (Integer) params.get("userId");
        Integer userType = (Integer) params.get("userType");
        if (userId == null || userType == null) {
            return Result.error("参数错误");
        }
        User user = userMapper.selectById(userId);
        if (user == null) return Result.error("用户不存在");
        user.setUserType(userType);
        userMapper.updateById(user);
        return Result.success("更新成功");
    }

    /**
     * 禁用/启用用户（TC-ADMIN-003）
     * status=0禁用，status=1正常
     */
    @PutMapping("/user/status")
    public Result updateUserStatus(@RequestBody Map<String, Object> params) {
        Integer userId = (Integer) params.get("userId");
        Integer status = (Integer) params.get("status");
        if (userId == null || status == null) {
            return Result.error("参数错误");
        }
        User user = userMapper.selectById(userId);
        if (user == null) return Result.error("用户不存在");
        user.setStatus(status);
        userMapper.updateById(user);
        return Result.success(status == 0 ? "禁用成功" : "启用成功");
    }

    // ==================== 教材管理（论文5.2.2） ====================

    /**
     * 管理员查询教材列表（含待审核）
     */
    @GetMapping("/textbook/list")
    public Result textbookList(@RequestParam(required = false) Integer auditStatus,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int pageSize) {
        return textbookService.listForAdmin(auditStatus, keyword, page, pageSize);
    }

    /**
     * 审核教材（TC-ADMIN-004通过、TC-ADMIN-005驳回）
     */
    @PutMapping("/textbook/audit")
    public Result auditTextbook(@RequestBody Map<String, Object> params) {
        Integer bookId = (Integer) params.get("bookId");
        Integer auditStatus = (Integer) params.get("auditStatus");
        String rejectReason = (String) params.get("rejectReason");
        return textbookService.auditTextbook(bookId, auditStatus, rejectReason);
    }

    /**
     * 强制下架教材（TC-ADMIN-006）
     */
    @PutMapping("/textbook/offshelf/{id}")
    public Result offShelf(@PathVariable Integer id) {
        return textbookService.offShelfByAdmin(id);
    }

    // ==================== 交易监控（论文5.2.3） ====================

    /**
     * 查看所有交易（TC-ADMIN-007）
     */
    @GetMapping("/transaction/list")
    public Result transactionList(@RequestParam(required = false) Integer status,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int pageSize) {
        return transactionService.listAllTransactions(status, keyword, page, pageSize);
    }

    /**
     * 管理员强制取消交易
     */
    @PutMapping("/transaction/cancel/{id}")
    public Result cancelTransaction(@PathVariable Integer id,
                                    @RequestBody(required = false) Map<String, String> params) {
        String reason = params != null ? params.get("reason") : null;
        return transactionService.cancelByAdmin(id, reason);
    }

    /**
     * 标记交易纠纷
     */
    @PutMapping("/transaction/dispute/{id}")
    public Result markDispute(@PathVariable Integer id) {
        return transactionService.markDispute(id);
    }

    // ==================== 公告管理（论文5.2.4） ====================

    /**
     * 管理员查询公告列表
     */
    @GetMapping("/announcement/list")
    public Result announcementList(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int pageSize) {
        return announcementService.listAnnouncements(page, pageSize);
    }

    /**
     * 发布公告（TC-ADMIN-008）
     */
    @PostMapping("/announcement/publish")
    public Result publishAnnouncement(@RequestBody Map<String, String> params, Authentication auth) {
        Integer adminId = (Integer) auth.getPrincipal();
        String title = params.get("title");
        String content = params.get("content");
        return announcementService.publish(title, content, adminId);
    }

    /**
     * 编辑公告
     */
    @PutMapping("/announcement/update/{id}")
    public Result updateAnnouncement(@PathVariable Integer id,
                                     @RequestBody Map<String, String> params) {
        String title = params.get("title");
        String content = params.get("content");
        return announcementService.update(id, title, content);
    }

    /**
     * 删除公告（TC-ADMIN-009）
     */
    @DeleteMapping("/announcement/delete/{id}")
    public Result deleteAnnouncement(@PathVariable Integer id) {
        return announcementService.delete(id);
    }
}
