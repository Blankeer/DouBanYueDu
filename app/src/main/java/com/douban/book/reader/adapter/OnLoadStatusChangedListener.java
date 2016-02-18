package com.douban.book.reader.adapter;

public interface OnLoadStatusChangedListener {
    void onLoadingEnd(Throwable th);

    void onLoadingStarted();
}
