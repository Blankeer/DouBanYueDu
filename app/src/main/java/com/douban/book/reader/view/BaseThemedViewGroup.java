package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.util.ViewUtils;

public abstract class BaseThemedViewGroup extends ViewGroup {
    public BaseThemedViewGroup(Context context) {
        super(context);
        init();
    }

    public BaseThemedViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseThemedViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ViewUtils.setEventAware(this);
    }

    public void onEventMainThread(ColorThemeChangedEvent event) {
        onColorThemeChanged();
        invalidate();
    }

    protected void onColorThemeChanged() {
    }
}
