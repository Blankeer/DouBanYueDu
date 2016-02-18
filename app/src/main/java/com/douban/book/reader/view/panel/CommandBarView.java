package com.douban.book.reader.view.panel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.event.ArkRequest;
import com.douban.book.reader.event.EventBusUtils;
import com.douban.book.reader.fragment.ReviewListFragment_;
import com.douban.book.reader.fragment.TocFragment_;
import com.douban.book.reader.fragment.WorksUgcFragment_;
import com.douban.book.reader.util.RichText;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903137)
public class CommandBarView extends RelativeLayout {
    @ViewById(2131558815)
    TextView mBtnAnnotation;
    @ViewById(2131558816)
    TextView mBtnReview;
    @ViewById(2131558817)
    TextView mBtnSetting;
    @ViewById(2131558814)
    TextView mBtnToc;
    @ViewById(2131558818)
    SeekBarView mSeekBarView;
    private int mWorksId;

    public CommandBarView(Context context) {
        super(context);
    }

    public CommandBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommandBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setWorksId(int worksId) {
        this.mWorksId = worksId;
        this.mSeekBarView.setWorksId(worksId);
    }

    @AfterViews
    void init() {
        this.mBtnToc.setText(RichText.textWithIcon((int) R.drawable.v_toc, (int) R.string.command_btn_text_contents));
        this.mBtnAnnotation.setText(RichText.textWithIcon((int) R.drawable.v_annotation, (int) R.string.command_btn_text_notes));
        this.mBtnReview.setText(RichText.textWithIcon((int) R.drawable.v_review, (int) R.string.command_btn_text_reviews));
        this.mBtnSetting.setText(RichText.singleIcon(R.drawable.v_setting));
    }

    @Click({2131558814})
    void onBtnContentsClicked() {
        if (this.mWorksId > 0) {
            TocFragment_.builder().worksId(this.mWorksId).build().setDrawerEnabled(false).showAsActivity(this, 1073741824);
        }
    }

    @Click({2131558816})
    void onBtnReviewsClicked() {
        if (this.mWorksId > 0) {
            ReviewListFragment_.builder().worksId(this.mWorksId).build().showAsActivity((View) this);
        }
    }

    @Click({2131558815})
    void onBtnNotesClicked() {
        if (this.mWorksId > 0) {
            WorksUgcFragment_.builder().worksId(this.mWorksId).build().setDrawerEnabled(false).showAsActivity(this, 1073741824);
        }
    }

    @Click({2131558817})
    void onBtnSettingsClicked() {
        EventBusUtils.post(ArkRequest.READER_PANEL_OPEN_SETTINGS_PANEL);
    }
}
