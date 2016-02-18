package com.douban.book.reader.view.card;

import android.content.Context;
import android.widget.RatingBar;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.ParagraphView;
import com.douban.book.reader.view.ParagraphView.Indent;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EViewGroup
public class WorksInfoCard extends Card<WorksInfoCard> {
    @ViewById(2131558548)
    ParagraphView mAbstract;
    @ViewById(2131558549)
    RatingBar mRatingBar;
    @ViewById(2131558550)
    TextView mRatingInfo;
    @ViewById(2131558547)
    TextView mSubTitle;
    @ViewById(2131558546)
    TextView mTitle;
    @ViewById(2131558541)
    TextView mWorksBasicInfo;
    @Bean
    WorksManager mWorksManager;

    public WorksInfoCard(Context context) {
        super(context);
        content((int) R.layout.card_works_info);
    }

    public WorksInfoCard worksId(int worksId) {
        loadWorks(worksId);
        return this;
    }

    @Background
    void loadWorks(int worksId) {
        try {
            updateViews(this.mWorksManager.getWorks(worksId));
        } catch (DataLoadException e) {
            Logger.e(this.TAG, e);
        }
    }

    @UiThread
    void updateViews(Works works) {
        works(works);
    }

    public WorksInfoCard works(Works works) {
        if (works != null) {
            ViewUtils.showTextIfNotEmpty(this.mTitle, works.title);
            ViewUtils.showTextIfNotEmpty(this.mSubTitle, works.subtitle);
            ViewUtils.showTextIfNotEmpty(this.mWorksBasicInfo, works.formatBasicInfo());
            ViewUtils.showTextIfNotEmpty(this.mAbstract, works.abstractText);
            this.mAbstract.setFirstLineIndent(Indent.NONE);
            this.mRatingBar.setRating(works.averageRating);
            ViewUtils.showTextIfNotEmpty(this.mRatingInfo, works.formatRatingInfo());
        }
        return this;
    }
}
