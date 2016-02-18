package com.douban.book.reader.view;

import android.content.Context;
import android.os.Bundle;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.controller.TaskController;
import com.douban.book.reader.entity.Tag;
import com.douban.book.reader.lib.view.FlowLayout;
import com.douban.book.reader.manager.WorksManager_;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.card.Card;
import java.util.List;
import java.util.concurrent.Callable;
import natalya.os.TaskExecutor.TaskCallback;

public class WorksTagsCard extends Card<WorksTagsCard> {
    private int mWorksId;
    private FlowLayout mWorksTagsLayout;

    public WorksTagsCard(Context context) {
        super(context);
        init();
    }

    public WorksTagsCard worksId(int worksId) {
        this.mWorksId = worksId;
        loadTags();
        return this;
    }

    private void init() {
        ViewUtils.setBottomPaddingResId(this, R.dimen.general_subview_vertical_padding_medium);
        content((int) R.layout.card_works_tags);
        this.mWorksTagsLayout = (FlowLayout) findViewById(R.id.tags_layout);
    }

    private void updateTagsView(List<Tag> list) {
        if (list == null || list.size() == 0) {
            setVisibility(8);
        }
        for (Tag tag : list) {
            TagView tagView = TagView_.build(App.get());
            tagView.setEntity(tag);
            this.mWorksTagsLayout.addView(tagView);
        }
    }

    private void loadTags() {
        TaskController.getInstance().execute(new Callable<List<Tag>>() {
            public List<Tag> call() throws Exception {
                return WorksManager_.getInstance_(App.get()).worksTags(WorksTagsCard.this.mWorksId);
            }
        }, new TaskCallback<List<Tag>>() {
            public void onTaskSuccess(List<Tag> tags, Bundle extras, Object object) {
                WorksTagsCard.this.updateTagsView(tags);
            }

            public void onTaskFailure(Throwable e, Bundle extras) {
                Logger.e(WorksTagsCard.this.TAG, e);
                WorksTagsCard.this.setVisibility(8);
            }
        }, this);
    }
}
