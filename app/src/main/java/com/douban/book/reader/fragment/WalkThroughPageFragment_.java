package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.douban.book.reader.R;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class WalkThroughPageFragment_ extends WalkThroughPageFragment implements HasViews, OnViewChangedListener {
    public static final String PAGE_IMG_RES_ID_ARG = "pageImgResId";
    public static final String SHOW_CLOSE_BTN_ARG = "showCloseBtn";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, WalkThroughPageFragment> {
        public WalkThroughPageFragment build() {
            WalkThroughPageFragment_ fragment_ = new WalkThroughPageFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ showCloseBtn(boolean showCloseBtn) {
            this.args.putBoolean(WalkThroughPageFragment_.SHOW_CLOSE_BTN_ARG, showCloseBtn);
            return this;
        }

        public FragmentBuilder_ pageImgResId(int pageImgResId) {
            this.args.putInt(WalkThroughPageFragment_.PAGE_IMG_RES_ID_ARG, pageImgResId);
            return this;
        }
    }

    public WalkThroughPageFragment_() {
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
        if (this.contentView_ == null) {
            this.contentView_ = inflater.inflate(R.layout.frag_walk_through_page, container, false);
        }
        return this.contentView_;
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.contentView_ = null;
        this.mPageImg = null;
        this.mBtnClose = null;
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        injectFragmentArguments_();
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static FragmentBuilder_ builder() {
        return new FragmentBuilder_();
    }

    public void onViewChanged(HasViews hasViews) {
        this.mPageImg = (ImageView) hasViews.findViewById(R.id.page_img);
        this.mBtnClose = (Button) hasViews.findViewById(R.id.btn_close);
        if (this.mBtnClose != null) {
            this.mBtnClose.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    WalkThroughPageFragment_.this.onCloseBtnClicked();
                }
            });
        }
        init();
    }

    private void injectFragmentArguments_() {
        Bundle args_ = getArguments();
        if (args_ != null) {
            if (args_.containsKey(SHOW_CLOSE_BTN_ARG)) {
                this.showCloseBtn = args_.getBoolean(SHOW_CLOSE_BTN_ARG);
            }
            if (args_.containsKey(PAGE_IMG_RES_ID_ARG)) {
                this.pageImgResId = args_.getInt(PAGE_IMG_RES_ID_ARG);
            }
        }
    }
}
