package com.douban.book.reader.entity;

import com.douban.book.reader.manager.cache.Identifiable;
import java.util.Date;

public class PurchaseRecord implements Identifiable {
    public int amount;
    public int id;
    public UserInfo recipient;
    public Target target;
    public Date time;
    public int type;

    public static class Target {
        public String cover;
        public String title;
    }

    public static class Type {
        public static int PURCHASE_FOR_GIFT;
        public static int PURCHASE_FOR_SELF;

        static {
            PURCHASE_FOR_GIFT = 1;
            PURCHASE_FOR_SELF = 2;
        }
    }

    public Integer getId() {
        return Integer.valueOf(this.id);
    }
}
