package com.douban.book.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import io.realm.internal.Table;
import org.androidannotations.api.BackgroundExecutor;
import org.androidannotations.api.BackgroundExecutor.Task;
import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.builder.PostActivityStarter;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class WeixinAuthActivity_ extends WeixinAuthActivity implements HasViews {
    private final OnViewChangedNotifier onViewChangedNotifier_;

    /* renamed from: com.douban.book.reader.activity.WeixinAuthActivity_.1 */
    class AnonymousClass1 extends Task {
        final /* synthetic */ String val$code;

        AnonymousClass1(String x0, long x1, String x2, String str) {
            this.val$code = str;
            super(x0, x1, x2);
        }

        public void execute() {
            try {
                super.getOpenId(this.val$code);
            } catch (Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public static class IntentBuilder_ extends ActivityIntentBuilder<IntentBuilder_> {
        private Fragment fragmentSupport_;
        private android.app.Fragment fragment_;

        public IntentBuilder_(Context context) {
            super(context, WeixinAuthActivity_.class);
        }

        public IntentBuilder_(android.app.Fragment fragment) {
            super(fragment.getActivity(), WeixinAuthActivity_.class);
            this.fragment_ = fragment;
        }

        public IntentBuilder_(Fragment fragment) {
            super(fragment.getActivity(), WeixinAuthActivity_.class);
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
    }

    public WeixinAuthActivity_() {
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
    }

    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    private void init_(Bundle savedInstanceState) {
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

    void getOpenId(String code) {
        BackgroundExecutor.execute(new AnonymousClass1(Table.STRING_DEFAULT_VALUE, 0, Table.STRING_DEFAULT_VALUE, code));
    }
}
