package com.douban.book.reader.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ListerViewBinderAdapter;
import com.douban.book.reader.adapter.OnLoadStatusChangedListener;
import com.douban.book.reader.adapter.Pageable;
import com.douban.book.reader.util.ExceptionUtils;
import com.mcxiaoke.next.ui.view.SimpleProgressView;
import in.srain.cube.views.GridViewWithHeaderAndFooter;

public class EndlessGridView extends GridViewWithHeaderAndFooter implements OnLoadStatusChangedListener, OnScrollListener {
    public static final String TAG;
    private int mFirstVisibleItem;
    private SimpleProgressView mFooter;
    private boolean mIsRefreshing;
    private OnLoadStatusChangedListener mOnLoadStatusChangedListener;
    private OnScrollListener mOnScrollListener;
    private int mScrollState;
    private int mTotalItemCount;
    private int mVisibleItemCount;

    static {
        TAG = EndlessGridView.class.getSimpleName();
    }

    public EndlessGridView(Context context) {
        super(context);
        this.mScrollState = 0;
        initialize(context);
    }

    public EndlessGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mScrollState = 0;
        initialize(context);
    }

    public EndlessGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mScrollState = 0;
        initialize(context);
    }

    private void initialize(Context context) {
        this.mFooter = new SimpleProgressView(context);
        this.mFooter.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                EndlessGridView.this.loadData();
            }
        });
        setFadingEdgeLength(0);
        super.setOnScrollListener(this);
    }

    public void setOnLoadStatusChangedListener(OnLoadStatusChangedListener listener) {
        this.mOnLoadStatusChangedListener = listener;
    }

    public void setOnScrollListener(OnScrollListener listener) {
        this.mOnScrollListener = listener;
    }

    public void setAdapter(ListAdapter adapter) {
        addFooterView(this.mFooter);
        if (adapter instanceof Pageable) {
            ((Pageable) adapter).setOnLoadStateChangedListener(this);
        }
        super.setAdapter(adapter);
    }

    public void onLoadingStarted() {
        this.mFooter.showProgress();
        this.mIsRefreshing = true;
        if (this.mOnLoadStatusChangedListener != null) {
            this.mOnLoadStatusChangedListener.onLoadingStarted();
        }
    }

    public void onLoadingEnd(Throwable e) {
        this.mIsRefreshing = false;
        if (e != null) {
            this.mFooter.showText(ExceptionUtils.getHumanReadableMessage(e, (int) R.string.general_load_failed));
        } else {
            this.mFooter.showEmpty();
        }
        if (this.mOnLoadStatusChangedListener != null) {
            this.mOnLoadStatusChangedListener.onLoadingEnd(e);
        }
    }

    public void loadData() {
        ListAdapter adapter = getOriginalAdapter();
        if (adapter instanceof Pageable) {
            ((Pageable) adapter).loadNextPage();
        }
    }

    public void refresh() {
        ListAdapter adapter = getOriginalAdapter();
        if (adapter instanceof ListerViewBinderAdapter) {
            ((ListerViewBinderAdapter) adapter).refresh();
        }
    }

    public void showFooterText(int resId) {
        showFooterText(getResources().getString(resId));
    }

    public void showFooterText(CharSequence text) {
        this.mIsRefreshing = false;
        this.mFooter.showText(text);
    }

    public void showFooterRefreshing() {
        if (!isRefreshing()) {
            this.mIsRefreshing = true;
            this.mFooter.showProgress();
        }
    }

    public void showFooterEmpty() {
        this.mIsRefreshing = false;
        this.mFooter.showEmpty();
    }

    private boolean isRefreshing() {
        return this.mIsRefreshing;
    }

    private void loadNextPageIfNeeded() {
        if (!isRefreshing() && this.mTotalItemCount != 0 && this.mVisibleItemCount < this.mTotalItemCount && this.mFirstVisibleItem + this.mVisibleItemCount >= this.mTotalItemCount) {
            ListAdapter adapter = getOriginalAdapter();
            if (adapter instanceof Pageable) {
                ((Pageable) adapter).loadNextPage();
            }
        }
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (this.mOnScrollListener != null) {
            this.mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
        this.mScrollState = scrollState;
        if (this.mScrollState == 0) {
            loadNextPageIfNeeded();
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
}
