package com.douban.book.reader.content.chapter;

import android.content.Context;
import com.douban.book.reader.view.page.AbsPageView;
import com.douban.book.reader.view.page.LastPageView_;

public class LastPageChapter extends PseudoChapter {
    public LastPageChapter(int bookId) {
        super(bookId);
    }

    public int getPackageId() {
        return Chapter.ID_PURCHASE;
    }

    public AbsPageView getPageView(Context context, int pageNum) {
        return LastPageView_.build(context);
    }
}
