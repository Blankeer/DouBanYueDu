package com.douban.book.reader.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.douban.book.reader.R;
import com.douban.book.reader.manager.SearchHistoryManager_;
import com.douban.book.reader.manager.WorksManager_;
import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.builder.PostActivityStarter;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class StoreSearchActivity_ extends StoreSearchActivity implements HasViews {
    private final OnViewChangedNotifier onViewChangedNotifier_;

    public static class IntentBuilder_ extends ActivityIntentBuilder<IntentBuilder_> {
        private Fragment fragmentSupport_;
        private android.app.Fragment fragment_;

        public IntentBuilder_(Context context) {
            super(context, StoreSearchActivity_.class);
        }

        public IntentBuilder_(android.app.Fragment fragment) {
            super(fragment.getActivity(), StoreSearchActivity_.class);
            this.fragment_ = fragment;
        }

        public IntentBuilder_(Fragment fragment) {
            super(fragment.getActivity(), StoreSearchActivity_.class);
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

    public StoreSearchActivity_() {
        this.onViewChangedNotifier_ = new OnViewChangedNotifier();
    }

    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(this.onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
        setContentView((int) R.layout.act_general);
    }

    private void init_(Bundle savedInstanceState) {
        this.mSearchManager = (SearchManager) getSystemService("search");
        this.mWorksManager = WorksManager_.getInstance_(this);
        this.mSearchHistoryManager = SearchHistoryManager_.getInstance_(this);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.store_search, menu);
        this.mMenuSearch = menu.findItem(R.id.action_search);
        return super.onCreateOptionsMenu(menu);
    }
}
