package com.douban.book.reader.entity;

import com.douban.book.reader.R;
import com.douban.book.reader.manager.UserManager;
import com.douban.book.reader.manager.cache.Identifiable;
import com.douban.book.reader.util.Res;

public class UserInfo implements Identifiable {
    public static final int TYPE_USER_ANONYMOUS = 0;
    public static final int TYPE_USER_NORMAL = 1;
    public int agentId;
    public int amountLeft;
    public String avatar;
    public int creditLeft;
    public int id;
    public String name;
    public int purchasedCount;
    public int type;

    public static UserInfo createDefault() {
        UserInfo userInfo = new UserInfo();
        userInfo.name = Res.getString(R.string.me);
        return userInfo;
    }

    public Integer getId() {
        return Integer.valueOf(this.id);
    }

    public boolean isMe() {
        return UserManager.getInstance().getUserId() == this.id;
    }

    public String getDisplayName() {
        if (isMe() && UserManager.getInstance().isAnonymousUser()) {
            return Res.getString(R.string.me);
        }
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public boolean isAuthor() {
        return this.agentId > 0;
    }

    public boolean isAnonymous() {
        return this.type == 0;
    }
}
