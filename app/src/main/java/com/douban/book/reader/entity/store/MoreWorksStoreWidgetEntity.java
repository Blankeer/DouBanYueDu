package com.douban.book.reader.entity.store;

import com.douban.book.reader.entity.Works;
import com.douban.book.reader.entity.store.BaseStoreWidgetEntity.LinkButton;
import java.util.List;

public class MoreWorksStoreWidgetEntity extends BaseStoreWidgetEntity {
    public Payload payload;

    public class Payload {
        public LinkButton moreBtn;
        public List<Works> worksList;
    }
}
