package com.douban.book.reader.entity;

import com.douban.book.reader.R;
import com.douban.book.reader.manager.cache.Identifiable;
import com.douban.book.reader.util.Res;
import io.realm.internal.Table;
import java.util.Date;

public class DepositRecord implements Identifiable {
    public int amount;
    public int id;
    public Date time;
    public int type;

    public static class Type {
        public static int COUPON;
        public static int DEPOSIT;

        static {
            DEPOSIT = 1;
            COUPON = 2;
        }
    }

    public String getTypeName() {
        if (this.type == Type.DEPOSIT) {
            return Res.getString(R.string.type_record_deposit);
        }
        if (this.type == Type.COUPON) {
            return Res.getString(R.string.type_record_redeem);
        }
        return Table.STRING_DEFAULT_VALUE;
    }

    public Integer getId() {
        return Integer.valueOf(this.id);
    }
}
