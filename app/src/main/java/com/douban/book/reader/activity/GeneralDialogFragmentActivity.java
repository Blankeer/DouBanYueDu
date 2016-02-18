package com.douban.book.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.douban.book.reader.fragment.BaseDialogFragment;
import com.douban.book.reader.util.FragmentInstanceCache;

public class GeneralDialogFragmentActivity extends BaseBlankActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            Fragment fragment = FragmentInstanceCache.pop(intent.getStringExtra(GeneralFragmentActivity.KEY_FRAGMENT));
            if (fragment instanceof BaseDialogFragment) {
                ((BaseDialogFragment) fragment).show(getSupportFragmentManager(), this.TAG);
                return;
            } else {
                finish();
                return;
            }
        }
        finish();
    }
}
