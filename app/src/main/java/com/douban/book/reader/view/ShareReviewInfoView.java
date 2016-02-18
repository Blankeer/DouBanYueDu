package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Review;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.manager.ReviewManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903186)
public class ShareReviewInfoView extends LinearLayout {
    private static final String TAG;
    private Review mReview;
    @ViewById(2131558935)
    TextView mReviewContent;
    private int mReviewId;
    @ViewById(2131558462)
    TextView mTitle;
    private Works mWorks;
    private int mWorksId;

    static {
        TAG = ShareReviewInfoView.class.getSimpleName();
    }

    public ShareReviewInfoView(Context context) {
        super(context);
    }

    public ShareReviewInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShareReviewInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ShareReviewInfoView reviewId(int reviewId) {
        this.mReviewId = reviewId;
        return this;
    }

    public ShareReviewInfoView worksId(int worksId) {
        this.mWorksId = worksId;
        return this;
    }

    public ShareReviewInfoView commit() {
        loadData();
        return this;
    }

    @AfterViews
    void init() {
        setOrientation(1);
    }

    @UiThread
    void updateViews() {
        if (!(this.mReview == null || this.mWorks == null)) {
            this.mTitle.setText(Res.getString(R.string.msg_review_works, this.mReview.getAuthorName(), this.mWorks.title));
        }
        if (this.mReview != null) {
            this.mReviewContent.setText(this.mReview.content);
        }
    }

    @Background
    void loadData() {
        try {
            this.mReview = (Review) ReviewManager.getInstance().get((Object) Integer.valueOf(this.mReviewId));
            this.mWorks = WorksManager.getInstance().getWorks(this.mWorksId);
            updateViews();
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
    }
}
