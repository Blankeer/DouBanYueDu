package com.douban.book.reader.fragment.share;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ShareUrlEditFragment_ extends ShareUrlEditFragment implements HasViews {
    public static final String DEFAULT_TITLE_ARG = "defaultTitle";
    public static final String URL_ARG = "url";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, ShareUrlEditFragment> {
        public ShareUrlEditFragment build() {
            ShareUrlEditFragment_ fragment_ = new ShareUrlEditFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ defaultTitle(String defaultTitle) {
            this.args.putString(ShareUrlEditFragment_.DEFAULT_TITLE_ARG, defaultTitle);
            return this;
        }

        public FragmentBuilder_ url(String url) {
            this.args.putString(ShareUrlEditFragment_.URL_ARG, url);
            return this;
        }
    }

    public ShareUrlEditFragment_() {
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
            if (args_.containsKey(DEFAULT_TITLE_ARG)) {
                this.defaultTitle = args_.getString(DEFAULT_TITLE_ARG);
            }
            if (args_.containsKey(URL_ARG)) {
                this.url = args_.getString(URL_ARG);
            }
        }
    }
}
