package com.douban.book.reader.lib.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.Utils;

public class StrokeTextView extends TextView {
    private int mStrokeColor;
    private int mStrokeWidth;

    public StrokeTextView(Context context) {
        super(context);
        this.mStrokeColor = Res.getColor(R.color.white);
        this.mStrokeWidth = Utils.dp2pixel(2.0f);
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mStrokeColor = Res.getColor(R.color.white);
        this.mStrokeWidth = Utils.dp2pixel(2.0f);
    }

    public StrokeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mStrokeColor = Res.getColor(R.color.white);
        this.mStrokeWidth = Utils.dp2pixel(2.0f);
    }

    protected void onDraw(Canvas canvas) {
        ColorStateList textColor = getTextColors();
        TextPaint paint = getPaint();
        PaintUtils.applyNightModeMaskIfNeeded(paint);
        paint.setStyle(Style.STROKE);
        paint.setStrokeJoin(Join.ROUND);
        paint.setStrokeMiter(10.0f);
        setTextColor(this.mStrokeColor);
        paint.setStrokeWidth((float) this.mStrokeWidth);
        super.onDraw(canvas);
        paint.setStyle(Style.FILL);
        setTextColor(textColor);
        super.onDraw(canvas);
    }
}
