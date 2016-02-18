package com.douban.book.reader.view.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.douban.book.reader.R;
import com.douban.book.reader.content.paragraph.IllusParagraph;
import com.douban.book.reader.content.paragraph.IllusParagraph.ClipMode;
import com.douban.book.reader.content.touchable.Touchable;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import java.util.List;

public class NoLegendTopGalleryPageView extends AbsGalleryPageView {
    private static final int BUTTON_HEIGHT = 48;
    int mButtonHeight;

    public NoLegendTopGalleryPageView(Context context) {
        super(context);
    }

    protected void initView() {
        inflate(getContext(), R.layout.pageview_gallery_top_no_legend, this);
        super.initView();
        this.mButtonHeight = Utils.dp2pixel(48.0f);
    }

    public List<Touchable> getTouchableArray() {
        if (this.mParagraph == null) {
            return EMPTY_TOUCHABLE_LIST;
        }
        return this.mParagraph.getTouchableArray();
    }

    protected int getHeaderBgColor() {
        return Res.getColor(R.array.reader_gallery_photo_bg_color);
    }

    protected void drawPage(Canvas canvas) {
        super.drawPage(canvas);
        if (((float) canvas.getClipBounds().bottom) > this.mMarginTop) {
            RectF layoutRect = new RectF(0.0f, getHeaderHeight(), (float) getPageWidth(), (float) (getPageHeight() - this.mButtonHeight));
            if (this.mParagraph instanceof IllusParagraph) {
                IllusParagraph paragraph = this.mParagraph;
                paragraph.setOnGetDrawableListener(this.mOnGetDrawableListener);
                paragraph.setClipMode(ClipMode.FIT_WIDTH);
                paragraph.setLayoutRect(layoutRect);
                Drawable illusDrawable = this.mOnGetDrawableListener.getDrawable(paragraph.getIllusSeq());
                if (illusDrawable != null) {
                    Bitmap bitmap = ((BitmapDrawable) illusDrawable).getBitmap();
                    if ((bitmap.getHeight() > AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT || bitmap.getWidth() > AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT) && !ViewUtils.isSoftLayerType(this)) {
                        ViewUtils.setSoftLayerType(this);
                        invalidate();
                        return;
                    }
                }
                paragraph.drawIllus(canvas);
            }
        }
    }
}
