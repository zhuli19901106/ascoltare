package com.zhuli.ascoltate.server.util;

import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Support page/size style and offset/limit style pagination
 */

public class BetterPageable implements Pageable {
    private int offset = 0;
    private int limit = 15;
    private int page = 0;
    private int size = 15;
    @Setter
    private Sort sort;

    public static BetterPageable limitOf(int offset, int limit) {
        return limitOf(offset, limit, Sort.unsorted());
    }

    public static BetterPageable limitOf(int offset, int limit, Sort sort) {
        BetterPageable page = new BetterPageable();
        page.offset = offset;
        page.limit = limit;
        page.sort = sort;
        page.size = limit;
        return page;
    }

    public static BetterPageable of(int page, int size) {
        return of(page, size, Sort.unsorted());
    }

    public static BetterPageable of(int page, int size, Sort sort) {
        BetterPageable pageable = new BetterPageable();
        pageable.page = page;
        pageable.size = size;
        pageable.offset = page * size;
        pageable.limit = size;
        pageable.sort = sort;
        return pageable;
    }

    public static BetterPageable of(int page, int size, Sort.Direction direction, String... properties) {
        BetterPageable pageable = new BetterPageable();
        pageable.page = page;
        pageable.size = size;
        pageable.offset = page * size;
        pageable.limit = size;
        if (direction != null && properties != null && properties.length > 0) {
            pageable.sort = new Sort(direction, properties);
        } else {
            pageable.sort = Sort.unsorted();
        }
        return pageable;
    }

    @Override
    public int getPageNumber() {
        return page;
    }

    @Override
    public int getPageSize() {
        return size;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return this.sort;
    }

    @Override
    public Pageable next() {
        return null;
    }

    @Override
    public Pageable previousOrFirst() {
        return null;
    }

    @Override
    public Pageable first() {
        return null;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }

    public int getLimit() {
        return limit;
    }
}
