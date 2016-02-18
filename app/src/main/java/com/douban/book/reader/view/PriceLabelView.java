package com.douban.book.reader.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import com.douban.book.reader.R;
import com.douban.book.reader.entity.Works;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.Font;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.Utils;

public class PriceLabelView extends BaseThemedView {
    private static final int HEIGHT;
    private static final float LABEL_PADDING_HORIZONTAL;
    private static final float LABEL_TEXT_SIZE;
    private static final float PRICE_PADDING_HORIZONTAL;
    private static final float PRICE_TEXT_SIZE;
    private boolean mShowStroke;
    private Works mWorks;

    static {
        PRICE_TEXT_SIZE = Res.getDimension(R.dimen.general_font_size_medium);
        LABEL_TEXT_SIZE = PRICE_TEXT_SIZE * 0.75f;
        PRICE_PADDING_HORIZONTAL = Res.getDimension(R.dimen.general_subview_horizontal_padding_small);
        LABEL_PADDING_HORIZONTAL = PRICE_PADDING_HORIZONTAL;
        HEIGHT = Utils.dp2pixel(16.0f);
    }

    public PriceLabelView(Context context) {
        super(context);
        this.mShowStroke = true;
    }

    public PriceLabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mShowStroke = true;
    }

    public PriceLabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mShowStroke = true;
    }

    public void showPriceFor(Works works) {
        this.mWorks = works;
        requestLayout();
    }

    public void showStroke(boolean showStroke) {
        this.mShowStroke = showStroke;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = HEIGHT;
        int width = getMeasuredWidth();
        if (MeasureSpec.getMode(widthMeasureSpec) != 1073741824) {
            float measuredWidth = (getLabelWidth() + getPriceWidth(getOriginalPriceStr())) + (PRICE_PADDING_HORIZONTAL * 2.0f);
            if (this.mWorks != null && this.mWorks.isPromotion()) {
                measuredWidth += getPriceWidth(getPromotionPriceStr()) + PRICE_PADDING_HORIZONTAL;
            }
            width = Math.round(measuredWidth);
        }
        setMeasuredDimension(width, height);
    }

    protected void onDraw(Canvas canvas) {
        float right = (float) getWidth();
        float bottom = (float) getHeight();
        float currentX = 0.0f;
        float centerY = (0.0f + bottom) * 0.5f;
        canvas.drawColor(Res.getColor(R.array.page_highlight_bg_color));
        Paint paint = PaintUtils.obtainStrokePaint(Res.getColor(R.array.btn_stroke_color));
        if (this.mShowStroke) {
            canvas.drawRect(0.0f, 0.0f, right, bottom, paint);
        }
        paint.setTypeface(Font.SANS_SERIF);
        float labelWidth = getLabelWidth();
        if (labelWidth > 0.0f) {
            paint.setColor(getLabelColor());
            paint.setStyle(Style.FILL_AND_STROKE);
            canvas.drawRect(0.0f, 0.0f, 0.0f + labelWidth, bottom, paint);
            paint.setTextSize(LABEL_TEXT_SIZE);
            paint.setColor(Res.getColor(R.array.invert_text_color));
            CanvasUtils.drawTextCenteredOnPoint(canvas, paint, 0.0f + (0.5f * labelWidth), centerY, getLabelStr());
            currentX = 0.0f + (PRICE_PADDING_HORIZONTAL + labelWidth);
        }
        boolean isPromotion = this.mWorks != null && this.mWorks.isPromotion();
        String originalPriceStr = getOriginalPriceStr();
        float originalPriceWidth = getPriceWidth(originalPriceStr);
        if (originalPriceWidth > 0.0f) {
            paint.setTextSize(PRICE_TEXT_SIZE);
            paint.setColor(Res.getColor(isPromotion ? R.array.secondary_text_color : R.array.content_text_color));
            if (isPromotion) {
                paint.setStrikeThruText(true);
            }
            CanvasUtils.drawTextCenteredOnPoint(canvas, paint, (0.5f * originalPriceWidth) + currentX, centerY, originalPriceStr);
            currentX += PRICE_PADDING_HORIZONTAL + originalPriceWidth;
        }
        paint.setTextAlign(Align.LEFT);
        if (isPromotion) {
            paint.setColor(Res.getColor(R.array.red));
            paint.setStrikeThruText(false);
            CanvasUtils.drawTextVerticalCentered(canvas, paint, currentX, centerY, getPromotionPriceStr());
        }
        PaintUtils.recyclePaint(paint);
    }

    private float getLabelWidth() {
        String label = getLabelStr();
        if (StringUtils.isEmpty(label)) {
            return 0.0f;
        }
        Paint paint = PaintUtils.obtainPaint(LABEL_TEXT_SIZE);
        float result = paint.measureText(label);
        PaintUtils.recyclePaint(paint);
        return (LABEL_PADDING_HORIZONTAL * 2.0f) + result;
    }

    private String getLabelStr() {
        if (this.mWorks == null) {
            return null;
        }
        if (this.mWorks.isFree()) {
            return Res.getString(R.string.flag_free);
        }
        if (this.mWorks.isPromotion()) {
            return Res.getString(R.string.flag_sale);
        }
        return null;
    }

    private int getLabelColor() {
        if (this.mWorks.isFree()) {
            return Res.getColor(R.array.bg_label_free);
        }
        if (this.mWorks.isPromotion()) {
            return Res.getColor(R.array.red);
        }
        return Res.getColor(R.array.page_highlight_bg_color);
    }

    private float getPriceWidth(String str) {
        if (StringUtils.isEmpty(str)) {
            return 0.0f;
        }
        Paint paint = PaintUtils.obtainPaint(PRICE_TEXT_SIZE);
        float result = paint.measureText(str);
        PaintUtils.recyclePaint(paint);
        return result;
    }

    private String getOriginalPriceStr() {
        if (this.mWorks == null) {
            return null;
        }
        if (this.mWorks.isFree()) {
            return Res.getString(R.string.text_free);
        }
        if (this.mWorks.isPromotion()) {
            return Utils.formatPrice(this.mWorks.price);
        }
        return Utils.formatPriceWithSymbol(this.mWorks.price);
    }

    private String getPromotionPriceStr() {
        if (this.mWorks != null && this.mWorks.isPromotion()) {
            return Utils.formatPriceWithSymbol(this.mWorks.promotion.price);
        }
        return null;
    }
}
