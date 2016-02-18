package com.douban.book.reader.view.card;

import android.content.Context;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.location.Toc;
import com.douban.book.reader.util.Logger;
import java.util.List;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;

@EViewGroup
public class WorksTocCard extends TextCard {
    private int mWorksId;

    public WorksTocCard(Context context) {
        super(context);
    }

    public WorksTocCard worksId(int worksId) {
        this.mWorksId = worksId;
        loadData();
        return this;
    }

    @UiThread
    void updateContent(List<Paragraph> paragraphList) {
        if (paragraphList.isEmpty()) {
            onLoadFailed();
        } else {
            content((List) paragraphList);
        }
    }

    @Background
    void loadData() {
        try {
            updateContent(Toc.get(this.mWorksId).getTocParagraphList());
        } catch (Exception e) {
            Logger.e(this.TAG, e);
            onLoadFailed();
        }
    }

    @UiThread
    void onLoadFailed() {
        hide();
    }
}
