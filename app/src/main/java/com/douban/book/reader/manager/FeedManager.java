package com.douban.book.reader.manager;

import com.douban.book.reader.entity.Feed;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.network.exception.RestException;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

@EBean(scope = Scope.Singleton)
public class FeedManager extends BaseManager<Feed> {
    @Bean
    UnreadCountManager mUnreadCountManager;

    public FeedManager() {
        super("feeds", Feed.class);
    }

    public Lister<Feed> feedsChapterUpdatedLister() {
        return list();
    }

    public int getUnreadCount() throws DataLoadException {
        return this.mUnreadCountManager.getUnreadFeedsCount();
    }

    public void markAsRead(int id) throws DataLoadException {
        try {
            getRestClient().getSubClientWithId(Integer.valueOf(id), "read", Feed.class).put();
            this.mUnreadCountManager.refresh();
        } catch (RestException e) {
            throw wrapDataLoadException(e);
        }
    }

    public void markAllAsRead() throws DataLoadException {
        try {
            getSubManager("read_all", Feed.class).getRestClient().put();
            this.mUnreadCountManager.refresh();
        } catch (RestException e) {
            throw wrapDataLoadException(e);
        }
    }
}
