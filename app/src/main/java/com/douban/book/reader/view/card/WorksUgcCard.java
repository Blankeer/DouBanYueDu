package com.douban.book.reader.view.card;

import android.content.Context;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.event.WorksUpdatedEvent;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.WorksUGCView;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;

@EViewGroup
public class WorksUgcCard extends Card<WorksUgcCard> {
    @Bean
    WorksManager mWorkManager;
    private int mWorksId;
    private WorksUGCView mWorksUGC;

    public WorksUgcCard(Context context) {
        super(context);
        init();
    }

    private void init() {
        content((int) R.layout.card_works_ugc);
        this.mWorksUGC = (WorksUGCView) findViewById(R.id.works_ugc);
        this.mWorksUGC.setMode(0);
        noContentPadding();
        ViewUtils.setEventAware(this);
    }

    public WorksUgcCard worksId(int worksId) {
        this.mWorksId = worksId;
        loadWorks();
        return this;
    }

    public void onEventMainThread(WorksUpdatedEvent event) {
        if (event.isValidFor(this.mWorksId)) {
            loadWorks();
        }
    }

    @Background
    void loadWorks() {
        try {
            updateViews(this.mWorkManager.getWorks(this.mWorksId));
        } catch (Exception e) {
            Logger.e(this.TAG, e);
        }
    }

    @UiThread
    void updateViews(Works works) {
        this.mWorksUGC.worksId(this.mWorksId);
    }
}
