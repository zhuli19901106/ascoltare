package com.zhuli.ascoltate.common.dto.response;

import com.google.common.collect.Lists;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Page<T> {
    T list;
    long total;
    long offset;
    long limit;
    long page;
    long size;

    public Page(T list, long total, long offset, long limit) {
        this.list = list;
        this.total = total;
        this.offset = offset;
        this.limit = limit;
    }

    public static Page defaultPage() {
        Page page = new Page();
        page.setList(Lists.newArrayList());
        page.setLimit(15L);
        page.setPage(1L);
        return page;
    }
}
