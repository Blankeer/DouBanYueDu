package com.douban.book.reader.event;

public class ReadingProgressUpdatedEvent extends WorksEvent {
    public ReadingProgressUpdatedEvent(int bookId) {
        super(bookId);
    }
}
