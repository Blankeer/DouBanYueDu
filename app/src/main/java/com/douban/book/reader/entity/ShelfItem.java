package com.douban.book.reader.entity;

import android.provider.BaseColumns;
import com.douban.book.reader.database.AndroidDao;
import com.douban.book.reader.manager.cache.Identifiable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;

@DatabaseTable(daoClass = AndroidDao.class, tableName = "shelf")
public class ShelfItem implements Identifiable {
    public static final String TABLE_NAME = "shelf";
    @DatabaseField(columnName = "id", id = true)
    public int id;
    @DatabaseField(columnName = "last_touched_time")
    public Date lastTouchedTime;
    @DatabaseField(columnName = "title")
    public String title;

    public static final class Column implements BaseColumns {
        public static final String ID = "id";
        public static final String LAST_TOUCHED_TIME = "last_touched_time";
        public static final String TITLE = "title";
    }

    public Object getId() {
        return Integer.valueOf(this.id);
    }

    public boolean equals(Object shelfItem) {
        if (!(shelfItem instanceof ShelfItem)) {
            return false;
        }
        if (((Integer) ((ShelfItem) shelfItem).getId()).intValue() == this.id) {
            return true;
        }
        return false;
    }
}
