package com.douban.book.reader.event;

public class PagingStartedEvent extends PagingStatusChangedEvent {
    public PagingStartedEvent(int bookId) {
        super(bookId);
    }
}
