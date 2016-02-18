package com.douban.book.reader.entity;

import com.douban.book.reader.manager.cache.Identifiable;
import java.util.Date;

public class Feed implements Identifiable {
    public String cover;
    public String desc;
    public boolean hasRead;
    public int id;
    public Payload payload;
    public Date publishTime;
    public String title;

    public static class Payload {
        public int chapterId;
        public int price;
        public int worksId;
        public String worksTitle;
    }

    public Integer getId() {
        return Integer.valueOf(this.id);
    }
}
