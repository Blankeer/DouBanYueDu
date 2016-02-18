package com.douban.book.reader.entity;

import com.douban.book.reader.manager.cache.Identifiable;

public class Topic implements Identifiable {
    public String description;
    public int id;
    public String title;
    public String uri;

    public Integer getId() {
        return Integer.valueOf(this.id);
    }
}
