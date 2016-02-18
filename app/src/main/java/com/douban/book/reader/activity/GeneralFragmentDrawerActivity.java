package com.douban.book.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.douban.book.reader.R;
import com.douban.book.reader.fragment.BaseFragment;
import com.douban.book.reader.panel.Transaction;
import com.douban.book.reader.util.FragmentInstanceCache;

public class GeneralFragmentDrawerActivity extends BaseDrawerActivity {
    Fragment mFragment;

    public GeneralFragmentDrawerActivity() {
        this.mFragment = null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setShowDrawerToggle(false);
        Intent intent = getIntent();
        if (intent != null) {
            String key = intent.getStringExtra(GeneralFragmentActivity.KEY_FRAGMENT);
            boolean showActionBar = intent.getBooleanExtra(GeneralFragmentActivity.KEY_SHOW_ACTION_BAR, true);
            this.mFragment = FragmentInstanceCache.pop(key);
            if (this.mFragment != null) {
                if (!showActionBar) {
                    hideActionBar();
                }
                Bundle additionalArgs = intent.getBundleExtra(GeneralFragmentActivity.KEY_ADDITIONAL_ARGS_FOR_FRAGMENT);
                if (!(additionalArgs == null || additionalArgs.isEmpty())) {
                    Bundle args = this.mFragment.getArguments();
                    if (args == null) {
                        args = new Bundle();
                    }
                    args.putAll(additionalArgs);
                    this.mFragment.setArguments(args);
                }
                Transaction.begin((FragmentActivity) this, (int) R.id.frag_container).replace(this.mFragment).commit();
            }
        }
    }

    public void onBackPressed() {
        if (this.mFragment instanceof BaseFragment) {
            ((BaseFragment) this.mFragment).onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    public boolean shouldFinish() {
        if (this.mFragment instanceof BaseFragment) {
            return ((BaseFragment) this.mFragment).shouldFinish();
        }
        return super.shouldFinish();
    }

    public boolean shouldBeConsideredAsAPage() {
        return false;
    }
}
