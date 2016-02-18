package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ViewBinder;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.Font;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ViewUtils;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903201)
public class WorksItemView extends RelativeLayout implements ViewBinder<Works> {
    @ViewById(2131558967)
    TextView mAuthors;
    private boolean mBriefMode;
    @ViewById(2131558772)
    WorksCoverView mCover;
    @ViewById(2131558966)
    View mDividerToLeftOfWorksStatus;
    @ViewById(2131558903)
    ImageView mIconGift;
    @ViewById(2131558968)
    TextView mOwnedDate;
    @ViewById(2131558462)
    TextView mTitle;
    @ViewById(2131558965)
    WorksStatusView mWorksStatusView;
    @ViewById(2131558560)
    WorksUGCView mWorksUCGView;

    public WorksItemView(Context context) {
        super(context);
        this.mBriefMode = false;
    }

    public WorksItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mBriefMode = false;
    }

    public WorksItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mBriefMode = false;
    }

    @AfterViews
    void init() {
        ViewUtils.setEventAware(this);
        this.mWorksUCGView.setMode(1);
    }

    public void setBriefMode(boolean briefMode) {
        this.mBriefMode = briefMode;
    }

    public void bindData(Works works) {
        this.mTitle.setText(works.title);
        if (DebugSwitch.on(Key.APP_DEBUG_SHOW_BOOK_IDS)) {
            this.mTitle.setText(String.format("%s %s", new Object[]{Integer.valueOf(works.id), works.title}));
        }
        this.mAuthors.setText(Res.getString(R.string.title_author, works.author));
        this.mWorksStatusView.worksId(works.id);
        if (this.mBriefMode) {
            ViewUtils.gone(this.mCover, this.mOwnedDate, this.mWorksUCGView, this.mIconGift);
            ViewUtils.visible(this.mWorksStatusView, this.mDividerToLeftOfWorksStatus);
            this.mTitle.setTypeface(Font.SANS_SERIF);
            this.mTitle.setMaxLines(AdvancedShareActionProvider.WEIGHT_MAX);
            return;
        }
        ViewUtils.visible(this.mCover, this.mOwnedDate, this.mWorksUCGView);
        ViewUtils.gone(this.mWorksStatusView, this.mDividerToLeftOfWorksStatus);
        this.mTitle.setMaxLines(2);
        this.mTitle.setTypeface(Font.SANS_SERIF_BOLD);
        this.mCover.works(works).noLabel();
        this.mWorksUCGView.worksId(works.id);
        this.mOwnedDate.setText(works.formatOwnedInfo());
        ViewUtils.showIf(works.isGift(), this.mIconGift);
    }
}
