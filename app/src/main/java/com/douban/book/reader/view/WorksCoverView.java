package com.douban.book.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView.ScaleType;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.CharUtils;
import com.douban.book.reader.util.Font;
import com.douban.book.reader.util.ImageLoaderUtils;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.Shadow;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;

public class WorksCoverView extends CheckableImageView {
    private float mDrawableRatio;
    private Drawable mForegroundDrawable;
    private Label mLabel;
    private boolean mLayedOnLightBackground;
    private boolean mShowLabel;
    private String mUrl;

    public enum Label {
        FREE("FREE", R.array.bg_label_free),
        SALE("SALE", R.array.red),
        SAMPLE("SAMPLE", R.array.blue);
        
        private final int mColorArrayResId;
        private final String mName;

        private Label(String name, int colorArrayResId) {
            this.mName = name;
            this.mColorArrayResId = colorArrayResId;
        }

        public String getName() {
            return this.mName;
        }

        public int getColorArrayResId() {
            return this.mColorArrayResId;
        }
    }

    public WorksCoverView(Context context) {
        super(context);
        this.mShowLabel = true;
        this.mDrawableRatio = 1.4909091f;
        init();
    }

    public WorksCoverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mShowLabel = true;
        this.mDrawableRatio = 1.4909091f;
        init();
    }

    public WorksCoverView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mShowLabel = true;
        this.mDrawableRatio = 1.4909091f;
        init();
    }

    private void init() {
        setScaleType(ScaleType.FIT_XY);
        setDuplicateParentStateEnabled(true);
        this.mForegroundDrawable = getResources().getDrawable(R.drawable.bg_works_cover_mask);
        setImageResource(R.drawable.default_works_cover);
        ViewUtils.setEventAware(this);
        updateViews();
    }

    public void setLayedOnLightBackground() {
        this.mLayedOnLightBackground = true;
    }

    public WorksCoverView works(Works works) {
        if (works != null) {
            this.mUrl = works.coverUrl;
            if (works.isFree()) {
                this.mLabel = Label.FREE;
            } else if (works.isPromotion()) {
                this.mLabel = Label.SALE;
            } else {
                this.mLabel = null;
            }
            updateViews();
        }
        return this;
    }

    public WorksCoverView noLabel() {
        this.mShowLabel = false;
        return this;
    }

    public WorksCoverView url(String url) {
        this.mUrl = url;
        updateViews();
        return this;
    }

    public WorksCoverView label(Label label) {
        this.mLabel = label;
        updateViews();
        return this;
    }

    private void updateViews() {
        if (StringUtils.isNotEmpty(this.mUrl)) {
            ImageLoaderUtils.displayImage(this.mUrl, this, R.drawable.default_works_cover);
        }
        invalidate();
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        this.mForegroundDrawable.setState(getDrawableState());
        invalidate();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mForegroundDrawable.setBounds(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
    }

    public void onEventMainThread(ColorThemeChangedEvent event) {
        invalidate();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(widthMeasureSpec) == 1073741824) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(width, (getPaddingTop() + Math.round(((float) ((width - getPaddingLeft()) - getPaddingRight())) * this.mDrawableRatio)) + getPaddingBottom());
        } else if (MeasureSpec.getMode(heightMeasureSpec) == 1073741824) {
            int height = MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension((getPaddingLeft() + Math.round(((float) ((height - getPaddingTop()) - getPaddingBottom())) / this.mDrawableRatio)) + getPaddingRight(), height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setDrawableRatio(float ratio) {
        this.mDrawableRatio = ratio;
    }

    protected void onDraw(Canvas canvas) {
        if (this.mLayedOnLightBackground) {
            Shadow.drawLightShadow(canvas, this.mForegroundDrawable.getBounds());
        } else {
            Shadow.draw(canvas, this.mForegroundDrawable.getBounds());
        }
        canvas.save();
        canvas.clipRect(this.mForegroundDrawable.getBounds());
        super.onDraw(canvas);
        if (this.mShowLabel) {
            if (this.mLabel == Label.SAMPLE) {
                drawSampleLabel(this, canvas, this.mLabel.getColorArrayResId(), Res.getString(R.string.text_sample));
            } else if (this.mLabel == Label.FREE || this.mLabel == Label.SALE) {
                drawTopRightLabel(this, canvas, this.mLabel.getColorArrayResId(), this.mLabel.getName());
            }
        }
        canvas.restore();
        this.mForegroundDrawable.draw(canvas);
    }

    private static void drawSampleLabel(View view, Canvas canvas, int labelColor, String text) {
        if (view != null) {
            canvas.save();
            canvas.translate((float) (view.getWidth() - view.getPaddingRight()), (float) view.getPaddingTop());
            int tagHeight = ((view.getWidth() - view.getPaddingRight()) - view.getPaddingLeft()) / 3;
            Paint tagPaint = PaintUtils.obtainPaint();
            canvas.rotate(45.0f);
            tagPaint.setColor(Res.getColorOverridingAlpha(labelColor, 0.9f));
            canvas.drawRect((float) (-Utils.dp2pixel(200.0f)), (float) null, (float) Utils.dp2pixel(200.0f), (float) (0 + tagHeight), tagPaint);
            tagPaint.setColor(Res.getColor(R.array.invert_text_color));
            tagPaint.setTextAlign(Align.CENTER);
            tagPaint.setTextSize(((float) tagHeight) * CharUtils.FULL_WIDTH_CHAR_OFFSET_ADJUSTMENT_RATIO);
            tagPaint.setTypeface(Font.SANS_SERIF);
            CanvasUtils.drawTextCenteredOnPoint(canvas, tagPaint, 0.0f, ((float) null) + (((float) tagHeight) * 0.65f), text);
            PaintUtils.recyclePaint(tagPaint);
            canvas.restore();
        }
    }

    private static void drawTopRightLabel(View view, Canvas canvas, int labelColor, String text) {
        if (view != null) {
            canvas.save();
            canvas.translate((float) (view.getWidth() - view.getPaddingRight()), (float) view.getPaddingTop());
            int tagHeight = ((view.getWidth() - view.getPaddingRight()) - view.getPaddingLeft()) / 5;
            int tagStartY = tagHeight / 2;
            Paint tagPaint = PaintUtils.obtainPaint();
            canvas.rotate(45.0f);
            tagPaint.setColor(Res.getColor(labelColor));
            canvas.drawRect((float) (-Utils.dp2pixel(200.0f)), (float) tagStartY, (float) Utils.dp2pixel(200.0f), (float) (tagStartY + tagHeight), tagPaint);
            tagPaint.setColor(Res.getColor(R.array.invert_text_color));
            tagPaint.setTextAlign(Align.CENTER);
            tagPaint.setTextSize(((float) tagHeight) / 2.0f);
            tagPaint.setTypeface(Font.SANS_SERIF_BOLD);
            CanvasUtils.drawTextCenteredOnPoint(canvas, tagPaint, 0.0f, ((float) tagStartY) + (((float) tagHeight) / 2.0f), text);
            PaintUtils.recyclePaint(tagPaint);
            canvas.restore();
        }
    }
}
