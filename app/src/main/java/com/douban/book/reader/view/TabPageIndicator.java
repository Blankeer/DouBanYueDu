package com.douban.book.reader.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.Utils;
import com.douban.book.reader.util.ViewUtils;
import com.viewpagerindicator.PageIndicator;

public class TabPageIndicator extends LinearLayout implements PageIndicator {
    private PagerAdapter mAdapter;
    private DataSetObserver mDataSetObserver;
    private OnPageChangeListener mPageListener;
    private ViewPager mViewPager;

    /* renamed from: com.douban.book.reader.view.TabPageIndicator.2 */
    class AnonymousClass2 implements OnClickListener {
        final /* synthetic */ int val$index;
        final /* synthetic */ TextView val$tabItem;

        AnonymousClass2(int i, TextView textView) {
            this.val$index = i;
            this.val$tabItem = textView;
        }

        public void onClick(View v) {
            if (TabPageIndicator.this.mViewPager == null) {
                return;
            }
            if (TabPageIndicator.this.mViewPager.getCurrentItem() != this.val$index) {
                TabPageIndicator.this.mViewPager.setCurrentItem(this.val$index);
            } else {
                this.val$tabItem.setSelected(true);
            }
        }
    }

    public TabPageIndicator(Context context) {
        super(context);
        this.mDataSetObserver = new DataSetObserver() {
            public void onChanged() {
                super.onChanged();
                TabPageIndicator.this.refreshTabViews();
            }
        };
    }

    public TabPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDataSetObserver = new DataSetObserver() {
            public void onChanged() {
                super.onChanged();
                TabPageIndicator.this.refreshTabViews();
            }
        };
    }

    public TabPageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mDataSetObserver = new DataSetObserver() {
            public void onChanged() {
                super.onChanged();
                TabPageIndicator.this.refreshTabViews();
            }
        };
    }

    public void setViewPager(ViewPager viewPager) {
        setViewPager(viewPager, 0);
    }

    public void setViewPager(ViewPager viewPager, int initialPosition) {
        if (viewPager == null) {
            throw new IllegalArgumentException("viewPager cannot be null");
        }
        this.mViewPager = viewPager;
        this.mViewPager.setOnPageChangeListener(this);
        this.mViewPager.setCurrentItem(initialPosition);
        this.mAdapter = viewPager.getAdapter();
        this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
        refreshTabViews();
    }

    public void setAdapter(PagerAdapter adapter) {
        this.mAdapter = adapter;
        this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
        refreshTabViews();
    }

    protected PagerAdapter getAdapter() {
        return this.mAdapter;
    }

    protected View createTabIndicatorItem(int index) {
        TextView tabItem = new TextView(getContext());
        ViewUtils.of(tabItem).width(0).height(Res.getDimensionPixelSize(R.dimen.height_tab_indicator)).weight(1).commit();
        tabItem.setTextSize(0, Res.getDimension(R.dimen.general_font_size_normal));
        tabItem.setGravity(17);
        ThemedAttrs.ofView(tabItem).append(R.attr.textColorArray, Integer.valueOf(R.array.tab_item_text_color)).append(R.attr.backgroundDrawableArray, Integer.valueOf(R.array.tab_item_bg)).updateView();
        tabItem.setText(getAdapter().getPageTitle(index));
        tabItem.setOnClickListener(new AnonymousClass2(index, tabItem));
        return tabItem;
    }

    private void refreshTabViews() {
        removeAllViews();
        PagerAdapter adapter = this.mAdapter;
        for (int i = 0; i < adapter.getCount(); i++) {
            View tabItem = createTabIndicatorItem(i);
            if (tabItem != null) {
                if (i > 0) {
                    addView(ViewUtils.createVerticalDivider());
                }
                addView(tabItem);
            }
        }
        Utils.changeFonts(this);
        updateSelectedItem();
    }

    protected void updateSelectedItem() {
        if (this.mViewPager != null) {
            int currentItem = this.mViewPager.getCurrentItem();
            PagerAdapter adapter = this.mViewPager.getAdapter();
            int i = 0;
            while (i < adapter.getCount()) {
                View tab = getChildAt(i * 2);
                if (tab != null) {
                    tab.setSelected(i == currentItem);
                }
                i++;
            }
        }
    }

    public void setCurrentItem(int item) {
        if (this.mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        this.mViewPager.setCurrentItem(item);
        invalidate();
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mPageListener = listener;
    }

    public void notifyDataSetChanged() {
        invalidate();
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (this.mPageListener != null) {
            this.mPageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    public void onPageSelected(int position) {
        if (this.mPageListener != null) {
            this.mPageListener.onPageSelected(position);
        }
        updateSelectedItem();
    }

    public void onPageScrollStateChanged(int state) {
        if (this.mPageListener != null) {
            this.mPageListener.onPageScrollStateChanged(state);
        }
    }
}
