package com.douban.book.reader.entity.store;

import com.douban.book.reader.entity.Works;
import java.util.List;

public class BannerStoreWidgetEntity extends BaseStoreWidgetEntity {
    public Payload payload;

    public class Payload {
        public List<Works> worksList;
    }
}
