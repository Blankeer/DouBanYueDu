package com.douban.book.reader.manager;

import com.douban.book.reader.constant.Key;
import com.douban.book.reader.util.Analysis;
import com.douban.book.reader.util.Pref;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

@EBean(scope = Scope.Singleton)
public class FontScaleManager {
    private static final int SCALE_COUNT = 4;
    private int mScale;

    public FontScaleManager() {
        this.mScale = -1;
    }

    public void setScale(int scale) {
        if (scale >= 0 && scale <= 3) {
            this.mScale = scale;
            Pref.ofApp().set(Key.SETTING_SCALE, Integer.valueOf(scale));
            Analysis.sendPrefChangedEvent("font_scale", Integer.valueOf(scale));
        }
    }

    public int getScale() {
        if (this.mScale < 0) {
            this.mScale = Pref.ofApp().getInt(Key.SETTING_SCALE, 1);
        }
        this.mScale = Math.min(this.mScale, 3);
        this.mScale = Math.max(this.mScale, 0);
        return this.mScale;
    }

    public int getScaleCount() {
        return SCALE_COUNT;
    }
}
