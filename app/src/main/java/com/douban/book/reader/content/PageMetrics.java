package com.douban.book.reader.content;

import android.app.Activity;
import android.view.View;
import com.douban.book.reader.app.App;
import com.douban.book.reader.manager.FontScaleManager_;

public class PageMetrics {
    private static PageMetrics sLast;
    public int fontScale;
    public float height;
    public float width;

    static {
        sLast = getDefault();
    }

    public String toString() {
        return String.format("%sx%s@%s", new Object[]{Float.valueOf(this.width), Float.valueOf(this.height), Integer.valueOf(this.fontScale)});
    }

    public static PageMetrics getDefault() {
        PageMetrics pageMetrics = new PageMetrics();
        pageMetrics.width = (float) App.get().getPageWidth();
        pageMetrics.height = (float) App.get().getPageHeight();
        pageMetrics.fontScale = FontScaleManager_.getInstance_(App.get()).getScale();
        sLast = pageMetrics;
        return pageMetrics;
    }

    public static PageMetrics getFromActivity(Activity activity) {
        try {
            View contentView = activity.getWindow().getDecorView().findViewById(16908290);
            PageMetrics pageMetrics = new PageMetrics();
            int width = contentView.getWidth();
            int height = contentView.getHeight();
            if (width <= 0 || height <= 0) {
                throw new IllegalStateException("ContentView size unknown.");
            }
            pageMetrics.width = (float) width;
            pageMetrics.height = (float) height;
            pageMetrics.fontScale = FontScaleManager_.getInstance_(App.get()).getScale();
            sLast = pageMetrics;
            return pageMetrics;
        } catch (Throwable th) {
            return getDefault();
        }
    }

    public static PageMetrics getLast() {
        return sLast;
    }
}
