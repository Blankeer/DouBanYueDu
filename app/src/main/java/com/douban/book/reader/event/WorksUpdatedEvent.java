package com.douban.book.reader.event;

public class WorksUpdatedEvent extends WorksEvent {
    public WorksUpdatedEvent(int worksId) {
        super(worksId);
    }
}
