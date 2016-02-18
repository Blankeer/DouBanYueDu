package com.douban.book.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.douban.book.reader.R;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ThemedUtils;
import com.douban.book.reader.util.Utils;

public class KnotPageTopView extends BaseThemedView {
    public KnotPageTopView(Context context) {
        super(context);
    }

    public KnotPageTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KnotPageTopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Res.getColor(R.array.page_bg_color));
        float top = (float) (getHeight() - Utils.dp2pixel(5.0f));
        float bottom = (float) getHeight();
        float right = (float) getWidth();
        Paint paint = PaintUtils.obtainPaint();
        paint.setStyle(Style.FILL_AND_STROKE);
        paint.setColor(Res.getColor(R.array.red));
        PaintUtils.applyNightModeMaskIfNeeded(paint);
        canvas.drawRect(0.0f, top, right, bottom, paint);
        float drawableTop = ((float) getHeight()) * 0.6f;
        Drawable knot = Res.getDrawable(R.drawable.ic_bow);
        ThemedUtils.setAutoDimInNightMode(knot);
        CanvasUtils.drawDrawableCenteredInArea(canvas, knot, new RectF(0.0f, drawableTop, right, bottom));
        PaintUtils.recyclePaint(paint);
    }
}
