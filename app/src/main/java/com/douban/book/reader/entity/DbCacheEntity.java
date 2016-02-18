package com.douban.book.reader.entity;

import android.provider.BaseColumns;
import com.douban.book.reader.database.AndroidDao;
import com.douban.book.reader.manager.cache.Identifiable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(daoClass = AndroidDao.class, tableName = "cache")
public class DbCacheEntity implements Identifiable {
    public static final String TABLE_NAME = "cache";
    @DatabaseField(columnName = "id", id = true)
    public String id;
    @DatabaseField(columnName = "value")
    public String value;

    public static final class Column implements BaseColumns {
        public static final String ID = "id";
        public static final String VALUE = "value";
    }

    public Object getId() {
        return this.id;
    }
}
