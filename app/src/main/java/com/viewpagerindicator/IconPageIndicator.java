package com.viewpagerindicator;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

public class IconPageIndicator extends HorizontalScrollView implements PageIndicator {
    private Runnable mIconSelector;
    private final IcsLinearLayout mIconsLayout;
    private OnPageChangeListener mListener;
    private int mSelectedIndex;
    private ViewPager mViewPager;

    /* renamed from: com.viewpagerindicator.IconPageIndicator.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ View val$iconView;

        AnonymousClass1(View view) {
            this.val$iconView = view;
        }

        public void run() {
            IconPageIndicator.this.smoothScrollTo(this.val$iconView.getLeft() - ((IconPageIndicator.this.getWidth() - this.val$iconView.getWidth()) / 2), 0);
            IconPageIndicator.this.mIconSelector = null;
        }
    }

    public IconPageIndicator(Context context) {
        this(context, null);
    }

    public IconPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);
        this.mIconsLayout = new IcsLinearLayout(context, R.attr.vpiIconPageIndicatorStyle);
        addView(this.mIconsLayout, new LayoutParams(-2, -1, 17));
    }

    private void animateToIcon(int position) {
        View iconView = this.mIconsLayout.getChildAt(position);
        if (this.mIconSelector != null) {
            removeCallbacks(this.mIconSelector);
        }
        this.mIconSelector = new AnonymousClass1(iconView);
        post(this.mIconSelector);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mIconSelector != null) {
            post(this.mIconSelector);
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mIconSelector != null) {
            removeCallbacks(this.mIconSelector);
        }
    }

    public void onPageScrollStateChanged(int arg0) {
        if (this.mListener != null) {
            this.mListener.onPageScrollStateChanged(arg0);
        }
    }

    public void onPageScrolled(int arg0, float arg1, int arg2) {
        if (this.mListener != null) {
            this.mListener.onPageScrolled(arg0, arg1, arg2);
        }
    }

    public void onPageSelected(int arg0) {
        setCurrentItem(arg0);
        if (this.mListener != null) {
            this.mListener.onPageSelected(arg0);
        }
    }

    public void setViewPager(ViewPager view) {
        if (this.mViewPager != view) {
            if (this.mViewPager != null) {
                this.mViewPager.setOnPageChangeListener(null);
            }
            if (view.getAdapter() == null) {
                throw new IllegalStateException("ViewPager does not have adapter instance.");
            }
            this.mViewPager = view;
            view.setOnPageChangeListener(this);
            notifyDataSetChanged();
        }
    }

    public void notifyDataSetChanged() {
        this.mIconsLayout.removeAllViews();
        IconPagerAdapter iconAdapter = (IconPagerAdapter) this.mViewPager.getAdapter();
        int count = iconAdapter.getCount();
        for (int i = 0; i < count; i++) {
            ImageView view = new ImageView(getContext(), null, R.attr.vpiIconPageIndicatorStyle);
            view.setImageResource(iconAdapter.getIconResId(i));
            this.mIconsLayout.addView(view);
        }
        if (this.mSelectedIndex > count) {
            this.mSelectedIndex = count - 1;
        }
        setCurrentItem(this.mSelectedIndex);
        requestLayout();
    }

    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    public void setCurrentItem(int item) {
        if (this.mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        this.mSelectedIndex = item;
        this.mViewPager.setCurrentItem(item);
        int tabCount = this.mIconsLayout.getChildCount();
        int i = 0;
        while (i < tabCount) {
            View child = this.mIconsLayout.getChildAt(i);
            boolean isSelected = i == item;
            child.setSelected(isSelected);
            if (isSelected) {
                animateToIcon(item);
            }
            i++;
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mListener = listener;
    }
}
