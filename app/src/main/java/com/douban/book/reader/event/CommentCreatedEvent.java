package com.douban.book.reader.event;

public class CommentCreatedEvent {
    public int reviewId;

    public CommentCreatedEvent(int reviewId) {
        this.reviewId = reviewId;
    }

    public boolean isValidForReviewId(int reviewId) {
        return this.reviewId == reviewId;
    }
}
