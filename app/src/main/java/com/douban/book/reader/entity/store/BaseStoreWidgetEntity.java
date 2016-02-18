package com.douban.book.reader.entity.store;

import com.douban.book.reader.manager.cache.Identifiable;

public class BaseStoreWidgetEntity implements Identifiable {
    public int id;
    public String subtitle;
    public String title;
    public String type;

    public static class LinkButton {
        public int id;
        public String text;
        public String uri;
    }

    public static class Type {
        public static final String BANNER = "widget_banner";
        public static final String BUTTONS = "widget_buttons";
        public static final String CHARTS = "widget_charts";
        public static final String KIND = "widget_kind";
        public static final String KIND_LIST = "widget_kind_list";
        public static final String LINK = "widget_link";
        public static final String LINKS = "widget_links";
        public static final String MORE_WORKS = "widget_more_works";
        public static final String NEW_WORKS = "widget_new_works";
        public static final String PROMOTION = "widget_promotion";
        public static final String TOPIC = "widget_topic";
    }

    public Integer getId() {
        return Integer.valueOf(this.id);
    }
}
