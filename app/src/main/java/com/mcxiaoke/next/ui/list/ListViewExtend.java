package com.mcxiaoke.next.ui.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListViewExtend extends ListView implements OnScrollListener {
    public static final boolean DEBUG = false;
    public static final int MODE_AUTO = 2;
    public static final int MODE_MANUAL = 1;
    public static final int MODE_NONE = 0;
    public static final String TAG;
    private AdapterExtend mEndlessAdapter;
    private int mFirstVisibleItem;
    private OnRefreshListener mOnRefreshListener;
    private OnScrollListener mOnScrollListener;
    private int mRefreshMode;
    private int mTotalItemCount;
    private int mVisibleItemCount;

    public interface OnRefreshListener {
        void onRefresh(ListViewExtend listViewExtend);
    }

    static {
        TAG = ListViewExtend.class.getSimpleName();
    }

    public ListViewExtend(Context context) {
        super(context);
        this.mRefreshMode = 0;
        setUp(context);
    }

    public ListViewExtend(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mRefreshMode = 0;
        setUp(context);
    }

    public ListViewExtend(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mRefreshMode = 0;
        setUp(context);
    }

    private void setUp(Context context) {
        setFadingEdgeLength(0);
        super.setOnScrollListener(this);
    }

    public void setOnScrollListener(OnScrollListener listener) {
        this.mOnScrollListener = listener;
    }

    public void setAdapter(ListAdapter adapter) {
        this.mEndlessAdapter = new AdapterExtend(getContext(), adapter);
        this.mEndlessAdapter.setRefreshListener(new OnRefreshListener() {
            public void onRefresh(ListViewExtend listView) {
                ListViewExtend.this.notifyRefresh();
            }
        });
        super.setAdapter(this.mEndlessAdapter);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mOnRefreshListener = listener;
    }

    public void setRefreshMode(int mode) {
        this.mRefreshMode = mode;
        checkManualRefresh();
    }

    public int getRefreshMode() {
        return this.mRefreshMode;
    }

    public void showFooterRefreshing() {
        this.mEndlessAdapter.setRefreshing(DEBUG);
    }

    public void showFooterEmpty() {
        this.mEndlessAdapter.showFooterEmpty();
    }

    public void showFooterText(int resId) {
        this.mEndlessAdapter.showFooterText(resId);
    }

    public void showFooterText(CharSequence text) {
        this.mEndlessAdapter.showFooterText(text);
    }

    public boolean isRefreshing() {
        return this.mEndlessAdapter.isRefreshing();
    }

    private void notifyRefresh() {
        if (this.mOnRefreshListener != null) {
            this.mOnRefreshListener.onRefresh(this);
        }
    }

    private void checkManualRefresh() {
        if (this.mEndlessAdapter != null) {
            if (this.mRefreshMode == MODE_MANUAL) {
                this.mEndlessAdapter.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        ListViewExtend.this.mEndlessAdapter.setRefreshing(true);
                    }
                });
            } else {
                this.mEndlessAdapter.setOnClickListener(null);
            }
        }
    }

    private void checkAutoRefresh() {
        if (this.mRefreshMode == MODE_AUTO && !isRefreshing() && this.mTotalItemCount != 0 && this.mVisibleItemCount < this.mTotalItemCount && this.mFirstVisibleItem + this.mVisibleItemCount >= this.mTotalItemCount) {
            this.mEndlessAdapter.setRefreshing(true);
        }
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (this.mOnScrollListener != null) {
            this.mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
        if (scrollState == 0) {
            checkAutoRefresh();
        }
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (this.mOnScrollListener != null) {
            this.mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        this.mFirstVisibleItem = firstVisibleItem;
        this.mVisibleItemCount = visibleItemCount;
        this.mTotalItemCount = totalItemCount;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }
}
