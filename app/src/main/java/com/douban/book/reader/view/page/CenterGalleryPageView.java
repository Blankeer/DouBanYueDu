package com.douban.book.reader.view.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import com.douban.book.reader.R;
import com.douban.book.reader.app.App;
import com.douban.book.reader.content.paragraph.IllusParagraph;
import com.douban.book.reader.content.paragraph.IllusParagraph.ClipMode;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.Tag;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.FlexibleScrollView;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import u.aly.dj;
import u.aly.dx;

public class CenterGalleryPageView extends AbsGalleryPageView {
    private static final int COMMAND_PANEL_HEIGHT = 40;
    private static final int MAX_LEGEND_HEIGHT = 260;
    private static final int TOUCH_SLOP;
    private float mAnimateTouchedX;
    private float mAnimateTouchedY;
    private boolean mBeingDragged;
    ImageView mBtnLegendHide;
    ImageView mBtnLegendShow;
    View mCommandPanel;
    int mCommandPanelHeight;
    private OnTouchListener mDragLegendListener;
    private int mDraggedY;
    GestureDetectorCompat mGeneralGestureDetector;
    private IllusMode mIllusMode;
    private IllusParagraph mIllusParagraph;
    private float mLastDragY;
    private PointF mLastTouchedPoint;
    View mLegendPanel;
    ScaleGestureDetector mScaleDetector;
    private boolean mZoomInPlace;

    public enum IllusMode {
        NORMAL,
        DRAG,
        ZOOM
    }

    static {
        TOUCH_SLOP = ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(App.get()));
    }

    public CenterGalleryPageView(Context context) {
        super(context);
        this.mBeingDragged = false;
        this.mDraggedY = 0;
        this.mIllusMode = IllusMode.NORMAL;
        this.mLastTouchedPoint = new PointF();
        this.mZoomInPlace = true;
        this.mScaleDetector = new ScaleGestureDetector(getContext(), new SimpleOnScaleGestureListener() {
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            public boolean onScale(ScaleGestureDetector detector) {
                CenterGalleryPageView.this.mIllusParagraph.postScale(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
                CenterGalleryPageView.this.invalidate();
                return true;
            }

            public void onScaleEnd(ScaleGestureDetector detector) {
                if (CenterGalleryPageView.this.mIllusParagraph.getScale() < 1.0f) {
                    CenterGalleryPageView.this.mIllusParagraph.resetMatrix();
                    CenterGalleryPageView.this.invalidate();
                }
            }
        });
        this.mGeneralGestureDetector = null;
        this.mDragLegendListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case dx.a /*0*/:
                        CenterGalleryPageView.this.mAnimateTouchedX = event.getX();
                        CenterGalleryPageView.this.mAnimateTouchedY = event.getY();
                        CenterGalleryPageView.this.startDrag();
                        CenterGalleryPageView.this.requestDisallowInterceptTouchEvent(true);
                        break;
                    case dx.b /*1*/:
                        if (CenterGalleryPageView.this.mBeingDragged) {
                            CenterGalleryPageView.this.endDrag();
                            break;
                        }
                        break;
                    case dx.c /*2*/:
                        CenterGalleryPageView.this.mLastDragY = event.getY();
                        float dy = CenterGalleryPageView.this.mLastDragY - CenterGalleryPageView.this.mAnimateTouchedY;
                        if (Math.abs(event.getX() - CenterGalleryPageView.this.mAnimateTouchedX) <= ((float) CenterGalleryPageView.TOUCH_SLOP)) {
                            if (Math.abs(dy) > ((float) CenterGalleryPageView.TOUCH_SLOP)) {
                                if (!CenterGalleryPageView.this.mBeingDragged) {
                                    CenterGalleryPageView.this.startDrag();
                                    break;
                                }
                                CenterGalleryPageView.this.performDrag(dy);
                                break;
                            }
                        }
                        if (CenterGalleryPageView.this.mBeingDragged) {
                            CenterGalleryPageView.this.endDrag();
                        }
                        CenterGalleryPageView.this.requestDisallowInterceptTouchEvent(false);
                        break;
                        break;
                    case dx.d /*3*/:
                        if (CenterGalleryPageView.this.mBeingDragged) {
                            CenterGalleryPageView.this.endDrag();
                        }
                        CenterGalleryPageView.this.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return true;
            }
        };
    }

    protected void initView() {
        View.inflate(getContext(), R.layout.center_page_view_gallery, this);
        super.initView();
        this.mCommandPanelHeight = Utils.dp2pixel(40.0f);
        this.mCommandPanel = findViewById(R.id.gallery_command_panel);
        this.mLegendPanel = findViewById(R.id.gallery_legend_panel);
        this.mBtnLegendShow = (ImageView) findViewById(R.id.gallery_center_text_expand);
        this.mBtnLegendHide = (ImageView) findViewById(R.id.gallery_center_text_collapse);
        if (this.mLegendWrapper != null) {
            this.mLegendWrapper.setOnTouchListener(getGeneralOnTouchListener());
        }
        this.mBtnLegendShow.setOnTouchListener(getGeneralOnTouchListener());
        this.mBtnLegendShow.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CenterGalleryPageView.this.showLegend();
            }
        });
        this.mBtnLegendHide.setOnTouchListener(getGeneralOnTouchListener());
        this.mBtnLegendHide.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CenterGalleryPageView.this.hideLegend();
            }
        });
        ((FlexibleScrollView) this.mLegendWrapper).setMaxHeight((float) Utils.dp2pixel(260.0f));
        this.mCommandPanel.setBackgroundColor(getHeaderBgColor());
        ViewUtils.setEventAware(this);
    }

    public void onEventMainThread(ColorThemeChangedEvent event) {
        if (this.mCommandPanel != null) {
            this.mCommandPanel.setBackgroundColor(getHeaderBgColor());
        }
    }

    protected void fillView() {
        super.fillView();
        if (this.mParagraph instanceof IllusParagraph) {
            this.mIllusParagraph = (IllusParagraph) this.mParagraph;
        }
        if (TextUtils.isEmpty(this.mLegendView.getText())) {
            this.mBtnLegendShow.setVisibility(8);
            this.mLegendPanel.setVisibility(8);
        }
        if (this.mLegendPanel != null && VERSION.SDK_INT >= 11) {
            ViewUtils.setBottomMargin(this.mLegendPanel, -getPageHeight());
        }
    }

    protected void onPageInvisible() {
        super.onPageInvisible();
        hideLegend();
        this.mIllusParagraph.resetMatrix();
        invalidate();
    }

    protected int getHeaderBgColor() {
        return Res.getColorOverridingAlpha(R.array.page_bg_color, 0.7f);
    }

    protected void drawPage(Canvas canvas) {
        boolean z = false;
        super.drawPage(canvas);
        if (((float) canvas.getClipBounds().bottom) > this.mMarginTop) {
            int pageWidth = getPageWidth();
            int pageHeight = getPageHeight();
            Rect clipRect = new Rect(0, 0, pageWidth, pageHeight);
            RectF layoutRect = new RectF(0.0f, getHeaderHeight(), (float) pageWidth, (float) (pageHeight - this.mCommandPanelHeight));
            this.mIllusParagraph.setOnGetDrawableListener(this.mOnGetDrawableListener);
            this.mIllusParagraph.setClipRect(clipRect);
            this.mIllusParagraph.setClipMode(ClipMode.FIT_INSIDE);
            this.mIllusParagraph.setLayoutRect(layoutRect);
            IllusParagraph illusParagraph = this.mIllusParagraph;
            if (!this.mZoomInPlace) {
                z = true;
            }
            illusParagraph.setIllusClickable(z);
            Drawable illusDrawable = this.mOnGetDrawableListener.getDrawable(this.mIllusParagraph.getIllusSeq());
            if (illusDrawable != null) {
                Bitmap bitmap = ((BitmapDrawable) illusDrawable).getBitmap();
                if ((bitmap.getHeight() > AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT || bitmap.getWidth() > AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT) && !ViewUtils.isSoftLayerType(this)) {
                    ViewUtils.setSoftLayerType(this);
                    invalidate();
                    return;
                }
            }
            this.mIllusParagraph.drawIllus(canvas);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        Logger.d(Tag.TOUCHEVENT, "CenterGalleryPageView.onTouchEvent: " + event, new Object[0]);
        if (this.mZoomInPlace) {
            this.mScaleDetector.onTouchEvent(event);
        }
        switch (event.getAction() & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) {
            case dx.a /*0*/:
                this.mLastTouchedPoint.set(event.getX(), event.getY());
                if (this.mIllusParagraph.inOriginScale()) {
                    this.mIllusMode = IllusMode.NORMAL;
                    return true;
                }
                this.mIllusMode = IllusMode.DRAG;
                return true;
            case dx.b /*1*/:
                this.mIllusMode = IllusMode.NORMAL;
                return true;
            case dx.c /*2*/:
                if (this.mIllusMode == IllusMode.ZOOM) {
                    return true;
                }
                int deltaX = Math.round(event.getX() - this.mLastTouchedPoint.x);
                int deltaY = Math.round(event.getY() - this.mLastTouchedPoint.y);
                this.mLastTouchedPoint.set(event.getX(), event.getY());
                if (this.mIllusMode == IllusMode.DRAG) {
                    this.mIllusParagraph.postTranslate((float) deltaX, (float) deltaY);
                    invalidate();
                }
                if ((deltaX < (-TOUCH_SLOP) && this.mIllusParagraph.rightEdgeArrived()) || (deltaX > TOUCH_SLOP && this.mIllusParagraph.leftEdgeArrived())) {
                    requestDisallowInterceptTouchEvent(false);
                    return false;
                } else if (this.mIllusMode == IllusMode.NORMAL) {
                    if (((float) Math.abs(deltaY)) * 0.5f > ((float) Math.abs(deltaX))) {
                        this.mAllowInterceptScroll = true;
                    }
                    requestDisallowInterceptTouchEvent(false);
                    return false;
                } else if (Math.abs(deltaX) <= TOUCH_SLOP && Math.abs(deltaY) <= TOUCH_SLOP) {
                    return true;
                } else {
                    this.mIllusMode = IllusMode.DRAG;
                    requestDisallowInterceptTouchEvent(true);
                    return true;
                }
            case dj.f /*5*/:
                if (!this.mZoomInPlace) {
                    return true;
                }
                this.mIllusMode = IllusMode.ZOOM;
                requestDisallowInterceptTouchEvent(true);
                return true;
            default:
                return true;
        }
    }

    public boolean canScrollHorizontally(int direction) {
        if (this.mParagraph instanceof IllusParagraph) {
            if (direction < 0) {
                if (this.mIllusParagraph.leftEdgeArrived()) {
                    return false;
                }
                return true;
            } else if (this.mIllusParagraph.rightEdgeArrived()) {
                return false;
            } else {
                return true;
            }
        } else if (VERSION.SDK_INT >= 14) {
            return super.canScrollHorizontally(direction);
        } else {
            return ViewCompat.canScrollHorizontally(this, direction);
        }
    }

    public void setZoomInPlace(boolean zoomInPlace) {
        this.mZoomInPlace = zoomInPlace;
    }

    GestureDetectorCompat getGeneralGestureDetector() {
        if (this.mGeneralGestureDetector == null) {
            SimpleOnGestureListener listener = new SimpleOnGestureListener() {
                boolean isZoomIn;

                {
                    this.isZoomIn = true;
                }

                public boolean onDoubleTap(MotionEvent e) {
                    boolean z;
                    Logger.d(Tag.TOUCHEVENT, "CenterGalleryPageView.Gesture.onDoubleTap", new Object[0]);
                    CenterGalleryPageView.this.mIllusParagraph.postScale(this.isZoomIn ? 2.0f : 0.5f, e.getX(), e.getY());
                    if (this.isZoomIn) {
                        z = false;
                    } else {
                        z = true;
                    }
                    this.isZoomIn = z;
                    CenterGalleryPageView.this.invalidate();
                    return true;
                }
            };
            this.mGeneralGestureDetector = new GestureDetectorCompat(getContext(), listener);
            this.mGeneralGestureDetector.setOnDoubleTapListener(listener);
        }
        return this.mGeneralGestureDetector;
    }

    private void showLegend() {
        ViewUtils.goneWithAnim(R.anim.push_bottom_out, this.mCommandPanel);
        ViewUtils.visibleWithAnim(R.anim.push_bottom_in, this.mLegendPanel);
    }

    private void hideLegend() {
        ViewUtils.visibleWithAnim(R.anim.push_bottom_in, this.mCommandPanel);
        ViewUtils.goneWithAnim(R.anim.push_bottom_out, this.mLegendPanel);
    }

    private void startDrag() {
        this.mBeingDragged = true;
        this.mDraggedY = this.mBtnLegendShow.getTop();
    }

    private void endDrag() {
        this.mBeingDragged = false;
        this.mLastDragY = 0.0f;
        this.mAnimateTouchedY = 0.0f;
    }

    private void performDrag(float dy) {
        this.mDraggedY = (int) (((float) this.mDraggedY) + (dy / 3.0f));
    }
}
