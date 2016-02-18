package com.douban.book.reader.entity;

import com.douban.book.reader.manager.cache.Identifiable;
import java.util.Date;

public class Notification implements Identifiable {
    public String content;
    public boolean hasRead;
    public int id;
    public Date time;
    public String uri;

    public Integer getId() {
        return Integer.valueOf(this.id);
    }
}
