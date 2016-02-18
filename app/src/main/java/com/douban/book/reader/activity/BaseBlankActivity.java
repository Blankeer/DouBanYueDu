package com.douban.book.reader.activity;

import android.os.Bundle;
import android.view.MotionEvent;
import com.douban.book.reader.R;

public class BaseBlankActivity extends BaseActivity {
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar_Fullscreen_Translucent);
        super.onCreate(savedInstanceState);
    }

    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }

    public boolean shouldBeConsideredAsAPage() {
        return false;
    }
}
