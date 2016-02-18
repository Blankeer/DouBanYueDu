package com.douban.book.reader.event;

public class ReviewDeletedEvent {
    private int mReviewId;

    public ReviewDeletedEvent(int reviewId) {
        this.mReviewId = reviewId;
    }

    public boolean isValidForReviewId(int reviewId) {
        return this.mReviewId == reviewId;
    }
}
