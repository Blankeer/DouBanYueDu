package com.douban.book.reader.entity;

import com.douban.book.reader.R;
import com.douban.book.reader.manager.cache.Identifiable;
import com.douban.book.reader.util.Res;

public class WorksAgent implements Identifiable {
    public String avatar;
    public String description;
    public int id;
    public boolean isAgency;
    public String name;
    public String uri;
    public int worksCount;

    public Integer getId() {
        return Integer.valueOf(this.id);
    }

    public String getTitle() {
        return Res.getString(this.isAgency ? R.string.card_title_agent : R.string.card_title_author);
    }
}
