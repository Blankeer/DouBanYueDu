package com.douban.book.reader.entity.store;

import com.douban.book.reader.entity.Works;
import com.douban.book.reader.entity.store.BaseStoreWidgetEntity.LinkButton;
import java.util.List;

public class ChartsStoreWidgetEntity extends BaseStoreWidgetEntity {
    public Payload payload;

    public class Chart {
        public int id;
        public LinkButton moreBtn;
        public String title;
        public Works topWorks;
    }

    public class Payload {
        public List<Chart> charts;
    }
}
