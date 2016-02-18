package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import com.douban.book.reader.R;
import com.douban.book.reader.lib.view.ArkSwipeRefreshLayout;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.ViewUtils;

public abstract class BaseRefreshFragment extends BaseFragment implements OnRefreshListener, Refreshable {
    private boolean mPullToRefreshEnabled;
    private ArkSwipeRefreshLayout mSwipeRefreshLayout;

    protected abstract View onCreateContentView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle);

    public BaseRefreshFragment() {
        this.mPullToRefreshEnabled = true;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.frag_base_refresh, container, false);
        this.mSwipeRefreshLayout = (ArkSwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_base);
        this.mSwipeRefreshLayout.setOnRefreshListener(this);
        ViewUtils.enableIf(this.mPullToRefreshEnabled, this.mSwipeRefreshLayout);
        View contentView = onCreateContentView(inflater, this.mSwipeRefreshLayout, savedInstanceState);
        if (contentView != null) {
            this.mSwipeRefreshLayout.addView(contentView);
            setAsRootOfContentView(contentView);
        }
        View bottomView = onCreateBottomView();
        if (bottomView != null) {
            ViewGroup bottomViewBase = (ViewGroup) view.findViewById(R.id.base_refresh_root);
            if (bottomViewBase != null) {
                bottomViewBase.addView(bottomView, new LayoutParams(-1, -2, 80));
                bottomView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        ViewUtils.setBottomPadding(BaseRefreshFragment.this.mSwipeRefreshLayout, bottom - top);
                        BaseRefreshFragment.this.mSwipeRefreshLayout.setClipToPadding(false);
                    }
                });
            }
        }
        return view;
    }

    protected View onCreateBottomView() {
        return null;
    }

    protected void updateThemedViews() {
        super.updateThemedViews();
        this.mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(Res.getColor(R.array.page_highlight_bg_color));
        this.mSwipeRefreshLayout.setColorSchemeColors(Res.getColor(R.array.blue), Res.getColor(R.array.green));
    }

    protected void enablePullToRefresh(boolean enabled) {
        this.mPullToRefreshEnabled = enabled;
        ViewUtils.enableIf(enabled, this.mSwipeRefreshLayout);
    }

    protected void setScrollableChild(View view) {
        if (this.mSwipeRefreshLayout != null) {
            this.mSwipeRefreshLayout.setReferenceView(view);
        }
    }

    public void onRefresh() {
    }

    protected void performShowLoadingDialog(boolean blockPage) {
        if (this.mSwipeRefreshLayout != null) {
            this.mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    protected void performDismissLoadingDialog() {
        if (this.mSwipeRefreshLayout != null) {
            this.mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
