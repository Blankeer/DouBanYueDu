package com.douban.book.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.douban.book.reader.R;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.Font;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.Utils;

public class ReaderGuideView extends View {
    public ReaderGuideView(Context context) {
        super(context);
    }

    public ReaderGuideView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReaderGuideView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = (float) getWidth();
        float height = (float) getHeight();
        canvas.drawColor(Res.getColor(R.color.reader_guide_bg));
        Paint paint = PaintUtils.obtainPaint();
        paint.setColor(Res.getColorOverridingAlpha(R.color.black, 0.2f));
        canvas.drawRect(0.0f, 0.0f, width * ReadViewPager.EDGE_RATIO, height, paint);
        canvas.drawRect(width * 0.7f, 0.0f, width, height, paint);
        paint.setColor(Res.getColor(R.color.white));
        paint.setTypeface(Font.SANS_SERIF);
        float xLeft = (ReadViewPager.EDGE_RATIO * width) / 2.0f;
        float xCenter = width / 2.0f;
        float xRight = width * 0.85f;
        float yTop = (height / 2.0f) - ((float) Utils.dp2pixel(20.0f));
        float yBottom = height / 2.0f;
        paint.setTextSize((float) Res.getDimensionPixelSize(R.dimen.general_font_size_small));
        CanvasUtils.drawTextCenteredOnPoint(canvas, paint, xLeft, yTop, Res.getString(R.string.reader_guide_click_left_edge));
        CanvasUtils.drawTextCenteredOnPoint(canvas, paint, xCenter, yTop, Res.getString(R.string.reader_guide_click_center));
        CanvasUtils.drawTextCenteredOnPoint(canvas, paint, xRight, yTop, Res.getString(R.string.reader_guide_click_right_edge));
        paint.setTextSize((float) Res.getDimensionPixelSize(R.dimen.general_font_size_normal));
        CanvasUtils.drawTextCenteredOnPoint(canvas, paint, xLeft, yBottom, Res.getString(R.string.reader_guide_goto_prev_page));
        CanvasUtils.drawTextCenteredOnPoint(canvas, paint, xCenter, yBottom, Res.getString(R.string.reader_guide_open_command_bar));
        CanvasUtils.drawTextCenteredOnPoint(canvas, paint, xRight, yBottom, Res.getString(R.string.reader_guide_goto_next_page));
        PaintUtils.recyclePaint(paint);
    }
}
