package com.douban.book.reader.view.card;

import android.content.Context;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.store.WorksGridView;
import com.douban.book.reader.view.store.WorksGridView_;
import java.util.List;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;

@EViewGroup
public class SimilarWorksCard extends Card<SimilarWorksCard> {
    private WorksGridView mWorksBanner;
    int mWorksId;
    @Bean
    WorksManager mWorksManager;

    public SimilarWorksCard(Context context) {
        super(context);
        initViews();
    }

    public SimilarWorksCard worksId(int worksId) {
        this.mWorksId = worksId;
        loadData();
        return this;
    }

    private void initViews() {
        this.mWorksBanner = WorksGridView_.build(getContext());
        content(this.mWorksBanner);
        noContentPadding();
        title((int) R.string.title_works_similar);
        ViewUtils.setBottomPaddingResId(this.mWorksBanner, R.dimen.general_subview_vertical_padding_medium);
    }

    @Background
    void loadData() {
        try {
            updateViews(this.mWorksManager.getSimilarWorks(this.mWorksId));
        } catch (Exception e) {
            Logger.e(this.TAG, e);
            onLoadFailed();
        }
    }

    @UiThread
    void onLoadFailed() {
        setVisibility(8);
    }

    @UiThread
    void updateViews(List<Works> worksList) {
        if (this.mWorksBanner == null || worksList == null || worksList.size() <= 0) {
            onLoadFailed();
            return;
        }
        this.mWorksBanner.showFirstRowOnly(false);
        this.mWorksBanner.setWorksList(worksList);
    }
}
