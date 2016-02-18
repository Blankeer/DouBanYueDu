package com.douban.book.reader.entity;

import com.douban.book.reader.manager.cache.Identifiable;

public class OrderInfo implements Identifiable {
    public boolean hasFinished;
    public String order;
    public String url;

    public Object getId() {
        return Integer.valueOf(0);
    }
}
