package com.douban.book.reader.entity.store;

import java.util.List;

public class LinksStoreWidgetEntity extends BaseStoreWidgetEntity {
    public Payload payload;

    public class Link {
        public String img;
        public String uri;
    }

    public class Payload {
        public List<Link> links;
    }
}
