package com.douban.book.reader.view.item;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ViewBinder;
import com.douban.book.reader.entity.WorksSubscription;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.WorksCoverView;
import io.realm.internal.Table;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903124)
public class WorksSubscriptionItemView extends RelativeLayout implements ViewBinder<WorksSubscription> {
    @ViewById(2131558774)
    TextView mAuthor;
    @ViewById(2131558772)
    WorksCoverView mCover;
    @ViewById(2131558773)
    TextView mStatus;
    @ViewById(2131558462)
    TextView mTitle;

    public WorksSubscriptionItemView(Context context) {
        super(context);
    }

    public WorksSubscriptionItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WorksSubscriptionItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterViews
    void init() {
        setGravity(16);
        ViewUtils.of(this).width(-1).height(-2).commit();
    }

    public void bindData(WorksSubscription worksSubscription) {
        this.mCover.url(worksSubscription.coverUrl);
        this.mTitle.setText(worksSubscription.title);
        this.mAuthor.setText(Res.getString(R.string.title_author, worksSubscription.author));
        if (worksSubscription.hasFinished) {
            this.mStatus.setText(R.string.subscription_status_finished);
        } else if (worksSubscription.isCanceled) {
            this.mStatus.setText(R.string.subscription_status_canceled);
        } else {
            this.mStatus.setText(Table.STRING_DEFAULT_VALUE);
            this.mStatus.setVisibility(8);
        }
    }

    public void showStatus(boolean show) {
        ViewUtils.showIf(show, this.mStatus);
    }

    public void setTitleColor(int color) {
        this.mTitle.setTextColor(color);
    }
}
