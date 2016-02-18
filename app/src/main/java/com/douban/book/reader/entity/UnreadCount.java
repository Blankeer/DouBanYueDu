package com.douban.book.reader.entity;

import com.douban.book.reader.manager.cache.Identifiable;

public class UnreadCount implements Identifiable {
    public int feeds;
    public int notifications;

    public Object getId() {
        return Integer.valueOf(0);
    }
}
