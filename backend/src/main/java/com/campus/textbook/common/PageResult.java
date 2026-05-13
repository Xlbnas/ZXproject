package com.campus.textbook.common;

import lombok.Data;
import java.util.List;

/**
 * 分页结果封装
 */
@Data
public class PageResult {
    private List<?> list;   // 当前页数据
    private long total;     // 总记录数
    private int page;       // 当前页码
    private int pageSize;   // 每页条数

    public PageResult(List<?> list, long total, int page, int pageSize) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }
}
