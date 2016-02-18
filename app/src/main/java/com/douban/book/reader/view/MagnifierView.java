package com.douban.book.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import com.igexin.download.Downloads;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;

public class MagnifierView extends View {
    private static final int MAGNIFIER_HEIGHT;
    private static final int MAGNIFIER_WIDTH;
    private static final int MAGNIFIER_Y_OFFSET;
    private static final float MAGNIFIER_ZOOM_BY = 1.2f;
    private static final int PADDING_BOTTOM;
    private static final int PADDING_LEFT;
    private static final int PADDING_RIGHT;
    private static final int PADDING_TOP;
    Shader mHighlight;
    RectF mHighlightRect;
    Path mInnerPath;
    float mOffsetX;
    float mOffsetY;
    Path mOuterPath;
    Path mShadowPath;
    View mView;

    static {
        MAGNIFIER_WIDTH = Utils.dp2pixel(140.0f);
        MAGNIFIER_HEIGHT = Utils.dp2pixel(140.0f);
        MAGNIFIER_Y_OFFSET = Utils.dp2pixel(30.0f);
        PADDING_TOP = Utils.dp2pixel(10.0f);
        PADDING_LEFT = Utils.dp2pixel(8.0f);
        PADDING_BOTTOM = PADDING_TOP;
        PADDING_RIGHT = PADDING_LEFT;
    }

    public MagnifierView(Context context) {
        super(context);
        this.mView = null;
        this.mOuterPath = new Path();
        this.mInnerPath = new Path();
        this.mShadowPath = new Path();
        initView();
    }

    public MagnifierView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mView = null;
        this.mOuterPath = new Path();
        this.mInnerPath = new Path();
        this.mShadowPath = new Path();
        initView();
    }

    public MagnifierView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mView = null;
        this.mOuterPath = new Path();
        this.mInnerPath = new Path();
        this.mShadowPath = new Path();
        initView();
    }

    private void initView() {
        ViewUtils.setSoftLayerType(this);
        initDrawElement();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MAGNIFIER_WIDTH, MAGNIFIER_HEIGHT);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mView != null) {
            Paint paint = PaintUtils.obtainPaint();
            drawBoundary(canvas, paint);
            canvas.save();
            try {
                canvas.clipPath(this.mInnerPath);
            } catch (UnsupportedOperationException e) {
            }
            canvas.translate(-this.mOffsetX, -this.mOffsetY);
            canvas.scale(MAGNIFIER_ZOOM_BY, MAGNIFIER_ZOOM_BY);
            canvas.setDrawFilter(PaintUtils.sDrawFilter);
            this.mView.draw(canvas);
            canvas.restore();
            drawHighlight(canvas, paint);
            if (DebugSwitch.on(Key.APP_DEBUG_SHOW_MAGNIFIER_FOCUS)) {
                CanvasUtils.drawFocusPoint(canvas, (float) (MAGNIFIER_WIDTH / 2), (float) (MAGNIFIER_HEIGHT / 2));
            }
            PaintUtils.recyclePaint(paint);
        }
    }

    void drawBoundary(Canvas canvas, Paint paint) {
        paint.setShadowLayer(4.0f, 4.0f, 4.0f, Color.argb(100, MAGNIFIER_Y_OFFSET, MAGNIFIER_Y_OFFSET, MAGNIFIER_Y_OFFSET));
        paint.setColor(Color.argb(SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT, Downloads.STATUS_SUCCESS, Downloads.STATUS_SUCCESS, Downloads.STATUS_SUCCESS));
        canvas.drawPath(this.mOuterPath, paint);
        paint.setShadowLayer(0.0f, 0.0f, 0.0f, MAGNIFIER_Y_OFFSET);
        paint.setColor(Color.argb(SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT, 100, 100, 100));
        paint.setStrokeWidth((float) Utils.dp2pixel(1.0f));
        paint.setStyle(Style.STROKE);
        canvas.drawPath(this.mOuterPath, paint);
        paint.setStyle(Style.FILL);
    }

    void drawHighlight(Canvas canvas, Paint paint) {
        canvas.save();
        paint.setColor(Color.argb(25, MAGNIFIER_Y_OFFSET, MAGNIFIER_Y_OFFSET, 50));
        canvas.drawPath(this.mInnerPath, paint);
        paint.setColor(Color.argb(SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT, 100, 100, 100));
        paint.setStrokeWidth((float) Utils.dp2pixel(1.0f));
        paint.setStyle(Style.STROKE);
        canvas.drawPath(this.mInnerPath, paint);
        paint.setStyle(Style.FILL);
        paint.setColor(Color.argb(30, MAGNIFIER_Y_OFFSET, MAGNIFIER_Y_OFFSET, MAGNIFIER_Y_OFFSET));
        try {
            canvas.clipPath(this.mInnerPath, Op.INTERSECT);
            canvas.drawPath(this.mShadowPath, paint);
        } catch (UnsupportedOperationException e) {
        }
        paint.setShader(this.mHighlight);
        paint.setColor(Color.argb(Header.MAPDB, SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT, SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT, SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT));
        canvas.drawOval(this.mHighlightRect, paint);
        canvas.restore();
    }

    void initDrawElement() {
        this.mOuterPath.addCircle((float) (MAGNIFIER_WIDTH / 2), (float) (MAGNIFIER_HEIGHT / 2), (float) (((MAGNIFIER_WIDTH / 2) + 5) - PADDING_TOP), Direction.CW);
        this.mInnerPath.addCircle((float) (MAGNIFIER_WIDTH / 2), (float) (MAGNIFIER_HEIGHT / 2), (float) ((MAGNIFIER_WIDTH / 2) - PADDING_TOP), Direction.CW);
        this.mShadowPath.addCircle((float) (MAGNIFIER_WIDTH / 2), (float) (((double) MAGNIFIER_HEIGHT) * 1.3d), (float) (MAGNIFIER_WIDTH - PADDING_TOP), Direction.CCW);
        this.mHighlight = new RadialGradient((float) (MAGNIFIER_WIDTH / 2), (float) ((int) (((double) MAGNIFIER_HEIGHT) * 0.75d)), (float) (MAGNIFIER_WIDTH / 4), Color.argb(Header.MAPDB, SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT, SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT, SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT), Color.argb(MAGNIFIER_Y_OFFSET, SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT, SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT, SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT), TileMode.CLAMP);
        this.mHighlightRect = new RectF(((float) MAGNIFIER_WIDTH) * 0.2f, ((float) MAGNIFIER_HEIGHT) * 0.5f, ((float) MAGNIFIER_WIDTH) * Paragraph.CODE_TEXTSIZE_RATIO, ((float) MAGNIFIER_HEIGHT) * 1.0f);
    }

    public void setView(View view) {
        this.mView = view;
    }

    public void moveTo(float x, float y) {
        MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
        y = Math.min(y, (float) (this.mView.getHeight() - ((MAGNIFIER_HEIGHT / 2) - PADDING_BOTTOM)));
        params.leftMargin = (int) (x - ((float) (MAGNIFIER_WIDTH / 2)));
        params.topMargin = (int) ((y - ((float) MAGNIFIER_HEIGHT)) - ((float) MAGNIFIER_Y_OFFSET));
        params.topMargin = Math.max(params.topMargin, (-MAGNIFIER_HEIGHT) / 3);
        setLayoutParams(params);
        requestLayout();
        updateContentOffset(x, y);
    }

    private void updateContentOffset(float x, float y) {
        this.mOffsetX = (x * MAGNIFIER_ZOOM_BY) - ((float) (MAGNIFIER_WIDTH / 2));
        this.mOffsetY = (y * MAGNIFIER_ZOOM_BY) - ((float) (MAGNIFIER_HEIGHT / 2));
        invalidate();
    }
}
