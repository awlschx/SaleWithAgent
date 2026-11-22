package com.chx.saleswithagent.Result;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T>{
    private List<T> items;
    private long total;
    private int page;
    private int size;
    private boolean hasMore;
    public static <T> PageResult<T> of(List<T> items, long total, int page, int size) {
        PageResult<T> r = new PageResult<>();
        r.items = items;
        r.total = total;
        r.page = page;
        r.size = size;
        r.hasMore = (long)page*size < total;// 先转long再乘，防int溢出
        return r;



    }
}
