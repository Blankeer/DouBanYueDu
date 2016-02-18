package com.douban.book.reader.entity.store;

import com.douban.book.reader.manager.cache.Identifiable;
import java.util.List;

public class StoreTabEntity implements Identifiable {
    public String name;
    public String title;
    public List<BaseStoreWidgetEntity> widgetList;

    public Object getId() {
        return this.name;
    }
}
