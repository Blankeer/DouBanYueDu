package com.douban.book.reader.entity;

import com.douban.book.reader.R;
import com.douban.book.reader.manager.cache.Identifiable;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import java.util.Date;

public class GiftPack implements Identifiable {
    public Date createTime;
    public GiftEvent event;
    public int giftCount;
    public UserInfo giver;
    public String hashCode;
    public int id;
    public String message;
    public Date messageTime;
    public int price;
    public int quantity;
    public int status;
    public Works works;

    public static class Status {
        private static final int PACKED = 1;
        private static final int PENDING = 0;
    }

    public Object getId() {
        return Integer.valueOf(this.id);
    }

    public boolean isPacked() {
        return this.status == 1;
    }

    public boolean isDepleted() {
        return !isUnlimited() && this.giftCount >= this.quantity;
    }

    public boolean isUnlimited() {
        return this.quantity <= 0;
    }

    public int availableQuantity() {
        if (isUnlimited()) {
            return AdvancedShareActionProvider.WEIGHT_MAX;
        }
        return Math.max(this.quantity - this.giftCount, 0);
    }

    public boolean isMine() {
        return this.giver.isMe();
    }

    public boolean canEditMessage() {
        return this.giftCount <= 0;
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
