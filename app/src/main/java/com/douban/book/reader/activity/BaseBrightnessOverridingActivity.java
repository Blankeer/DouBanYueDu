package com.douban.book.reader.activity;

import android.os.Bundle;
import com.douban.book.reader.event.BrightnessChangedEvent;
import com.douban.book.reader.helper.BrightnessHelper;

public class BaseBrightnessOverridingActivity extends BaseActivity {
    private BrightnessHelper mBrightnessHelper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mBrightnessHelper = new BrightnessHelper();
    }

    public void onEventMainThread(BrightnessChangedEvent event) {
        this.mBrightnessHelper.applyBrightness(getWindow());
    }

    public void onResume() {
        super.onResume();
        this.mBrightnessHelper.applyBrightness(getWindow());
    }
}
