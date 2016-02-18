package com.douban.book.reader.event;

public class WorksEvent {
    private int mWorksId;

    public WorksEvent(int worksId) {
        this.mWorksId = worksId;
    }

    public boolean isValidFor(int worksId) {
        return this.mWorksId == 0 || this.mWorksId == worksId;
    }

    public int getWorksId() {
        return this.mWorksId;
    }
}
