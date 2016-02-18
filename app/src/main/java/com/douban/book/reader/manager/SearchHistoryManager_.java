package com.douban.book.reader.manager;

import android.content.Context;

public final class SearchHistoryManager_ extends SearchHistoryManager {
    private Context context_;

    private SearchHistoryManager_(Context context) {
        this.context_ = context;
        init_();
    }

    public static SearchHistoryManager_ getInstance_(Context context) {
        return new SearchHistoryManager_(context);
    }

    private void init_() {
    }

    public void rebind(Context context) {
        this.context_ = context;
        init_();
    }
}
