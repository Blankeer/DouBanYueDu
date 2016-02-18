package com.douban.book.reader.event;

public class PagingProgressUpdatedEvent extends WorksEvent {
    private int mProgress;

    public PagingProgressUpdatedEvent(int bookId, int progress) {
        super(bookId);
        this.mProgress = progress;
    }

    public int getProgress() {
        return this.mProgress;
    }
}
