package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Comment;
import com.douban.book.reader.manager.ReviewManager_;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.UiThreadExecutor;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ReviewDetailFragment_ extends ReviewDetailFragment implements HasViews {
    public static final String REVIEW_ID_ARG = "reviewId";
    public static final String WORKS_ID_ARG = "worksId";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.fragment.ReviewDetailFragment_.2 */
    class AnonymousClass2 extends Task {
        AnonymousClass2(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.deleteReview();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.fragment.ReviewDetailFragment_.3 */
    class AnonymousClass3 extends Task {
        final /* synthetic */ Comment val$comment;

        AnonymousClass3(String x0, long x1, String x2, Comment comment) {
            this.val$comment = comment;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.deleteComment(this.val$comment);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    /* renamed from: com.douban.book.reader.fragment.ReviewDetailFragment_.4 */
    class AnonymousClass4 extends Task {
        AnonymousClass4(String x0, long x1, String x2) {
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.loadData();
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, ReviewDetailFragment> {
        public ReviewDetailFragment build() {
            ReviewDetailFragment_ fragment_ = new ReviewDetailFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ reviewId(int reviewId) {
            this.args.putInt(ReviewDetailFragment_.REVIEW_ID_ARG, reviewId);
            return this;
        }

        public FragmentBuilder_ worksId(int worksId) {
            this.args.putInt(ReviewDetailFragment_.WORKS_ID_ARG, worksId);
            return this;
        }
    }

    public ReviewDetailFragment_() {
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
    }

    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    public View findViewById(int id) {
        if (this.contentView_ == null) {
            return null;
        }
        return this.contentView_.findViewById(id);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.contentView_ = super.onCreateView(inflater, container, savedInstanceState);
        return this.contentView_;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.contentView_ = null;
    }

    private void init_(Bundle savedInstanceState) {
        injectFragmentArguments_();
        this.mReviewManager = ReviewManager_.getInstance_(getActivity());
        setHasOptionsMenu(true);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static FragmentBuilder_ builder() {
        return new FragmentBuilder_();
    }

    private void injectFragmentArguments_() {
        Bundle args_ = getArguments();
        if (args_ != null) {
            if (args_.containsKey(REVIEW_ID_ARG)) {
                this.reviewId = args_.getInt(REVIEW_ID_ARG);
            }
            if (args_.containsKey(WORKS_ID_ARG)) {
                this.worksId = args_.getInt(WORKS_ID_ARG);
            }
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.review_detail, menu);
        this.mMenuItemCreate = menu.findItem(R.id.action_create);
        this.mMenuItemShare = menu.findItem(R.id.action_share);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId_ = item.getItemId();
        if (itemId_ == R.id.action_create) {
            onMenuItemCreateClicked();
            return true;
        } else if (itemId_ == R.id.action_share) {
            onMenuItemShareClicked();
            return true;
        } else if (itemId_ == R.id.action_edit) {
            onMenuItemEditClicked();
            return true;
        } else if (itemId_ != R.id.action_delete) {
            return super.onOptionsItemSelected(item);
        } else {
            onMenuItemDeleteClicked();
            return true;
        }
    }

    void updateHeaderView() {
        UiThreadExecutor.runTask(Table.STRING_DEFAULT_VALUE, new Runnable() {
            public void run() {
                super.updateHeaderView();
            }
        }, 0);
    }

    void deleteReview() {
        BackgroundExecutor.execute(new AnonymousClass2(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }

    void deleteComment(Comment comment) {
        BackgroundExecutor.execute(new AnonymousClass3(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, comment));
    }

    void loadData() {
        BackgroundExecutor.execute(new AnonymousClass4(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE));
    }
}
