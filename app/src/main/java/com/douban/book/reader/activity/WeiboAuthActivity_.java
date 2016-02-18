package com.douban.book.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.builder.PostActivityStarter;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class WeiboAuthActivity_ extends WeiboAuthActivity implements HasViews {
    public static final String INTENT_TO_FORWARD_AFTER_AUTH_EXTRA = "intentToForwardAfterAuth";
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public static class IntentBuilder_ extends ActivityIntentBuilder<IntentBuilder_> {
        private Fragment fragmentSupport_;
        private android.app.Fragment fragment_;

        public IntentBuilder_(Context context) {
            super(context, WeiboAuthActivity_.class);
        }

        public IntentBuilder_(android.app.Fragment fragment) {
            super(fragment.getActivity(), WeiboAuthActivity_.class);
            this.fragment_ = fragment;
        }

        public IntentBuilder_(Fragment fragment) {
            super(fragment.getActivity(), WeiboAuthActivity_.class);
            this.fragmentSupport_ = fragment;
        }

        public PostActivityStarter startForResult(int requestCode) {
            if (this.fragmentSupport_ != null) {
                this.fragmentSupport_.startActivityForResult(this.intent, requestCode);
            } else if (this.fragment_ != null) {
                if (VERSION.SDK_INT >= 16) {
                    this.fragment_.startActivityForResult(this.intent, requestCode, this.lastOptions);
                } else {
                    this.fragment_.startActivityForResult(this.intent, requestCode);
                }
            } else if (this.context instanceof Activity) {
                ActivityCompat.startActivityForResult(this.context, this.intent, requestCode, this.lastOptions);
            } else if (VERSION.SDK_INT >= 16) {
                this.context.startActivity(this.intent, this.lastOptions);
            } else {
                this.context.startActivity(this.intent);
            }
            return new PostActivityStarter(this.context);
        }

        public IntentBuilder_ intentToForwardAfterAuth(Intent intentToForwardAfterAuth) {
            return (IntentBuilder_) super.extra(WeiboAuthActivity_.INTENT_TO_FORWARD_AFTER_AUTH_EXTRA, (Parcelable) intentToForwardAfterAuth);
        }
    }

    public WeiboAuthActivity_() {
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
    }

    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    private void init_(Bundle savedInstanceState) {
        injectExtras_();
    }

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public void setContentView(View view) {
        super.setContentView(view);
        this.onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static IntentBuilder_ intent(Context context) {
        return new IntentBuilder_(context);
    }

    public static IntentBuilder_ intent(android.app.Fragment fragment) {
        return new IntentBuilder_(fragment);
    }

    public static IntentBuilder_ intent(Fragment supportFragment) {
        return new IntentBuilder_(supportFragment);
    }

    private void injectExtras_() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_ != null && extras_.containsKey(INTENT_TO_FORWARD_AFTER_AUTH_EXTRA)) {
            this.intentToForwardAfterAuth = (Intent) extras_.getParcelable(INTENT_TO_FORWARD_AFTER_AUTH_EXTRA);
        }
    }

    public void setIntent(Intent newIntent) {
        super.setIntent(newIntent);
        injectExtras_();
    }
}
