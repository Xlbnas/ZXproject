package com.campus.textbook.controller;

import com.campus.textbook.common.Result;
import com.campus.textbook.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 收藏相关接口
 */
@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /** 收藏/取消收藏（切换） */
    @PostMapping("/toggle")
    public Result toggle(@RequestBody Map<String, Integer> params, Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        Integer bookId = params.get("bookId");
        return favoriteService.toggleFavorite(userId, bookId);
    }

    /** 我的收藏列表 */
    @GetMapping("/my")
    public Result myFavorites(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int pageSize,
                              Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        return favoriteService.getMyFavorites(userId, page, pageSize);
    }

    /** 检查是否已收藏 */
    @GetMapping("/check")
    public Result check(@RequestParam Integer bookId, Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        return favoriteService.isFavorited(userId, bookId);
    }
}
