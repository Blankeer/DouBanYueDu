package com.douban.book.reader.content.paragraph;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.internal.view.SupportMenu;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import com.douban.book.reader.R;
import com.douban.book.reader.constant.Key;
import com.douban.book.reader.content.Format.Align;
import com.douban.book.reader.content.touchable.IllusTouchable;
import com.douban.book.reader.content.touchable.Touchable;
import com.douban.book.reader.helper.WorksListUri;
import com.douban.book.reader.theme.Theme;
import com.douban.book.reader.util.CanvasUtils;
import com.douban.book.reader.util.DebugSwitch;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.PaintUtils;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ThemedUtils;
import com.douban.book.reader.util.Utils;
import com.sina.weibo.sdk.component.ShareRequestParam;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import io.realm.internal.Table;
import org.json.JSONArray;
import org.json.JSONObject;

public class IllusParagraph extends Paragraph {
    private static final float MAX_SCALE = 4.0f;
    private static final int MIN_ILLUS_HEIGHT;
    private static final String TAG;
    Rect mBaseIllusBounds;
    ClipMode mClipMode;
    Rect mClipRect;
    private boolean mDrawLegend;
    private ContainerParagraph mFullLegends;
    Rect mIllusBounds;
    private boolean mIllusClickable;
    private int mIllusHeight;
    private IllusTouchable mIllusTouchable;
    private int mIllusWidth;
    Rect mLayoutRect;
    private ContainerParagraph mLegends;
    Matrix mMatrix;
    float[] mMatrixValues;
    private float mMaxScale;
    private OnGetDrawableListener mOnGetDrawableListener;
    private float mPageWidth;
    private int mSeq;
    VerticalAlign mVerticalAlign;

    public enum ClipMode {
        FIT_INSIDE,
        FIT_WIDTH
    }

    public interface OnGetDrawableListener {
        Drawable getDrawable(int i);

        boolean isDrawableFailedToLoad(int i);
    }

    public enum VerticalAlign {
        TOP,
        CENTER,
        BOTTOM
    }

    static {
        TAG = IllusParagraph.class.getSimpleName();
        MIN_ILLUS_HEIGHT = Utils.dp2pixel(50.0f);
    }

    public IllusParagraph() {
        this.mLegends = null;
        this.mFullLegends = null;
        this.mIllusTouchable = new IllusTouchable();
        this.mIllusClickable = true;
        this.mDrawLegend = false;
        this.mMaxScale = MAX_SCALE;
        this.mClipMode = ClipMode.FIT_INSIDE;
        this.mVerticalAlign = VerticalAlign.CENTER;
        this.mBaseIllusBounds = new Rect();
        this.mIllusBounds = new Rect();
        this.mClipRect = new Rect();
        this.mLayoutRect = new Rect();
        this.mMatrix = new Matrix();
        this.mMatrixValues = new float[9];
        this.mOnGetDrawableListener = null;
        setType(2);
        setPaddingTop((float) VERTICAL_MARGIN_NORMAL);
        setPaddingBottom((float) VERTICAL_MARGIN_NORMAL);
    }

    public static IllusParagraph parse(JSONObject json) {
        return parse(json, null);
    }

    public static IllusParagraph parse(JSONObject json, JSONObject defaultFormat) {
        IllusParagraph paragraph = new IllusParagraph();
        paragraph.setId(json.optInt(WorksListUri.KEY_ID));
        parseIllus(paragraph, json.optJSONObject(ShareRequestParam.RESP_UPLOAD_PIC_PARAM_DATA), defaultFormat);
        return paragraph;
    }

    private static void parseIllus(IllusParagraph paragraph, JSONObject data, JSONObject defaultFormat) {
        paragraph.setType(2);
        paragraph.setIllusSeq(data.optInt("seq"));
        paragraph.setIllusWidth(Utils.dp2pixel((float) data.optInt(SettingsJsonConstants.ICON_WIDTH_KEY)));
        paragraph.setIllusHeight(Utils.dp2pixel((float) data.optInt(SettingsJsonConstants.ICON_HEIGHT_KEY)));
        paragraph.setLegend(parseLegend(data.opt("legend"), defaultFormat));
        paragraph.setFullLegend(parseLegend(data.opt("full_legend"), defaultFormat));
    }

    private static ContainerParagraph parseLegend(Object legendObj, JSONObject defaultFormat) {
        ContainerParagraph result = null;
        if (legendObj instanceof JSONObject) {
            Paragraph legend = Paragraph.parse((JSONObject) legendObj, defaultFormat);
            if (legend instanceof ContainerParagraph) {
                result = (ContainerParagraph) legend;
            }
        } else if (legendObj instanceof JSONArray) {
            result = new ContainerParagraph();
            for (int i = 0; i < ((JSONArray) legendObj).length(); i++) {
                result.appendParagraph(Paragraph.parse(((JSONArray) legendObj).optJSONObject(i), defaultFormat));
            }
        }
        if (result != null) {
            result.setTextSizes(R.array.font_size_legend);
            result.setLineHeightArray(R.array.line_height_legend);
        }
        return result;
    }

    private void setLegend(ContainerParagraph legend) {
        this.mLegends = legend;
    }

    private void setFullLegend(ContainerParagraph legend) {
        this.mFullLegends = legend;
    }

    public void setWidth(float width) {
        super.setWidth(width);
        if (this.mLegends != null) {
            this.mLegends.setWidth(width);
            this.mLegends.setLineLimit(3);
        }
    }

    public CharSequence getText() {
        return getPrintableText();
    }

    public CharSequence getPrintableText(int startOffset, int endOffset) {
        if (this.mLegends == null) {
            return new SpannableStringBuilder(Table.STRING_DEFAULT_VALUE);
        }
        return this.mLegends.getPrintableText(startOffset, endOffset);
    }

    public CharSequence getPrintableFullLegend() {
        if (this.mFullLegends != null) {
            return this.mFullLegends.getPrintableText();
        }
        if (this.mLegends != null) {
            return this.mLegends.getPrintableText();
        }
        return new SpannableStringBuilder(Table.STRING_DEFAULT_VALUE);
    }

    public void setIllusSeq(int seq) {
        this.mSeq = seq;
    }

    public int getIllusSeq() {
        return this.mSeq;
    }

    public void setIllusWidth(int width) {
        this.mIllusWidth = width;
    }

    public void setIllusHeight(int height) {
        this.mIllusHeight = height;
    }

    public Touchable getTouchable(float pageWidth) {
        this.mPageWidth = pageWidth;
        return this.mIllusTouchable;
    }

    public synchronized void generate() {
        if (needRegenerate()) {
            if (this.mLegends != null) {
                this.mLegends.generate();
            }
        }
    }

    public boolean isPagable() {
        return false;
    }

    protected float calculateHeight(int startLine) {
        if (needRegenerate()) {
            generate();
        }
        float imageHeight = (float) this.mIllusHeight;
        if (((float) this.mIllusWidth) > this.mTextAreaWidth && this.mIllusWidth > 0) {
            imageHeight = (this.mTextAreaWidth * ((float) this.mIllusHeight)) / ((float) this.mIllusWidth);
        }
        float legendHeight = 0.0f;
        if (!isLegendEmpty()) {
            legendHeight = this.mLegends.getHeight();
        }
        return imageHeight + legendHeight;
    }

    protected float calculateMinHeight() {
        float legendHeight = 0.0f;
        if (!isLegendEmpty()) {
            legendHeight = this.mLegends.getHeight();
        }
        return ((float) MIN_ILLUS_HEIGHT) + legendHeight;
    }

    public void setDrawLegend(boolean drawLegend) {
        this.mDrawLegend = drawLegend;
    }

    public void setIllusClickable(boolean clickable) {
        this.mIllusClickable = clickable;
    }

    public Rect getIllusBounds() {
        if (this.mIllusBounds.isEmpty()) {
            calculateIllusBounds();
        }
        return this.mIllusBounds;
    }

    public void setMaxScale(float maxScale) {
        this.mMaxScale = maxScale;
    }

    public float getMaxScale() {
        return this.mMaxScale;
    }

    public void postScale(float scale, float px, float py) {
        float curScale = getScale();
        if (curScale == 0.0f) {
            Logger.e(TAG, " error curScale is zero", new Object[0]);
            return;
        }
        float opScale = Math.min(getMaxScale() / curScale, scale);
        if (opScale != 1.0f) {
            this.mMatrix.postScale(opScale, opScale, px, py);
            calculateIllusBounds();
            float tx = 0.0f;
            if (this.mIllusBounds.width() < this.mLayoutRect.width()) {
                tx = (float) (this.mLayoutRect.centerX() - this.mIllusBounds.centerX());
            } else if (this.mIllusBounds.left > this.mLayoutRect.left) {
                tx = (float) (this.mLayoutRect.left - this.mIllusBounds.left);
            } else if (this.mIllusBounds.right < this.mLayoutRect.right) {
                tx = (float) (this.mLayoutRect.right - this.mIllusBounds.right);
            }
            float ty = 0.0f;
            if (this.mIllusBounds.height() < this.mLayoutRect.height()) {
                ty = (float) (this.mLayoutRect.centerY() - this.mIllusBounds.centerY());
            } else if (this.mIllusBounds.top > this.mLayoutRect.top) {
                ty = (float) (this.mLayoutRect.top - this.mIllusBounds.top);
            } else if (this.mIllusBounds.bottom < this.mLayoutRect.bottom) {
                ty = (float) (this.mLayoutRect.bottom - this.mIllusBounds.bottom);
            }
            if (tx != 0.0f || ty != 0.0f) {
                this.mMatrix.postTranslate(tx, ty);
            }
        }
    }

    public void postTranslate(float dx, float dy) {
        if (dx < 0.0f && !rightEdgeArrived()) {
            dx = Math.max(dx, (float) (this.mLayoutRect.right - this.mIllusBounds.right));
        } else if (dx <= 0.0f || leftEdgeArrived()) {
            dx = 0.0f;
        } else {
            dx = Math.min(dx, (float) (this.mLayoutRect.left - this.mIllusBounds.left));
        }
        if (dy < 0.0f && !bottomEdgeArrived()) {
            dy = Math.max(dy, (float) (this.mLayoutRect.bottom - this.mIllusBounds.bottom));
        } else if (dy <= 0.0f || topEdgeArrived()) {
            dy = 0.0f;
        } else {
            dy = Math.min(dy, (float) (this.mLayoutRect.top - this.mIllusBounds.top));
        }
        if (dx != 0.0f || dy != 0.0f) {
            this.mMatrix.postTranslate(dx, dy);
        }
    }

    public void resetMatrix() {
        this.mMatrix.reset();
    }

    public float getScale() {
        this.mMatrix.getValues(this.mMatrixValues);
        return this.mMatrixValues[0];
    }

    public void setVerticalAlign(VerticalAlign align) {
        this.mVerticalAlign = align;
        calculateBaseIllusBounds();
    }

    public void setClipMode(ClipMode mode) {
        this.mClipMode = mode;
        calculateBaseIllusBounds();
    }

    public void setClipRect(Rect rect) {
        this.mClipRect.set(rect);
        calculateBaseIllusBounds();
    }

    public void setLayoutRect(RectF rect) {
        this.mLayoutRect.set(CanvasUtils.rectFToRect(rect));
        calculateBaseIllusBounds();
    }

    public void drawIllus(Canvas canvas) {
        if (this.mLayoutRect.isEmpty()) {
            Logger.e(TAG, new IllegalStateException("Must call setLayoutRect before calling drawIllus. mLayoutRect=" + this.mLayoutRect));
        }
        if (this.mOnGetDrawableListener != null) {
            if (this.mOnGetDrawableListener.isDrawableFailedToLoad(this.mSeq)) {
                drawStaticDrawable(canvas, R.drawable.ic_image_load_failed);
                return;
            }
            Drawable illusDrawable = this.mOnGetDrawableListener.getDrawable(this.mSeq);
            if (illusDrawable == null) {
                drawStaticDrawable(canvas, R.drawable.ic_loading);
                return;
            }
            Paint tempPaint;
            int count = canvas.save();
            if (this.mClipRect.isEmpty()) {
                canvas.clipRect(this.mLayoutRect);
            } else {
                canvas.clipRect(this.mClipRect);
            }
            canvas.setDrawFilter(PaintUtils.sDrawFilter);
            calculateIllusBounds();
            illusDrawable.setBounds(this.mIllusBounds);
            illusDrawable.draw(canvas);
            if (Theme.getCurrentTheme() == 1) {
                canvas.clipRect(this.mIllusBounds);
                canvas.drawColor(ThemedUtils.NIGHT_MODE_MASK_COLOR);
            }
            canvas.restoreToCount(count);
            if (this.mIllusClickable) {
                Rect illusHotArea = new Rect(this.mIllusBounds);
                if (this.mClipRect.isEmpty()) {
                    illusHotArea.intersect(this.mLayoutRect);
                } else {
                    illusHotArea.intersect(this.mClipRect);
                }
                illusHotArea.intersect(Math.round(this.mPageWidth / 10.0f), illusHotArea.top, Math.round((this.mPageWidth * 9.0f) / 10.0f), illusHotArea.bottom);
                this.mIllusTouchable.hotArea.clear();
                this.mIllusTouchable.hotArea.add(illusHotArea);
                this.mIllusTouchable.illusSeq = getIllusSeq();
                this.mIllusTouchable.canTriggerSelect = false;
                if (DebugSwitch.on(Key.APP_DEBUG_SHOW_ILLUS_MARGINS)) {
                    tempPaint = PaintUtils.obtainPaint();
                    tempPaint.setStyle(Style.STROKE);
                    tempPaint.setColor(-16711936);
                    canvas.drawRect(illusHotArea, tempPaint);
                    PaintUtils.recyclePaint(tempPaint);
                }
            }
            if (DebugSwitch.on(Key.APP_DEBUG_SHOW_ILLUS_MARGINS)) {
                tempPaint = PaintUtils.obtainPaint();
                tempPaint.setStyle(Style.STROKE);
                tempPaint.setColor(SupportMenu.CATEGORY_MASK);
                canvas.drawRect(this.mClipRect, tempPaint);
                tempPaint.setColor(-16776961);
                canvas.drawRect(this.mLayoutRect, tempPaint);
                tempPaint.setColor(-65281);
                canvas.drawRect(this.mIllusBounds, tempPaint);
                PaintUtils.recyclePaint(tempPaint);
            }
        }
    }

    protected void onDraw(Canvas canvas, float offsetX, float offsetY, int startLine, int endLine) {
        drawIllus(canvas);
        if (this.mDrawLegend && !isLegendEmpty()) {
            this.mLegends.setTextColorResId(R.array.reader_legend_text_color);
            if (this.mLegends.getLineCount() == 1) {
                this.mLegends.setAlign(Align.CENTER);
            }
            this.mLegends.draw(canvas, offsetX, (float) this.mIllusBounds.bottom);
        }
    }

    private void drawStaticDrawable(Canvas canvas, int resId) {
        Drawable drawable = Res.getDrawable(resId);
        if (drawable != null) {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            int left = this.mLayoutRect.left + ((this.mLayoutRect.width() - width) / 2);
            int top = this.mLayoutRect.top + ((this.mLayoutRect.height() - height) / 2);
            drawable.setBounds(left, top, left + width, top + height);
            drawable.draw(canvas);
        }
    }

    private void calculateIllusBounds() {
        if (this.mBaseIllusBounds.isEmpty()) {
            calculateBaseIllusBounds();
        }
        if (this.mMatrix != null) {
            this.mIllusBounds.set(this.mBaseIllusBounds);
            RectF convertedRect = new RectF(this.mIllusBounds);
            this.mMatrix.mapRect(convertedRect);
            convertedRect.round(this.mIllusBounds);
        }
    }

    private void calculateBaseIllusBounds() {
        if (this.mDrawLegend && !isLegendEmpty()) {
            Rect rect = this.mLayoutRect;
            rect.bottom = (int) (((float) rect.bottom) - this.mLegends.getHeight());
        }
        int windowWidth = this.mLayoutRect.width();
        int windowHeight = this.mLayoutRect.height();
        int illusWidth = this.mIllusWidth;
        int illusHeight = this.mIllusHeight;
        Drawable illusDrawable = this.mOnGetDrawableListener.getDrawable(this.mSeq);
        if (illusDrawable != null) {
            illusWidth = Math.max(illusWidth, illusDrawable.getIntrinsicWidth());
            illusHeight = Math.max(illusHeight, illusDrawable.getIntrinsicHeight());
            if (illusWidth < windowWidth && illusHeight < windowHeight) {
                illusHeight = (int) (((double) illusHeight) * 1.5d);
                illusWidth = (int) (((double) illusWidth) * 1.5d);
            }
        }
        if (illusWidth > 0 && illusHeight > 0) {
            int boundsWidth;
            int boundsHeight;
            if (this.mClipMode == ClipMode.FIT_WIDTH || illusWidth * windowHeight > windowWidth * illusHeight) {
                boundsWidth = Math.min(illusWidth, windowWidth);
                boundsHeight = (illusHeight * boundsWidth) / illusWidth;
            } else {
                boundsHeight = Math.min(illusHeight, windowHeight);
                boundsWidth = (illusWidth * boundsHeight) / illusHeight;
            }
            int x = this.mLayoutRect.left + ((windowWidth - boundsWidth) / 2);
            int y = this.mLayoutRect.top;
            if (this.mVerticalAlign == VerticalAlign.CENTER) {
                y += (windowHeight - boundsHeight) / 2;
            } else if (this.mVerticalAlign == VerticalAlign.BOTTOM) {
                y += windowHeight - boundsHeight;
            }
            this.mBaseIllusBounds.set(x, y, x + boundsWidth, y + boundsHeight);
        }
    }

    public boolean leftEdgeArrived() {
        return this.mIllusBounds.left >= this.mLayoutRect.left;
    }

    public boolean rightEdgeArrived() {
        return this.mIllusBounds.right <= this.mLayoutRect.right;
    }

    public boolean topEdgeArrived() {
        return this.mIllusBounds.top >= this.mLayoutRect.top;
    }

    public boolean bottomEdgeArrived() {
        return this.mIllusBounds.bottom <= this.mLayoutRect.bottom;
    }

    public boolean inOriginScale() {
        return this.mMatrix.isIdentity();
    }

    public void setOnGetDrawableListener(OnGetDrawableListener listener) {
        this.mOnGetDrawableListener = listener;
    }

    public int getOffsetByPoint(float x, float y, boolean jumpByWord, boolean isOffsetAfterThisWord, int startLine, int endLine) {
        return 0;
    }

    public boolean canPinStop() {
        return false;
    }

    private boolean isLegendEmpty() {
        return this.mLegends == null || TextUtils.isEmpty(this.mLegends.getPrintableText());
    }
}
