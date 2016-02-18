package com.douban.book.reader.adapter;

public interface Pageable {
    void loadNextPage();

    void setOnLoadStateChangedListener(OnLoadStatusChangedListener onLoadStatusChangedListener);
}
