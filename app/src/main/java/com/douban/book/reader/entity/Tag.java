package com.douban.book.reader.entity;

import com.douban.book.reader.manager.cache.Identifiable;

public class Tag implements Identifiable {
    public String comment;
    public int id;
    public String name;
    public String uri;

    public Integer getId() {
        return Integer.valueOf(this.id);
    }
}
