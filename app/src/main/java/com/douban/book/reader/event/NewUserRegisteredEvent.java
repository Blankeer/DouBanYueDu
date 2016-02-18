package com.douban.book.reader.event;

import com.douban.book.reader.entity.Session;

public class NewUserRegisteredEvent {
    public final Session session;
    public final String userName;

    public NewUserRegisteredEvent(String userName, Session session) {
        this.userName = userName;
        this.session = session;
    }
}
