package com.douban.book.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import com.douban.book.reader.R;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.theme.Theme;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;

public class OverlayToolbar extends Toolbar {
    private static final float BADGE_CENTER_X;
    private static final float BADGE_CENTER_Y;
    private static final int BADGE_RADIUS_INNER;
    private static final int BADGE_RADIUS_OUTER;
    private static final int MASK_COLOR;
    private boolean mShowBadge;

    static {
        MASK_COLOR = Res.getColorOverridingAlpha(17170444, ReadViewPager.EDGE_RATIO);
        BADGE_CENTER_X = (float) Utils.dp2pixel(38.0f);
        BADGE_CENTER_Y = (float) Utils.dp2pixel(20.0f);
        BADGE_RADIUS_OUTER = Utils.dp2pixel(6.0f);
        BADGE_RADIUS_INNER = Utils.dp2pixel(5.0f);
    }

    public OverlayToolbar(Context context) {
        super(context);
        init();
    }

    public OverlayToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OverlayToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        updatePopupTheme();
        setWillNotDraw(false);
        ViewUtils.setEventAware(this);
    }

    public void setShowBadge(boolean showBadge) {
        this.mShowBadge = showBadge;
        invalidate();
    }

    public void onEventMainThread(ColorThemeChangedEvent event) {
        updatePopupTheme();
        invalidate();
    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (Theme.isNight()) {
            canvas.drawColor(MASK_COLOR);
        }
        if (this.mShowBadge) {
            Paint paint = PaintUtils.obtainPaint();
            paint.setColor(Res.getColor(R.array.light_stroke_color));
            canvas.drawCircle(BADGE_CENTER_X, BADGE_CENTER_Y, (float) BADGE_RADIUS_OUTER, paint);
            paint.setColor(Res.getColor(R.array.red));
            canvas.drawCircle(BADGE_CENTER_X, BADGE_CENTER_Y, (float) BADGE_RADIUS_INNER, paint);
            PaintUtils.recyclePaint(paint);
        }
    }

    private void updatePopupTheme() {
        setPopupTheme(Theme.isNight() ? com.mcxiaoke.next.ui.R.style.AppBaseTheme : R.style.AppBaseTheme_Light);
    }
}
