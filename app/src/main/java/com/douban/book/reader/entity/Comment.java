package com.douban.book.reader.entity;

import com.douban.book.reader.manager.cache.Identifiable;
import java.util.Date;

public class Comment implements Identifiable {
    public String content;
    public Date createTime;
    public int id;
    public UserInfo user;

    public Integer getId() {
        return Integer.valueOf(this.id);
    }

    public boolean isPostedByMe() {
        return this.user != null && this.user.isMe();
    }
}
