package com.douban.book.reader.event;

public class PagingStatusChangedEvent extends WorksEvent {
    public PagingStatusChangedEvent(int bookId) {
        super(bookId);
    }
}
