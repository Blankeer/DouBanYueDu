package com.douban.book.reader.event;

public class PageFlipEvent extends WorksEvent {
    public static final int FLIP_TO_NEXT = 1;
    public static final int FLIP_TO_PREV = -1;
    private int mDirection;

    public PageFlipEvent(int bookId, int direction) {
        super(bookId);
        this.mDirection = direction;
    }

    public int getDirection() {
        return this.mDirection;
    }
}
