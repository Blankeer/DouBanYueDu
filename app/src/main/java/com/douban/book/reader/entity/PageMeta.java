package com.douban.book.reader.entity;

import com.douban.book.reader.manager.cache.Identifiable;
import java.util.List;

public class PageMeta implements Identifiable {
    public String description;
    public String title;
    public List<Topic> topicList;

    public String getId() {
        return this.title;
    }
}
