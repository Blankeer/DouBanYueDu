package com.douban.book.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.ExploreByTouchHelper;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.manager.WorksManager;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.theme.Theme;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.Font;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.Shadow;
import com.douban.book.reader.util.ThemedUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;

@EViewGroup
public class BoxedWorksView extends BaseThemedViewGroup {
    private static final float BIND_RADIUS_RATIO = 0.07f;
    private static final int BOX_BORDER_LINE_WIDTH;
    private static final int BOX_BORDER_WIDTH;
    private static final float CLOSED_BOX_COVER_HEIGHT_RATIO = 0.125f;
    private static final float CLOSED_BOX_COVER_SPACING = 0.03f;
    private static final int OUTER_SHADOW_WIDTH;
    private static final String TAG;
    private Rect mBoxRect;
    private Path mClosedBoxCover;
    private Rect mCoverRect;
    private boolean mIsDepleted;
    private boolean mIsOpened;
    private Matrix mMatrix;
    private Path mOpenedBoxLeftTopBorder;
    private int mQuantity;
    private boolean mShowBoxCover;
    private WorksCoverView mWorksCoverView;
    @Bean
    WorksManager mWorksManager;

    static {
        TAG = BoxedWorksView.class.getSimpleName();
        OUTER_SHADOW_WIDTH = Utils.dp2pixel(10.0f);
        BOX_BORDER_WIDTH = Utils.dp2pixel(10.0f);
        BOX_BORDER_LINE_WIDTH = Utils.dp2pixel(1.0f);
    }

    public BoxedWorksView(Context context) {
        super(context);
        this.mBoxRect = new Rect();
        this.mCoverRect = new Rect();
        this.mOpenedBoxLeftTopBorder = new Path();
        this.mClosedBoxCover = new Path();
        this.mMatrix = new Matrix();
    }

    public BoxedWorksView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mBoxRect = new Rect();
        this.mCoverRect = new Rect();
        this.mOpenedBoxLeftTopBorder = new Path();
        this.mClosedBoxCover = new Path();
        this.mMatrix = new Matrix();
    }

    public BoxedWorksView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mBoxRect = new Rect();
        this.mCoverRect = new Rect();
        this.mOpenedBoxLeftTopBorder = new Path();
        this.mClosedBoxCover = new Path();
        this.mMatrix = new Matrix();
    }

    @AfterViews
    void init() {
        setWillNotDraw(false);
        setClickable(true);
        this.mWorksCoverView = new WorksCoverView(getContext());
        ViewUtils.of(this.mWorksCoverView).widthMatchParent().heightMatchParent().horizontalPaddingResId(R.dimen.general_subview_horizontal_padding_normal).verticalPaddingResId(R.dimen.general_subview_vertical_padding_normal).commit();
        this.mWorksCoverView.setDuplicateParentStateEnabled(true);
        this.mWorksCoverView.setLayedOnLightBackground();
        this.mWorksCoverView.noLabel();
        addView(this.mWorksCoverView);
        onColorThemeChanged();
    }

    protected void onColorThemeChanged() {
        super.onColorThemeChanged();
        if (this.mWorksCoverView != null) {
            this.mWorksCoverView.setColorFilter(Theme.isNight() ? ThemedUtils.NIGHT_MODE_COLOR_FILTER : null);
        }
    }

    public BoxedWorksView worksId(int worksId) {
        loadWorks(worksId);
        return this;
    }

    public BoxedWorksView showBoxCover(boolean showBoxCover) {
        this.mShowBoxCover = showBoxCover;
        return this;
    }

    public BoxedWorksView isOpened(boolean isOpened) {
        this.mIsOpened = isOpened;
        return this;
    }

    public BoxedWorksView showQuantity(int quantity) {
        this.mQuantity = quantity;
        return this;
    }

    public BoxedWorksView isDepleted(boolean isDepleted) {
        this.mIsDepleted = isDepleted;
        return this;
    }

    @Background
    void loadWorks(int worksId) {
        try {
            updateWorksCover(this.mWorksManager.getWorks(worksId));
        } catch (DataLoadException e) {
            Logger.e(TAG, e);
        }
    }

    @UiThread
    void updateWorksCover(Works works) {
        this.mWorksCoverView.url(works.coverUrl);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        this.mWorksCoverView.measure(MeasureSpec.makeMeasureSpec(this.mCoverRect.width(), ExploreByTouchHelper.INVALID_ID), MeasureSpec.makeMeasureSpec(this.mCoverRect.height(), 1073741824));
        int centerX = this.mCoverRect.centerX();
        int centerY = this.mCoverRect.centerY();
        int radiusX = Math.round(((float) this.mWorksCoverView.getMeasuredWidth()) * 0.5f);
        int radiusY = Math.round(((float) this.mWorksCoverView.getMeasuredHeight()) * 0.5f);
        this.mWorksCoverView.layout(centerX - radiusX, centerY - radiusY, centerX + radiusX, centerY + radiusY);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float boxRadius = ((float) Math.min((getWidth() - getPaddingLeft()) - getPaddingRight(), (getHeight() - getPaddingTop()) - getPaddingBottom())) * 0.5f;
        if (this.mIsOpened) {
            boxRadius -= (float) OUTER_SHADOW_WIDTH;
        }
        float centerX = ((float) getWidth()) * 0.5f;
        float centerY = ((float) getHeight()) * 0.5f;
        if (!this.mIsOpened) {
            float topPadding = boxRadius * 0.25f;
            boxRadius -= (topPadding + (boxRadius * 0.35f)) * 0.5f;
            centerY += topPadding;
        } else if (this.mShowBoxCover) {
            boxRadius *= 0.85f;
            centerX -= boxRadius;
        }
        CanvasUtils.rectFromCenterAndRadius(centerX, centerY, boxRadius * (this.mIsOpened ? 1.0f : 1.2f), boxRadius).roundOut(this.mBoxRect);
        if (this.mIsOpened) {
            this.mCoverRect = CanvasUtils.shrinkRectBy(this.mBoxRect, Math.round((float) BOX_BORDER_WIDTH));
        } else {
            this.mCoverRect.set(this.mBoxRect);
        }
        if (this.mIsOpened) {
            this.mOpenedBoxLeftTopBorder.reset();
            this.mOpenedBoxLeftTopBorder.moveTo((float) (this.mBoxRect.left + BOX_BORDER_LINE_WIDTH), (float) (this.mBoxRect.top + BOX_BORDER_LINE_WIDTH));
            this.mOpenedBoxLeftTopBorder.rLineTo(0.0f, (float) (this.mBoxRect.height() - (BOX_BORDER_LINE_WIDTH * 2)));
            this.mOpenedBoxLeftTopBorder.rLineTo((float) BOX_BORDER_WIDTH, (float) (-BOX_BORDER_WIDTH));
            this.mOpenedBoxLeftTopBorder.rLineTo(0.0f, (float) (-((this.mBoxRect.height() - (BOX_BORDER_LINE_WIDTH * 2)) - (BOX_BORDER_WIDTH * 2))));
            this.mOpenedBoxLeftTopBorder.close();
            this.mOpenedBoxLeftTopBorder.moveTo((float) (this.mBoxRect.left + BOX_BORDER_LINE_WIDTH), (float) (this.mBoxRect.top + BOX_BORDER_LINE_WIDTH));
            this.mOpenedBoxLeftTopBorder.rLineTo((float) (this.mBoxRect.width() - (BOX_BORDER_LINE_WIDTH * 2)), 0.0f);
            this.mOpenedBoxLeftTopBorder.rLineTo((float) (-BOX_BORDER_WIDTH), (float) BOX_BORDER_WIDTH);
            this.mOpenedBoxLeftTopBorder.rLineTo((float) (-((this.mBoxRect.height() - (BOX_BORDER_LINE_WIDTH * 2)) - (BOX_BORDER_WIDTH * 2))), 0.0f);
            this.mOpenedBoxLeftTopBorder.close();
            return;
        }
        float coverHeight = ((float) this.mBoxRect.height()) * CLOSED_BOX_COVER_HEIGHT_RATIO;
        float baseY = ((float) this.mBoxRect.top) - coverHeight;
        float perspectiveHeight = coverHeight;
        float perspectiveWidth = perspectiveHeight * 1.2f;
        float coverSpacing = ((float) this.mBoxRect.height()) * CLOSED_BOX_COVER_SPACING;
        float enlargedBoxHeight = ((float) this.mBoxRect.height()) + (2.0f * coverSpacing);
        this.mClosedBoxCover.reset();
        this.mClosedBoxCover.addRect(((float) this.mBoxRect.left) - coverSpacing, ((float) this.mBoxRect.top) - coverHeight, ((float) this.mBoxRect.right) + coverSpacing, (float) this.mBoxRect.top, Direction.CCW);
        this.mClosedBoxCover.moveTo(((float) this.mBoxRect.right) + coverSpacing, baseY);
        this.mClosedBoxCover.rLineTo(-perspectiveWidth, -perspectiveHeight);
        this.mClosedBoxCover.lineTo((((float) this.mBoxRect.left) - coverSpacing) + perspectiveWidth, baseY - perspectiveHeight);
        this.mClosedBoxCover.lineTo(((float) this.mBoxRect.left) - coverSpacing, baseY);
        this.mClosedBoxCover.moveTo((float) this.mBoxRect.left, (float) this.mBoxRect.top);
        this.mClosedBoxCover.lineTo((float) this.mBoxRect.left, (float) this.mBoxRect.bottom);
        this.mClosedBoxCover.lineTo((float) this.mBoxRect.right, (float) this.mBoxRect.bottom);
        this.mClosedBoxCover.lineTo((float) this.mBoxRect.right, (float) this.mBoxRect.top);
        this.mMatrix.setPolyToPoly(new float[]{((float) this.mBoxRect.left) - coverSpacing, baseY - enlargedBoxHeight, ((float) this.mBoxRect.right) + coverSpacing, baseY - enlargedBoxHeight, ((float) this.mBoxRect.right) + coverSpacing, baseY, ((float) this.mBoxRect.left) - coverSpacing, baseY}, BOX_BORDER_WIDTH, new float[]{(((float) this.mBoxRect.left) - coverSpacing) + perspectiveWidth, baseY - perspectiveHeight, (((float) this.mBoxRect.right) + coverSpacing) - perspectiveWidth, baseY - perspectiveHeight, ((float) this.mBoxRect.right) + coverSpacing, baseY, ((float) this.mBoxRect.left) - coverSpacing, baseY}, BOX_BORDER_WIDTH, 4);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mIsOpened) {
            drawOpenedBox(canvas);
        } else {
            drawClosedBox(canvas);
        }
    }

    private void drawClosedBox(Canvas canvas) {
        float coverHeight = ((float) this.mBoxRect.height()) * CLOSED_BOX_COVER_HEIGHT_RATIO;
        Paint paint = PaintUtils.obtainPaint();
        PaintUtils.applyNightModeMaskIfNeeded(paint);
        paint.setStyle(Style.FILL_AND_STROKE);
        paint.setColor(Res.getColor(R.color.day_highlight_page_bg));
        canvas.drawPath(this.mClosedBoxCover, paint);
        paint.setStyle(Style.STROKE);
        paint.setColor(Res.getColor(R.color.palette_day_bie_blue));
        canvas.drawPath(this.mClosedBoxCover, paint);
        int count = canvas.save();
        canvas.concat(this.mMatrix);
        float coverSpacing = ((float) this.mBoxRect.height()) * CLOSED_BOX_COVER_SPACING;
        float coverRight = ((float) this.mBoxRect.right) + coverSpacing;
        float coverBottom = ((float) this.mBoxRect.top) - coverHeight;
        float centerX = ((((float) this.mBoxRect.left) - coverSpacing) + coverRight) * 0.5f;
        float centerY = ((((((float) this.mBoxRect.top) - coverHeight) - ((float) this.mBoxRect.height())) - (2.0f * coverSpacing)) + coverBottom) * 0.5f;
        float boxRadius = (((float) this.mBoxRect.width()) * 0.5f) + coverSpacing;
        float bindRadius = boxRadius * BIND_RADIUS_RATIO;
        paint.setStyle(Style.FILL_AND_STROKE);
        paint.setColor(Res.getColor(R.color.palette_day_red));
        canvas.drawRect(centerX - bindRadius, centerY - boxRadius, centerX + bindRadius, centerY + boxRadius, paint);
        canvas.drawRect(centerX - boxRadius, centerY - bindRadius, centerX + boxRadius, centerY + bindRadius, paint);
        canvas.restoreToCount(count);
        canvas.drawRect(centerX - bindRadius, coverBottom, centerX + bindRadius, (float) this.mBoxRect.top, paint);
        paint.setStyle(Style.STROKE);
        paint.setColor(Res.getColor(R.color.day_highlight_page_bg));
        canvas.drawLine(centerX - bindRadius, coverBottom, centerX + bindRadius, coverBottom, paint);
        RectF rect = CanvasUtils.rectFromCenterAndRadius(centerX, coverBottom - (0.22f * boxRadius), 0.5f * boxRadius);
        Drawable knot = Res.getDrawable(R.drawable.ic_bow);
        ThemedUtils.setAutoDimInNightMode(knot);
        CanvasUtils.drawDrawableCenteredInArea(canvas, knot, rect);
        if (this.mQuantity > 0) {
            paint.setColor(Res.getColor(this.mIsDepleted ? R.array.secondary_text_color : R.array.content_text_color));
            paint.setTextAlign(Align.RIGHT);
            paint.setTypeface(Font.SANS_SERIF_BOLD);
            paint.setTextSize(0.6f * coverHeight);
            CanvasUtils.drawTextVerticalCentered(canvas, paint, coverRight - (0.1f * boxRadius), (0.6f * coverHeight) + coverBottom, Res.getString(R.string.gift_quantity));
            paint.setTextSize(0.9f * coverHeight);
            CanvasUtils.drawTextVerticalCentered(canvas, paint, coverRight - (0.25f * boxRadius), (0.5f * coverHeight) + coverBottom, String.valueOf(this.mQuantity));
            if (this.mIsDepleted) {
                float boxRadiusX = boxRadius * 1.2f;
                RectF rectF = new RectF((centerX - boxRadiusX) + (CLOSED_BOX_COVER_SPACING * boxRadius), coverBottom - (0.5f * coverHeight), centerX - (0.05f * boxRadiusX), ((float) this.mBoxRect.top) + (1.5f * coverHeight));
                Drawable stamp = Res.getDrawable(R.drawable.ic_gift_pack_depleted);
                ThemedUtils.setAutoDimInNightMode(stamp);
                CanvasUtils.drawDrawableCenteredInArea(canvas, stamp, rectF);
            }
        }
        PaintUtils.recyclePaint(paint);
    }

    private void drawOpenedBox(Canvas canvas) {
        Shadow.draw(canvas, this.mBoxRect);
        Paint paint = PaintUtils.obtainPaint();
        PaintUtils.applyNightModeMaskIfNeeded(paint);
        paint.setColor(Res.getColor(R.color.day_highlight_page_bg));
        canvas.drawRect(this.mBoxRect, paint);
        paint.setColor(Res.getColor(R.color.day_divider));
        canvas.drawPath(this.mOpenedBoxLeftTopBorder, paint);
        paint.setColor(Res.getColor(R.color.day_highlight_page_bg));
        canvas.drawLine((float) (this.mBoxRect.left + BOX_BORDER_LINE_WIDTH), (float) (this.mBoxRect.top + BOX_BORDER_LINE_WIDTH), (float) (this.mBoxRect.left + BOX_BORDER_WIDTH), (float) (this.mBoxRect.top + BOX_BORDER_WIDTH), paint);
        paint.setColor(Res.getColor(R.color.day_page_bg));
        canvas.drawRect((float) (this.mBoxRect.left + BOX_BORDER_WIDTH), (float) (this.mBoxRect.top + BOX_BORDER_WIDTH), (float) (this.mBoxRect.right - BOX_BORDER_WIDTH), (float) (this.mBoxRect.bottom - BOX_BORDER_WIDTH), paint);
        if (this.mShowBoxCover) {
            float centerX = (float) this.mBoxRect.centerX();
            float centerY = (float) this.mBoxRect.centerY();
            float boxRadius = centerY;
            float bindRadius = boxRadius * BIND_RADIUS_RATIO;
            int count = canvas.save();
            canvas.translate(((float) this.mBoxRect.width()) * 0.95f, 0.0f);
            canvas.rotate(15.0f, centerX, centerY);
            paint.setColor(Res.getColor(R.color.day_highlight_page_bg));
            Shadow.draw(canvas, this.mBoxRect);
            canvas.drawRect(this.mBoxRect, paint);
            paint.setColor(Res.getColor(R.color.palette_day_red));
            canvas.clipRect(this.mBoxRect);
            canvas.drawRect(centerX - bindRadius, centerY - boxRadius, centerX + bindRadius, centerY + boxRadius, paint);
            canvas.drawRect(centerX - boxRadius, centerY - bindRadius, centerX + boxRadius, centerY + bindRadius, paint);
            RectF rect = CanvasUtils.rectFromCenterAndRadius(centerX, (1.45f * bindRadius) + centerY, 5.0f * bindRadius);
            Drawable knot = Res.getDrawable(R.drawable.ic_knot);
            ThemedUtils.setAutoDimInNightMode(knot);
            CanvasUtils.drawDrawableCenteredInArea(canvas, knot, rect);
            canvas.restoreToCount(count);
        }
        PaintUtils.recyclePaint(paint);
    }
}
