package com.campus.textbook.service;

import com.campus.textbook.common.Result;

public interface FavoriteService {

    /** 收藏/取消收藏教材（切换） */
    Result toggleFavorite(Integer userId, Integer bookId);

    /** 查询我的收藏列表 */
    Result getMyFavorites(Integer userId, int page, int pageSize);

    /** 检查是否已收藏 */
    Result isFavorited(Integer userId, Integer bookId);
}
