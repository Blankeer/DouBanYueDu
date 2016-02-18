package com.douban.book.reader.entity;

import com.douban.book.reader.R;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.manager.cache.Identifiable;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import java.util.Date;

public class Review implements Identifiable {
    public UserInfo author;
    public int commentCount;
    public String content;
    public Date createTime;
    @Deprecated
    public boolean downvoted;
    public boolean hasPurchased;
    public int id;
    public boolean isPrivate;
    public int rating;
    public Date updateTime;
    public int usefulCount;
    public int uselessCount;
    @Deprecated
    public boolean voted;
    public int worksId;

    public Integer getId() {
        return Integer.valueOf(this.id);
    }

    public boolean isUpVoted() {
        return this.voted && !this.downvoted;
    }

    public boolean isDownVoted() {
        return this.voted && this.downvoted;
    }

    public String getAuthorName() {
        if (this.author == null || (isPostedByMe() && UserManager.getInstance().isAnonymousUser())) {
            return Res.getString(R.string.me);
        }
        return this.author.name;
    }

    public boolean hasContent() {
        return StringUtils.isNotEmpty(this.content);
    }

    public boolean isPostedByMe() {
        return this.author == null || this.author.id == UserManager.getInstance().getUserId();
    }
}
