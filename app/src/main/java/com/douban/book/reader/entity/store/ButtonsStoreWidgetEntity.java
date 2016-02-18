package com.douban.book.reader.entity.store;

import com.douban.book.reader.entity.store.BaseStoreWidgetEntity.LinkButton;
import java.util.List;

public class ButtonsStoreWidgetEntity extends BaseStoreWidgetEntity {
    public Payload payload;

    public class Payload {
        public List<LinkButton> buttons;
    }
}
