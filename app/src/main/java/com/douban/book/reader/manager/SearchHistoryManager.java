package com.douban.book.reader.manager;

import com.douban.book.reader.constant.Key;
import com.douban.book.reader.event.ArkEvent;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.util.Pref;
import com.douban.book.reader.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.androidannotations.annotations.EBean;

@EBean
public class SearchHistoryManager {
    private Set<String> mSearchHistorySet;

    public SearchHistoryManager() {
        init();
    }

    private void init() {
        this.mSearchHistorySet = Pref.ofApp().getStringSet(Key.APP_SEARCH_HISTORY);
    }

    public boolean isEmpty() {
        return this.mSearchHistorySet != null && this.mSearchHistorySet.isEmpty();
    }

    public List<String> getQueryList() {
        return new ArrayList(this.mSearchHistorySet);
    }

    public void addNewQuery(String query) {
        if (StringUtils.isNotEmpty(query)) {
            this.mSearchHistorySet.add(query);
            EventBusUtils.post(ArkEvent.SEARCH_HISTORY_CHANGED);
            Pref.ofApp().set(Key.APP_SEARCH_HISTORY, this.mSearchHistorySet);
        }
    }

    public void clear() {
        this.mSearchHistorySet.clear();
        EventBusUtils.post(ArkEvent.SEARCH_HISTORY_CHANGED);
        Pref.ofApp().remove(Key.APP_SEARCH_HISTORY);
    }
}
