package com.douban.book.reader.entity;

import com.douban.book.reader.manager.cache.Identifiable;

public class DummyEntity implements Identifiable {
    public Object getId() {
        return Integer.valueOf(0);
    }
}
