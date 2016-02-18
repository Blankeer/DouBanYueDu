package com.douban.book.reader.event;

public class SelectionUpdatedEvent {
    int mBookId;

    public SelectionUpdatedEvent(int bookId) {
        this.mBookId = bookId;
    }

    public SelectionUpdatedEvent() {
        this(-1);
    }

    public boolean isValidForBook(int bookId) {
        return this.mBookId < 0 || this.mBookId == bookId;
    }
}
