package com.douban.book.reader.entity;

import com.douban.book.reader.R;
import com.douban.book.reader.manager.cache.Identifiable;
import com.douban.book.reader.util.StringUtils;

public class WorksKind implements Identifiable, Comparable<WorksKind> {
    public int[] childIds;
    public String description;
    public String icon;
    public int id;
    public boolean isRoot;
    public String name;
    public int parentId;
    public String uri;
    public int worksCount;

    public Integer getId() {
        return Integer.valueOf(this.id);
    }

    public int compareTo(WorksKind another) {
        if (this.isRoot && another.isRoot) {
            return getSortIndex(this.name) < getSortIndex(another.name) ? -1 : 1;
        } else {
            return 0;
        }
    }

    private int getSortIndex(String name) {
        if (StringUtils.equals((CharSequence) name, (int) R.string.title_original_works)) {
            return 0;
        }
        if (StringUtils.equals((CharSequence) name, (int) R.string.title_book)) {
            return 1;
        }
        if (StringUtils.equals((CharSequence) name, (int) R.string.title_magazine)) {
            return 2;
        }
        return 3;
    }
}
