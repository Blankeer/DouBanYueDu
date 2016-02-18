package com.douban.book.reader.entity.store;

import com.douban.book.reader.entity.WorksKind;
import java.util.List;

public class KindListStoreWidgetEntity extends BaseStoreWidgetEntity {
    public Payload payload;

    public class Payload {
        public boolean hasMoreBtn;
        public List<WorksKind> kindList;
    }
}
