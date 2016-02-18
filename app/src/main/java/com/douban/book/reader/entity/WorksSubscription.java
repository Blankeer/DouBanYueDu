package com.douban.book.reader.entity;

import com.douban.book.reader.manager.cache.Identifiable;

public class WorksSubscription implements Identifiable {
    public String author;
    public String coverUrl;
    public boolean hasFinished;
    public int id;
    public boolean isCanceled;
    public String title;
    public int worksId;

    public Integer getId() {
        return Integer.valueOf(this.id);
    }
}
