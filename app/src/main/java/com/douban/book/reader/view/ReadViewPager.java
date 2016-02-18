package com.douban.book.reader.view;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import com.douban.book.reader.app.App;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Tag;
import com.douban.book.reader.view.page.CenterGalleryPageView;
import u.aly.dx;

public class ReadViewPager extends ViewPager {
    public static final float EDGE_RATIO = 0.3f;
    private static final int SHORT_CLICK_PERIOD = 800;
    private static final String TAG = "ReadViewPager";
    private static final int TOUCH_SLOP;
    private final App mApp;
    private float mClickedDownX;
    private float mClickedDownY;
    GestureDetector mGestureDetector;
    private OnEdgePageArrivedListener mOnEdgePageArrivedListener;
    private OnViewPagerClickListener mOnViewPagerClickListener;
    private int mPageWidth;

    public interface OnEdgePageArrivedListener {
        void onViewPagerEdgeArrived(boolean z);
    }

    public interface OnViewPagerClickListener {
        boolean onViewPagerClick(boolean z);
    }

    static {
        TOUCH_SLOP = ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(App.get()));
    }

    public ReadViewPager(Context context) {
        super(context);
        this.mApp = App.get();
        this.mGestureDetector = new GestureDetector(getContext(), new OnGestureListener() {
            public boolean onDown(MotionEvent e) {
                Logger.d(Tag.TOUCHEVENT, "ReadViewPager.Guesture.onDown", new Object[0]);
                return false;
            }

            public void onShowPress(MotionEvent e) {
                Logger.d(Tag.TOUCHEVENT, "ReadViewPager.Guesture.onShowPress", new Object[0]);
            }

            public boolean onSingleTapUp(MotionEvent e) {
                Logger.d(Tag.TOUCHEVENT, "ReadViewPager.Guesture.onSingleTapUp", new Object[0]);
                ReadViewPager.this.clickOnViewPager(e);
                return false;
            }

            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Logger.d(Tag.TOUCHEVENT, "ReadViewPager.Guesture.onScroll", new Object[0]);
                return false;
            }

            public void onLongPress(MotionEvent e) {
                Logger.d(Tag.TOUCHEVENT, "ReadViewPager.Guesture.onLongPress", new Object[0]);
            }

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Logger.d(Tag.TOUCHEVENT, "ReadViewPager.Guesture.onFling", new Object[0]);
                return false;
            }
        });
        this.mOnViewPagerClickListener = null;
        this.mOnEdgePageArrivedListener = null;
        initView();
    }

    public ReadViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mApp = App.get();
        this.mGestureDetector = new GestureDetector(getContext(), new OnGestureListener() {
            public boolean onDown(MotionEvent e) {
                Logger.d(Tag.TOUCHEVENT, "ReadViewPager.Guesture.onDown", new Object[0]);
                return false;
            }

            public void onShowPress(MotionEvent e) {
                Logger.d(Tag.TOUCHEVENT, "ReadViewPager.Guesture.onShowPress", new Object[0]);
            }

            public boolean onSingleTapUp(MotionEvent e) {
                Logger.d(Tag.TOUCHEVENT, "ReadViewPager.Guesture.onSingleTapUp", new Object[0]);
                ReadViewPager.this.clickOnViewPager(e);
                return false;
            }

            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Logger.d(Tag.TOUCHEVENT, "ReadViewPager.Guesture.onScroll", new Object[0]);
                return false;
            }

            public void onLongPress(MotionEvent e) {
                Logger.d(Tag.TOUCHEVENT, "ReadViewPager.Guesture.onLongPress", new Object[0]);
            }

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Logger.d(Tag.TOUCHEVENT, "ReadViewPager.Guesture.onFling", new Object[0]);
                return false;
            }
        });
        this.mOnViewPagerClickListener = null;
        this.mOnEdgePageArrivedListener = null;
        initView();
    }

    private void initView() {
        setFocusableInTouchMode(true);
        requestFocus();
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mPageWidth = getWidth();
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean z = false;
        Logger.d(Tag.TOUCHEVENT, "ReadViewPager.onInterceptTouchEvent: " + event, new Object[z]);
        if (delegateClickToCurrentView(event)) {
            clickOnViewPager(event);
        } else {
            try {
                z = super.onInterceptTouchEvent(event);
            } catch (Exception e) {
            }
        }
        return z;
    }

    public boolean onTouchEvent(MotionEvent event) {
        Logger.d(Tag.TOUCHEVENT, "ReadViewPager.onTouchEvent: " + event, new Object[0]);
        if (this.mOnViewPagerClickListener != null && event.getAction() == 1) {
            if (event.getX() - this.mClickedDownX > ((float) TOUCH_SLOP) && getCurrentItem() == 0) {
                this.mOnEdgePageArrivedListener.onViewPagerEdgeArrived(false);
            }
            if (this.mClickedDownX - event.getX() > ((float) TOUCH_SLOP) && getCurrentItem() == getAdapter().getCount() - 1) {
                this.mOnEdgePageArrivedListener.onViewPagerEdgeArrived(true);
            }
        }
        return super.onTouchEvent(event);
    }

    private boolean delegateClickToCurrentView(MotionEvent event) {
        int action = event.getAction();
        if (action != 0) {
            float dy = event.getY() - this.mClickedDownY;
            if (Math.abs(event.getX() - this.mClickedDownX) > ((float) TOUCH_SLOP) || Math.abs(dy) > ((float) TOUCH_SLOP)) {
                Logger.d(Tag.TOUCHEVENT, "ReadViewPager.delegateClickToCurrentView returned false : 1", new Object[0]);
                return false;
            }
        }
        switch (action) {
            case dx.a /*0*/:
                this.mClickedDownX = event.getX();
                this.mClickedDownY = event.getY();
                break;
            case dx.b /*1*/:
                if (SystemClock.uptimeMillis() - event.getDownTime() < 800) {
                    Logger.d(Tag.TOUCHEVENT, "ReadViewPager.delegateClickToCurrentView returned true", new Object[0]);
                    return true;
                }
                break;
        }
        Logger.d(Tag.TOUCHEVENT, "ReadViewPager.delegateClickToCurrentView returned false : 2", new Object[0]);
        return false;
    }

    private boolean isInLeftEdge(float x) {
        return x < ((float) this.mPageWidth) * EDGE_RATIO;
    }

    private boolean isInRightEdge(float x) {
        return x > ((float) this.mPageWidth) * 0.7f;
    }

    private void clickOnViewPager(MotionEvent event) {
        boolean z = false;
        Logger.d(Tag.TOUCHEVENT, "ReadViewPager.clickOnViewPager called.", new Object[0]);
        float x = event.getX();
        if (this.mOnViewPagerClickListener != null) {
            OnViewPagerClickListener onViewPagerClickListener = this.mOnViewPagerClickListener;
            if (isInLeftEdge(x) || isInRightEdge(x)) {
                z = true;
            }
            if (onViewPagerClickListener.onViewPagerClick(z)) {
                return;
            }
        }
        if (isInLeftEdge(x)) {
            gotoPreviousPage();
        } else if (isInRightEdge(x)) {
            gotoNextPage();
        }
    }

    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (!(v instanceof CenterGalleryPageView) || VERSION.SDK_INT >= 14) {
            return super.canScroll(v, checkV, dx, x, y);
        }
        ViewGroup group = (ViewGroup) v;
        int scrollX = v.getScrollX();
        int scrollY = v.getScrollY();
        for (int i = group.getChildCount() - 1; i >= 0; i--) {
            View child = group.getChildAt(i);
            if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight() && y + scrollY >= child.getTop() && y + scrollY < child.getBottom()) {
                if (canScroll(child, true, dx, (x + scrollX) - child.getLeft(), (y + scrollY) - child.getTop())) {
                    return true;
                }
            }
        }
        return checkV && ((CenterGalleryPageView) v).canScrollHorizontally(-dx);
    }

    public void gotoPreviousPage() {
        gotoPreviousPage(false);
    }

    public void gotoPreviousPage(boolean useAnimation) {
        int page = getCurrentItem();
        if (page > 0) {
            setCurrentItem(page - 1, useAnimation);
        } else {
            this.mOnEdgePageArrivedListener.onViewPagerEdgeArrived(false);
        }
    }

    public void gotoNextPage() {
        gotoNextPage(false);
    }

    public void gotoNextPage(boolean useAnimation) {
        int page = getCurrentItem();
        if (page < getAdapter().getCount() - 1) {
            setCurrentItem(page + 1, useAnimation);
        } else {
            this.mOnEdgePageArrivedListener.onViewPagerEdgeArrived(true);
        }
    }

    public void setOnViewPagerClickListener(OnViewPagerClickListener listener) {
        this.mOnViewPagerClickListener = listener;
    }

    public void setOnEdgePageArrivedListener(OnEdgePageArrivedListener listener) {
        this.mOnEdgePageArrivedListener = listener;
    }
}
