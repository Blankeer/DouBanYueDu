package com.douban.book.reader.event;

public class CommentDeletedEvent {
    private int mCommentId;
    private int mReviewId;

    public CommentDeletedEvent(int reviewId, int commentId) {
        this.mReviewId = reviewId;
        this.mCommentId = commentId;
    }

    public boolean isValidForReviewId(int reviewId) {
        return this.mReviewId == reviewId;
    }

    public boolean isValidForCommentId(int commentId) {
        return this.mCommentId == commentId;
    }
}
