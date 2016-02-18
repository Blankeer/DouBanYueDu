package com.douban.book.reader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.event.ArkRequest;
import com.douban.book.reader.fragment.DoubanAccountOperationFragment.Action;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class DoubanAccountOperationFragment_ extends DoubanAccountOperationFragment implements HasViews {
    public static final String ACTION_ARG = "action";
    public static final String INTENT_TO_START_AFTER_LOGIN_ARG = "intentToStartAfterLogin";
    public static final String REQUEST_TO_SEND_AFTER_LOGIN_ARG = "requestToSendAfterLogin";
    private View contentView_;
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public static class FragmentBuilder_ extends FragmentBuilder<FragmentBuilder_, DoubanAccountOperationFragment> {
        public DoubanAccountOperationFragment build() {
            DoubanAccountOperationFragment_ fragment_ = new DoubanAccountOperationFragment_();
            fragment_.setArguments(this.args);
            return fragment_;
        }

        public FragmentBuilder_ action(Action action) {
            this.args.putSerializable(DoubanAccountOperationFragment_.ACTION_ARG, action);
            return this;
        }

        public FragmentBuilder_ intentToStartAfterLogin(Intent intentToStartAfterLogin) {
            this.args.putParcelable(DoubanAccountOperationFragment_.INTENT_TO_START_AFTER_LOGIN_ARG, intentToStartAfterLogin);
            return this;
        }

        public FragmentBuilder_ requestToSendAfterLogin(ArkRequest requestToSendAfterLogin) {
            this.args.putSerializable(DoubanAccountOperationFragment_.REQUEST_TO_SEND_AFTER_LOGIN_ARG, requestToSendAfterLogin);
            return this;
        }
    }

    public DoubanAccountOperationFragment_() {
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
            if (args_.containsKey(ACTION_ARG)) {
                this.action = (Action) args_.getSerializable(ACTION_ARG);
            }
            if (args_.containsKey(INTENT_TO_START_AFTER_LOGIN_ARG)) {
                this.intentToStartAfterLogin = (Intent) args_.getParcelable(INTENT_TO_START_AFTER_LOGIN_ARG);
            }
            if (args_.containsKey(REQUEST_TO_SEND_AFTER_LOGIN_ARG)) {
                this.requestToSendAfterLogin = (ArkRequest) args_.getSerializable(REQUEST_TO_SEND_AFTER_LOGIN_ARG);
            }
        }
    }
}
