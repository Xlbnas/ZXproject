package com.campus.textbook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.textbook.entity.Textbook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface TextbookMapper extends BaseMapper<Textbook> {

    /** 多条件检索教材（论文TC-SEARCH系列） */
    List<Textbook> searchTextbooks(@Param("keyword") String keyword,
                                   @Param("isbn") String isbn,
                                   @Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   @Param("offset") int offset,
                                   @Param("pageSize") int pageSize);

    /** 统计检索结果数量 */
    int countSearchTextbooks(@Param("keyword") String keyword,
                             @Param("isbn") String isbn,
                             @Param("minPrice") BigDecimal minPrice,
                             @Param("maxPrice") BigDecimal maxPrice);

    /** 查询某用户发布的所有教材 */
    List<Textbook> selectByPublisherId(@Param("publisherId") Integer publisherId);

    /** 管理员查询教材列表（可按审核状态筛选） */
    List<Textbook> selectForAdmin(@Param("auditStatus") Integer auditStatus,
                                  @Param("keyword") String keyword,
                                  @Param("offset") int offset,
                                  @Param("pageSize") int pageSize);

    int countForAdmin(@Param("auditStatus") Integer auditStatus,
                      @Param("keyword") String keyword);
}
