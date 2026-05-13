package com.campus.textbook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.textbook.entity.Transaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TransactionMapper extends BaseMapper<Transaction> {

    /** 查询某用户的订单（买家或卖家），支持状态筛选（论文5.1.4） */
    List<Transaction> selectByUserAndStatus(@Param("userId") Integer userId,
                                            @Param("status") Integer status,
                                            @Param("offset") int offset,
                                            @Param("pageSize") int pageSize);

    int countByUserAndStatus(@Param("userId") Integer userId,
                             @Param("status") Integer status);

    /** 管理员查询所有交易，支持状态筛选和关键词（论文5.2.3） */
    List<Transaction> selectByCondition(@Param("status") Integer status,
                                        @Param("keyword") String keyword,
                                        @Param("offset") int offset,
                                        @Param("pageSize") int pageSize);

    int countByCondition(@Param("status") Integer status,
                         @Param("keyword") String keyword);

    /** 检查某用户是否已经对同一本教材发起过未完成的交易 */
    int countActiveByBookAndRequester(@Param("bookId") Integer bookId,
                                      @Param("requesterId") Integer requesterId);
}
