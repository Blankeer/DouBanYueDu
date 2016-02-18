package com.douban.book.reader.event;

public class ManifestUpdatedEvent extends WorksEvent {
    public ManifestUpdatedEvent(int worksId) {
        super(worksId);
    }
}
