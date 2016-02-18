package com.douban.book.reader.entity;

import com.douban.book.reader.manager.cache.Identifiable;
import java.util.Date;

public class CouponRecord implements Identifiable {
    public int amount;
    public Date createTime;
    public int id;

    public Integer getId() {
        return Integer.valueOf(this.id);
    }
}
