package com.douban.book.reader.fragment.share;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.manager.ReviewManager_;
import com.douban.book.reader.manager.WorksManager_;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ShareReviewEditFragment_ extends ShareReviewEditFragment implements HasViews {
    public static final String REVIEW_ID_ARG = "reviewId";
    public static final String WORKS_ID_ARG = "worksId";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, ShareReviewEditFragment> {
        public ShareReviewEditFragment build() {
            ShareReviewEditFragment_ fragment_ = new ShareReviewEditFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ reviewId(int reviewId) {
            this.args.putInt(ShareReviewEditFragment_.REVIEW_ID_ARG, reviewId);
            return this;
        }

        public FragmentBuilder_ worksId(int worksId) {
            this.args.putInt(ShareReviewEditFragment_.WORKS_ID_ARG, worksId);
            return this;
        }
    }

    public ShareReviewEditFragment_() {
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
        this.mWorksManager = WorksManager_.getInstance_(getActivity());
        this.mReviewManager = ReviewManager_.getInstance_(getActivity());
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
}
