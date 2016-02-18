package com.douban.book.reader.entity;

import com.douban.book.reader.manager.cache.Identifiable;
import com.douban.book.reader.util.DateUtils;
import java.util.Date;

public class GiftEvent implements Identifiable {
    public int amount;
    public int availableAmount;
    public Date beginTime;
    public String defaultMessage;
    public Date endTime;
    public String giftAdImg;
    public String giftAlias;
    public String giftPackAdImg;
    public String hashTag;
    public int id;
    public String name;
    public String profileImg;
    public String subjectImg;
    public String url;

    public Object getId() {
        return Integer.valueOf(this.id);
    }

    public boolean isOnGoing() {
        return DateUtils.isInRange(this.beginTime, this.endTime);
    }
}
