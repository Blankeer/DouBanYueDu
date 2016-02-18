package com.mcxiaoke.next.ui.endless;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.mcxiaoke.next.ui.endless.EndlessAdapter.FooterState;
import com.mcxiaoke.next.ui.endless.EndlessAdapter.OnFooterStateChangeListener;
import com.mcxiaoke.next.ui.view.SimpleProgressView;

public class EndlessListView extends ListView implements OnScrollListener, OnFooterStateChangeListener {
    public static final boolean DEBUG = false;
    public static final String TAG;
    private EndlessAdapter mEndlessAdapter;
    private int mFirstVisibleItem;
    private SimpleProgressView mFooter;
    private boolean mLoading;
    private OnFooterRefreshListener mOnRefreshListener;
    private OnScrollListener mOnScrollListener;
    private RefreshMode mRefreshMode;
    private int mScrollState;
    private int mTotalItemCount;
    private int mVisibleItemCount;

    public interface OnFooterRefreshListener {
        void onFooterIdle(EndlessListView endlessListView);

        void onFooterRefresh(EndlessListView endlessListView);
    }

    public enum RefreshMode {
        AUTO,
        CLICK,
        NONE
    }

    static {
        TAG = EndlessListView.class.getSimpleName();
    }

    public EndlessListView(Context context) {
        super(context);
        this.mLoading = false;
        this.mScrollState = 0;
        this.mRefreshMode = RefreshMode.AUTO;
        initialize(context);
    }

    public EndlessListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mLoading = false;
        this.mScrollState = 0;
        this.mRefreshMode = RefreshMode.AUTO;
        initialize(context);
    }

    public EndlessListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mLoading = false;
        this.mScrollState = 0;
        this.mRefreshMode = RefreshMode.AUTO;
        initialize(context);
    }

    private void initialize(Context context) {
        this.mFooter = new SimpleProgressView(context);
        setFadingEdgeLength(0);
        super.setOnScrollListener(this);
    }

    public void setOnScrollListener(OnScrollListener listener) {
        this.mOnScrollListener = listener;
    }

    public void setAdapter(ListAdapter adapter) {
        this.mEndlessAdapter = new EndlessAdapter(getContext(), adapter);
        this.mEndlessAdapter.setFooterView(this.mFooter);
        this.mEndlessAdapter.setFooterStateChangeListener(this);
        super.setAdapter(this.mEndlessAdapter);
    }

    public void setOnFooterRefreshListener(OnFooterRefreshListener listener) {
        this.mOnRefreshListener = listener;
    }

    public void setRefreshMode(RefreshMode refreshMode) {
        this.mRefreshMode = refreshMode;
        checkFooterClick();
    }

    public boolean isAutoRefresh() {
        return RefreshMode.AUTO.equals(this.mRefreshMode);
    }

    public boolean isClickRefresh() {
        return RefreshMode.CLICK.equals(this.mRefreshMode);
    }

    public RefreshMode getRefreshMode() {
        return this.mRefreshMode;
    }

    public void showFooterText(int resId) {
        showFooterText(getResources().getString(resId));
    }

    public void showFooterText(CharSequence text) {
        this.mEndlessAdapter.setState(FooterState.IDLE, true);
        this.mFooter.showText(text);
    }

    public void showFooterRefreshing() {
        setRefreshing(false);
    }

    private void setRefreshing(boolean needNotify) {
        if (!isRefreshing()) {
            this.mEndlessAdapter.setState(FooterState.PROGRESS, needNotify);
            this.mFooter.showProgress();
        }
    }

    public void showFooterEmpty() {
        this.mEndlessAdapter.setState(FooterState.NONE, true);
        this.mFooter.showEmpty();
    }

    private boolean isRefreshing() {
        return this.mEndlessAdapter.isRefreshing();
    }

    private void checkFooterClick() {
        if (this.mFooter == null) {
            return;
        }
        if (isClickRefresh()) {
            this.mFooter.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    EndlessListView.this.setRefreshing(true);
                }
            });
        } else {
            this.mFooter.setOnClickListener(null);
        }
    }

    private void onRefresh() {
        if (this.mOnRefreshListener != null) {
            this.mOnRefreshListener.onFooterRefresh(this);
        }
    }

    private void onIdle() {
        if (this.mOnRefreshListener != null) {
            this.mOnRefreshListener.onFooterIdle(this);
        }
    }

    private void onNone() {
        if (this.mOnRefreshListener != null) {
            this.mOnRefreshListener.onFooterIdle(this);
        }
    }

    public void onFooterStateChanged(FooterState state, EndlessAdapter adapter) {
        if (FooterState.PROGRESS.equals(state)) {
            onRefresh();
        } else if (FooterState.IDLE.equals(state)) {
            this.mLoading = false;
            onIdle();
        } else {
            this.mLoading = false;
            onNone();
        }
    }

    private void checkRefresh() {
        if (isAutoRefresh() && !isRefreshing() && this.mTotalItemCount != 0 && this.mVisibleItemCount < this.mTotalItemCount && this.mFirstVisibleItem + this.mVisibleItemCount >= this.mTotalItemCount) {
            this.mLoading = true;
            setRefreshing(true);
        }
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (this.mOnScrollListener != null) {
            this.mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
        this.mScrollState = scrollState;
        if (this.mScrollState == 0) {
            checkRefresh();
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
