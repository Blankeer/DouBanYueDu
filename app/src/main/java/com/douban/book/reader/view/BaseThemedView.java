package com.douban.book.reader.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.util.ViewUtils;

public class BaseThemedView extends View {
    public BaseThemedView(Context context) {
        super(context);
        init();
    }

    public BaseThemedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseThemedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ViewUtils.setEventAware(this);
    }

    public void onEventMainThread(ColorThemeChangedEvent event) {
        invalidate();
    }
}
