package com.douban.book.reader.entity;

import com.douban.book.reader.R;
import com.douban.book.reader.manager.cache.Identifiable;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import java.util.Date;
import java.util.UUID;

public class Gift implements Identifiable {
    public Date createTime;
    public GiftEvent event;
    public UserInfo giver;
    public String message;
    public Date messageTime;
    public int packId;
    public UserInfo recipient;
    public UUID uuid;
    public Works works;

    public Object getId() {
        return this.uuid;
    }

    public boolean isMine() {
        return this.recipient.isMe();
    }

    public CharSequence getGiftAlias() {
        if (this.event != null) {
            if (StringUtils.isNotEmpty(this.event.giftAlias)) {
                return this.event.giftAlias;
            }
        }
        return Res.getString(R.string.ebook);
    }

    public CharSequence getWeiboHashTag() {
        if (this.event != null) {
            if (StringUtils.isNotEmpty(this.event.hashTag)) {
                return String.format(" #%s#", new Object[]{this.event.hashTag});
            }
        }
        return null;
    }
}
