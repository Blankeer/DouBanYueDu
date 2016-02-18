package com.douban.book.reader.view.store;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.douban.book.reader.R;
import com.douban.book.reader.content.paragraph.Paragraph;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.theme.Theme;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.ReadViewPager;
import com.viewpagerindicator.CirclePageIndicator;

public abstract class IndicatedViewPager extends RelativeLayout {
    private static final int PAGE_FLIP_DELAY = 4500;
    private static final String TAG;
    private CirclePageIndicator mCirclePageIndicator;
    private boolean mHighlightMode;
    private final Runnable mPageFlipRunnable;
    private ViewPager mPager;

    private class InnerPagerAdapter extends PagerAdapter {
        public Object instantiateItem(ViewGroup container, int position) {
            View view = IndicatedViewPager.this.getPageView(position);
            container.addView(view, 0);
            return view;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public boolean isViewFromObject(View view, Object object) {
            return object.equals(view);
        }

        public int getCount() {
            return IndicatedViewPager.this.getPageCount();
        }
    }

    protected abstract int getPageCount();

    protected abstract View getPageView(int i);

    static {
        TAG = IndicatedViewPager.class.getSimpleName();
    }

    public IndicatedViewPager(Context context) {
        super(context);
        this.mPageFlipRunnable = new Runnable() {
            public void run() {
                if (IndicatedViewPager.this.mPager != null) {
                    IndicatedViewPager.this.mPager.setCurrentItem((IndicatedViewPager.this.mPager.getCurrentItem() + 1) % IndicatedViewPager.this.mPager.getAdapter().getCount());
                }
                IndicatedViewPager.this.postDelayed(IndicatedViewPager.this.mPageFlipRunnable, 4500);
            }
        };
        init();
    }

    public IndicatedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPageFlipRunnable = new Runnable() {
            public void run() {
                if (IndicatedViewPager.this.mPager != null) {
                    IndicatedViewPager.this.mPager.setCurrentItem((IndicatedViewPager.this.mPager.getCurrentItem() + 1) % IndicatedViewPager.this.mPager.getAdapter().getCount());
                }
                IndicatedViewPager.this.postDelayed(IndicatedViewPager.this.mPageFlipRunnable, 4500);
            }
        };
        init();
    }

    public IndicatedViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mPageFlipRunnable = new Runnable() {
            public void run() {
                if (IndicatedViewPager.this.mPager != null) {
                    IndicatedViewPager.this.mPager.setCurrentItem((IndicatedViewPager.this.mPager.getCurrentItem() + 1) % IndicatedViewPager.this.mPager.getAdapter().getCount());
                }
                IndicatedViewPager.this.postDelayed(IndicatedViewPager.this.mPageFlipRunnable, 4500);
            }
        };
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_indicated_pager, this);
        this.mPager = (ViewPager) findViewById(R.id.pager);
        this.mCirclePageIndicator = (CirclePageIndicator) findViewById(R.id.circle_indicator);
        ViewUtils.setEventAware(this);
        updateIndicator();
    }

    protected final void populateData() {
        boolean z = true;
        try {
            this.mPager.setAdapter(new InnerPagerAdapter());
            this.mCirclePageIndicator.setViewPager(this.mPager);
            if (this.mPager.getAdapter().getCount() <= 1) {
                z = false;
            }
            ViewUtils.showIf(z, this.mCirclePageIndicator);
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
    }

    public void setAutoFlipEnabled() {
        postDelayed(this.mPageFlipRunnable, 4500);
    }

    public void overlayIndicator() {
        ViewUtils.of(this.mCirclePageIndicator).alignBottom(R.id.pager).topMargin(0).removeRule(3).commit();
    }

    public void setHighlightMode() {
        this.mHighlightMode = true;
        updateIndicator();
    }

    protected void onDetachedFromWindow() {
        getHandler().removeCallbacks(this.mPageFlipRunnable);
        super.onDetachedFromWindow();
    }

    public void onEventMainThread(ColorThemeChangedEvent event) {
        updateIndicator();
    }

    private void updateIndicator() {
        if (this.mCirclePageIndicator != null) {
            this.mCirclePageIndicator.setPageColor(getPageColor());
            this.mCirclePageIndicator.setFillColor(getFillColor());
        }
    }

    private int getPageColor() {
        if (this.mHighlightMode) {
            return Res.getColorOverridingAlpha(R.color.black, ReadViewPager.EDGE_RATIO);
        }
        return Res.getColor(R.array.secondary_text_color);
    }

    private int getFillColor() {
        if (!this.mHighlightMode) {
            return Res.getColor(R.array.green);
        }
        return Res.getColorOverridingAlpha(R.color.white, Theme.isNight() ? Paragraph.CODE_TEXTSIZE_RATIO : 1.0f);
    }
}
