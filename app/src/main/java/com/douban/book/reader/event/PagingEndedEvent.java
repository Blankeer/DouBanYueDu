package com.douban.book.reader.event;

public class PagingEndedEvent extends PagingStatusChangedEvent {
    public PagingEndedEvent(int bookId) {
        super(bookId);
    }
}
