package com.douban.book.reader.manager;

import com.douban.book.reader.entity.Notification;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.network.exception.RestException;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

@EBean(scope = Scope.Singleton)
public class NotificationManager extends BaseManager<Notification> {
    @Bean
    UnreadCountManager mUnreadCountManager;

    public NotificationManager() {
        super("notifications", Notification.class);
    }

    public void markAsRead(int id) throws DataLoadException {
        try {
            getRestClient().getSubClientWithId(Integer.valueOf(id), "read", Notification.class).put();
            this.mUnreadCountManager.refresh();
        } catch (RestException e) {
            throw wrapDataLoadException(e);
        }
    }

    public void markAllAsRead() throws DataLoadException {
        try {
            getSubManager("read_all", Notification.class).getRestClient().put();
            this.mUnreadCountManager.refresh();
        } catch (RestException e) {
            throw wrapDataLoadException(e);
        }
    }

    public int getUnreadCount() throws DataLoadException {
        return this.mUnreadCountManager.getUnreadNotificationsCount();
    }

    public Lister<Notification> myList() {
        return getSubManager("mine", Notification.class).list();
    }
}
