package com.douban.book.reader.event;

import com.douban.book.reader.entity.Progress;

public class RemoteProgressLoadedEvent extends WorksEvent {
    private Progress mRemoteProgress;

    public RemoteProgressLoadedEvent(int bookId, Progress progress) {
        super(bookId);
        this.mRemoteProgress = progress;
    }

    public Progress getRemoteProgress() {
        return this.mRemoteProgress;
    }
}
