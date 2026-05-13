package com.campus.textbook.service.impl;

import com.campus.textbook.common.PageResult;
import com.campus.textbook.common.Result;
import com.campus.textbook.entity.Favorite;
import com.campus.textbook.mapper.FavoriteMapper;
import com.campus.textbook.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public Result toggleFavorite(Integer userId, Integer bookId) {
        Favorite existing = favoriteMapper.selectByUserAndBook(userId, bookId);
        if (existing != null) {
            // 已收藏，取消收藏
            favoriteMapper.deleteById(existing.getId());
            return Result.success("已取消收藏");
        } else {
            // 未收藏，添加收藏
            Favorite fav = new Favorite();
            fav.setUserId(userId);
            fav.setBookId(bookId);
            fav.setFavTime(new Date());
            favoriteMapper.insert(fav);
            return Result.success("收藏成功");
        }
    }

    @Override
    public Result getMyFavorites(Integer userId, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;
        int offset = (page - 1) * pageSize;
        List<Favorite> list = favoriteMapper.selectByUserId(userId, offset, pageSize);
        int total = favoriteMapper.countByUserId(userId);
        return Result.success(new PageResult(list, total, page, pageSize));
    }

    @Override
    public Result isFavorited(Integer userId, Integer bookId) {
        Favorite fav = favoriteMapper.selectByUserAndBook(userId, bookId);
        return Result.success(fav != null);
    }
}
