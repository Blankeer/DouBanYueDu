package com.douban.book.reader.fragment;

import android.support.v4.app.Fragment;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.adapter.ViewBinderAdapter;
import com.douban.book.reader.entity.Review;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.event.ReviewChangedEvent;
import com.douban.book.reader.manager.Lister;
import com.douban.book.reader.manager.ReviewManager;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.view.MyReviewView;
import com.douban.book.reader.view.MyReviewView_;
import com.douban.book.reader.view.ReviewItemView_;
import com.mcxiaoke.next.ui.endless.EndlessListView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;

@EFragment
public class ReviewListFragment extends BaseEndlessListFragment<Review> {
    private MyReviewView mMyReviewView;
    private Works mWorks;
    @FragmentArg
    int worksId;

    public Lister<Review> onCreateLister() {
        return ReviewManager.getInstance().listForWorks(this.worksId);
    }

    public BaseArrayAdapter<Review> onCreateAdapter() {
        return new ViewBinderAdapter(getActivity(), ReviewItemView_.class);
    }

    @AfterViews
    void init() {
        updateTitle();
        loadWorks();
    }

    public void onEventMainThread(ReviewChangedEvent event) {
        if (event.isValidForWorks(this.worksId)) {
            refreshSilently();
        }
    }

    protected void onListViewCreated(EndlessListView listView) {
        setListTitle(Res.getString(R.string.title_more_reviews));
        this.mMyReviewView = MyReviewView_.build(getActivity());
        addHeaderView(this.mMyReviewView);
        setEmptyHint((int) R.string.hint_empty_review);
    }

    @Background
    void loadWorks() {
        try {
            this.mWorks = (Works) WorksManager.getInstance().get((Object) Integer.valueOf(this.worksId));
            updateTitle();
            updateMyReviewView();
        } catch (DataLoadException e) {
            Logger.e(this.TAG, e);
            ToastUtils.showToast((int) R.string.toast_general_load_failed);
            finish();
        }
    }

    @UiThread
    void updateMyReviewView() {
        this.mMyReviewView.worksId(this.worksId);
    }

    @UiThread
    void updateTitle() {
        setTitle(getTitleStr());
    }

    @ItemClick({2131558593})
    public void onItemClick(Review review) {
        if (review != null) {
            ReviewDetailFragment_.builder().reviewId(review.id).worksId(this.worksId).build().showAsActivity((Fragment) this);
        }
    }

    private String getTitleStr() {
        if (this.mWorks == null) {
            return Res.getString(R.string.title_all_reviews);
        }
        return Res.getString(R.string.title_all_reviews_for_works, this.mWorks.title);
    }
}
