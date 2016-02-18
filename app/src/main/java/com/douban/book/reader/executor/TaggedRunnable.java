package com.douban.book.reader.executor;

public abstract class TaggedRunnable implements Runnable {
    private Object mTag;

    public TaggedRunnable(Object tag) {
        setTag(tag);
    }

    public void setTag(Object tag) {
        this.mTag = tag;
    }

    public Object getTag() {
        return this.mTag;
    }
}
