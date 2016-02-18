package com.douban.book.reader.entity.store;

import com.douban.book.reader.entity.Works;
import com.douban.book.reader.entity.store.BaseStoreWidgetEntity.LinkButton;
import java.util.List;

public class TopicStoreWidgetEntity extends BaseStoreWidgetEntity {
    public Payload payload;

    public class Payload {
        public List<String> display;
        public LinkButton moreBtn;
        public String theme;
        public List<Works> worksList;
    }
}
