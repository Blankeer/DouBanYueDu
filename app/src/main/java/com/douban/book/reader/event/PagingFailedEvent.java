package com.douban.book.reader.event;

public class PagingFailedEvent extends PagingStatusChangedEvent {
    public PagingFailedEvent(int bookId) {
        super(bookId);
    }
}
