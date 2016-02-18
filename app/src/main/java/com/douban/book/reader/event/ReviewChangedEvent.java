package com.douban.book.reader.event;

public class ReviewChangedEvent {
    private int mReviewId;
    private int mWorksId;

    public ReviewChangedEvent(int worksId, int reviewId) {
        this.mWorksId = worksId;
        this.mReviewId = reviewId;
    }

    public boolean isValidForWorks(int worksId) {
        return this.mWorksId == worksId;
    }

    public boolean isValidForReview(int reviewId) {
        return this.mReviewId == reviewId;
    }
}
