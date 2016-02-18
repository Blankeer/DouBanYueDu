package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.manager.GiftPackManager_;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class GiftMessageEditFragment_ extends GiftMessageEditFragment implements HasViews, OnViewChangedListener {
    public static final String MESSAGE_ARG = "message";
    public static final String PACK_ID_ARG = "packId";
    public static final String SELECTED_ARG = "selected";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, GiftMessageEditFragment> {
        public GiftMessageEditFragment build() {
            GiftMessageEditFragment_ fragment_ = new GiftMessageEditFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ message(String message) {
            this.args.putString(GiftMessageEditFragment_.MESSAGE_ARG, message);
            return this;
        }

        public FragmentBuilder_ selected(boolean selected) {
            this.args.putBoolean(GiftMessageEditFragment_.SELECTED_ARG, selected);
            return this;
        }

        public FragmentBuilder_ packId(int packId) {
            this.args.putInt(GiftMessageEditFragment_.PACK_ID_ARG, packId);
            return this;
        }
    }

    public GiftMessageEditFragment_() {
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
        this.mGiftPackManager = GiftPackManager_.getInstance_(getActivity());
        OnViewChangedNotifier.registerOnViewChangedListener(this);
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
            if (args_.containsKey(MESSAGE_ARG)) {
                this.message = args_.getString(MESSAGE_ARG);
            }
            if (args_.containsKey(SELECTED_ARG)) {
                this.selected = args_.getBoolean(SELECTED_ARG);
            }
            if (args_.containsKey(PACK_ID_ARG)) {
                this.packId = args_.getInt(PACK_ID_ARG);
            }
        }
    }

    public void onViewChanged(HasViews hasViews) {
        init();
    }
}
