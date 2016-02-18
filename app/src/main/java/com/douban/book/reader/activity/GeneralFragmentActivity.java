package com.douban.book.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.douban.book.reader.R;
import com.douban.book.reader.fragment.BaseFragment;
import com.douban.book.reader.panel.Transaction;
import com.douban.book.reader.util.BundleUtils;
import com.douban.book.reader.util.FragmentInstanceCache;

public final class GeneralFragmentActivity extends BaseActivity {
    public static final String KEY_ADDITIONAL_ARGS_FOR_FRAGMENT = "key_additional_args_for_fragment";
    public static final String KEY_FRAGMENT = "key_fragment";
    public static final String KEY_SHOW_ACTION_BAR = "key_show_action_bar";
    private Fragment mFragment;

    public GeneralFragmentActivity() {
        this.mFragment = null;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.act_general);
        Intent intent = getIntent();
        if (intent != null) {
            String key = intent.getStringExtra(KEY_FRAGMENT);
            boolean showActionBar = intent.getBooleanExtra(KEY_SHOW_ACTION_BAR, true);
            this.mFragment = FragmentInstanceCache.pop(key);
            if (this.mFragment != null) {
                if (!showActionBar) {
                    hideActionBar();
                }
                Bundle additionalArgs = intent.getBundleExtra(KEY_ADDITIONAL_ARGS_FOR_FRAGMENT);
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

    public static void putAdditionalArgs(Intent intent, String key, Object value) {
        if (intent != null) {
            Bundle extra = intent.getExtras();
            if (extra != null) {
                Bundle additionalArgs = extra.getBundle(KEY_ADDITIONAL_ARGS_FOR_FRAGMENT);
                if (additionalArgs == null) {
                    additionalArgs = new Bundle();
                }
                BundleUtils.put(additionalArgs, key, value);
                intent.putExtra(KEY_ADDITIONAL_ARGS_FOR_FRAGMENT, additionalArgs);
            }
        }
    }
}
