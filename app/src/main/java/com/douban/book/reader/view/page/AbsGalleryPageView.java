package com.douban.book.reader.view.page;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import com.douban.book.reader.R;
import com.douban.book.reader.app.PageOpenHelper;
import com.douban.book.reader.constant.Constants;
import com.douban.book.reader.content.Book.ImageSize;
import com.douban.book.reader.content.page.Position;
import com.douban.book.reader.content.page.Range;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.fragment.share.ShareRangeEditFragment_;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.ReaderUri;
import com.douban.book.reader.util.Tag;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.view.ParagraphView;
import u.aly.dx;

public abstract class AbsGalleryPageView extends AbsPageView {
    private static final float HEADER_HEIGHT;
    ImageView mBtnMore;
    ImageView mBtnShare;
    ParagraphView mLegendView;
    ScrollView mLegendWrapper;
    Paragraph mParagraph;
    private Position mPosition;
    private OnTouchListener mSubmitDragListener;
    int mTextScale;

    static {
        HEADER_HEIGHT = (float) Utils.dp2pixel(25.0f);
    }

    public AbsGalleryPageView(Context context) {
        super(context);
        this.mSubmitDragListener = null;
    }

    protected void initView() {
        super.initView();
        this.mMarginTop = HEADER_HEIGHT;
        this.mLegendWrapper = (ScrollView) findViewById(R.id.gallery_legend_wrapper);
        this.mLegendView = (ParagraphView) findViewById(R.id.gallery_legend);
        this.mBtnShare = (ImageView) findViewById(R.id.gallery_share);
        this.mBtnMore = (ImageView) findViewById(R.id.gallery_more);
        if (this.mLegendWrapper != null) {
            this.mLegendWrapper.setOnTouchListener(getSubmitDragListener());
        }
        if (this.mBtnShare != null) {
            ThemedAttrs.ofView(this.mBtnShare).append(R.attr.srcArray, Integer.valueOf(R.array.btn_gallery_share)).updateView();
            this.mBtnShare.setOnTouchListener(getGeneralOnTouchListener());
            this.mBtnShare.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ShareRangeEditFragment_.builder().worksId(AbsGalleryPageView.this.mBook.getBookId()).range(new Range(AbsGalleryPageView.this.mPosition, AbsGalleryPageView.this.mPosition)).build().showAsActivity(PageOpenHelper.from(AbsGalleryPageView.this));
                }
            });
        }
        if (this.mBtnMore != null) {
            ThemedAttrs.ofView(this.mBtnMore).append(R.attr.srcArray, Integer.valueOf(R.array.btn_gallery_more)).updateView();
            this.mBtnMore.setOnTouchListener(getGeneralOnTouchListener());
        }
    }

    public boolean isTextSelectable() {
        return false;
    }

    protected float getHeaderHeight() {
        return HEADER_HEIGHT;
    }

    protected void onDraw(Canvas canvas) {
        drawPage(canvas);
        drawHeader(canvas);
    }

    protected void drawPage(Canvas canvas) {
    }

    public void setPage(int bookId, int pageNo) {
        super.setPage(bookId, pageNo);
        this.mPageInfo = this.mBook.getPageInfo(pageNo);
        this.mParagraph = this.mBook.getParagraph(this.mChapterIndex, this.mPageInfo.startParaIndex);
        this.mPosition = this.mBook.getPositionForPage(pageNo);
        fillView();
    }

    protected void fillView() {
        if (this.mLegendView != null) {
            this.mLegendView.setParagraphText(this.mParagraph.getPrintableText());
        }
        setLegendTextSize();
    }

    protected String getDrawableUri(int seq) {
        return ReaderUri.illus(this.mBook.getBookId(), this.mPackageId, seq, ImageSize.LARGE).toString();
    }

    public void redraw() {
        setLegendTextSize();
        invalidate();
    }

    protected void redraw(RectF rect) {
        loadModelInfo();
        invalidate((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
    }

    private void setLegendTextSize() {
        if (this.mLegendView != null) {
            this.mTextScale = this.mApp.getScale();
            this.mLegendView.setTextSize((float) Utils.dp2pixel(((float) Constants.TEXTSIZE_GALLERY_LEGEND[this.mTextScale]) * this.mParagraph.getTextSizeRatio()));
        }
    }

    private OnTouchListener getSubmitDragListener() {
        if (this.mSubmitDragListener == null) {
            this.mSubmitDragListener = new OnTouchListener() {
                float mTouchedDownY;

                {
                    this.mTouchedDownY = 0.0f;
                }

                public boolean onTouch(View v, MotionEvent event) {
                    Logger.d(Tag.TOUCHEVENT, "GalleryPageView.LegendWrapper.mOnTouchListener: " + event, new Object[0]);
                    switch (event.getAction()) {
                        case dx.a /*0*/:
                            this.mTouchedDownY = event.getY();
                            break;
                        case dx.c /*2*/:
                            float deltaY = event.getY() - this.mTouchedDownY;
                            if (AbsGalleryPageView.this.mLegendWrapper.getScrollY() == 0 && deltaY > 0.0f) {
                                AbsGalleryPageView.this.mAllowInterceptScroll = true;
                                AbsGalleryPageView.this.requestDisallowInterceptTouchEvent(false);
                                break;
                            }
                    }
                    return false;
                }
            };
        }
        return this.mSubmitDragListener;
    }
}
