package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.ListerViewBinderAdapter;
import com.douban.book.reader.adapter.OnLoadStatusChangedListener;
import com.douban.book.reader.app.App;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.lib.view.EndlessGridView;
import com.douban.book.reader.manager.cache.Identifiable;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.ExceptionUtils;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ThemedUtils;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.LoadErrorPageView.RefreshClickListener;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseEndlessGridFragment<T extends Identifiable> extends BaseRefreshFragment implements Refreshable, OnLoadStatusChangedListener {
    protected ListerViewBinderAdapter<T> mAdapter;
    private String mEmptyHint;
    protected EndlessGridView mGridView;
    private List<View> mHeaderViews;
    private boolean mIsRefreshing;
    private boolean mIsSilentlyRefreshing;
    private View mListEmptyView;
    private ViewGroup mRootView;
    private String mTitle;

    public abstract ListerViewBinderAdapter<T> onCreateAdapter();

    public BaseEndlessGridFragment() {
        this.mIsRefreshing = false;
        this.mIsSilentlyRefreshing = false;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAdapter = onCreateAdapter();
    }

    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_base_endless_grid, null);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mGridView = (EndlessGridView) view.findViewById(R.id.list);
        this.mGridView.setOnLoadStatusChangedListener(this);
        this.mGridView.setColumnWidth(Math.round(Math.min((((float) App.get().getPageWidth()) * 0.5f) - Res.getDimension(R.dimen.page_horizontal_padding), Res.getDimension(R.dimen.boxed_gift_view_item_width))));
        this.mRootView = (ViewGroup) view.findViewById(R.id.root);
        setScrollableChild(this.mGridView);
        init();
    }

    public void addHeaderView(View headerView) {
        if (this.mHeaderViews == null) {
            this.mHeaderViews = new ArrayList();
        }
        this.mHeaderViews.add(headerView);
        if (this.mGridView != null) {
            this.mGridView.addHeaderView(headerView);
        }
    }

    public void onEventMainThread(ColorThemeChangedEvent event) {
        if (this.mHeaderViews != null) {
            for (View headerView : this.mHeaderViews) {
                ThemedUtils.updateViewTree(headerView);
            }
        }
    }

    private boolean isRefreshing() {
        return this.mIsRefreshing;
    }

    private void onListLoadEmpty() {
        if (this.mListEmptyView != null) {
            this.mListEmptyView.setVisibility(0);
            this.mGridView.showFooterEmpty();
            return;
        }
        if (StringUtils.isNotEmpty(this.mEmptyHint)) {
            this.mGridView.showFooterText(this.mEmptyHint);
        } else {
            this.mGridView.showFooterEmpty();
        }
    }

    private void init() {
        onListViewCreated(this.mGridView);
        addListTitle();
        this.mGridView.setAdapter(this.mAdapter);
        View emptyView = onCreateEmptyView();
        if (emptyView != null) {
            this.mListEmptyView = emptyView;
            this.mListEmptyView.setVisibility(8);
            this.mRootView.addView(this.mListEmptyView);
        }
        int choiceMode = this.mGridView.getChoiceMode();
        if (choiceMode != 0) {
            this.mGridView.setChoiceMode(choiceMode);
        }
        this.mGridView.loadData();
    }

    public void setListTitle(String title) {
        this.mTitle = title;
    }

    public void setEmptyHint(int resId) {
        setEmptyHint(Res.getString(resId));
    }

    public void setEmptyHint(String emptyHint) {
        this.mEmptyHint = emptyHint;
    }

    public void setListTitle(int resId) {
        this.mTitle = Res.getString(resId);
    }

    public void onRefresh() {
        if (!isRefreshing()) {
            this.mIsRefreshing = true;
            this.mGridView.refresh();
            if (this.mListEmptyView != null) {
                this.mListEmptyView.setVisibility(8);
            }
        }
    }

    public void refreshSilently() {
        this.mIsSilentlyRefreshing = true;
        onRefresh();
    }

    private void addListTitle() {
        if (StringUtils.isNotEmpty(this.mTitle)) {
            TextView title = new TextView(App.get());
            ViewUtils.setTextAppearance(App.get(), title, R.style.AppWidget_Text_Title_Column);
            title.setText(this.mTitle);
            ViewUtils.setHorizontalPadding(title, Res.getDimensionPixelSize(R.dimen.page_horizontal_padding));
            ViewUtils.setTopPadding(title, Res.getDimensionPixelSize(R.dimen.general_subview_horizontal_padding_normal));
            ThemedAttrs.ofView(title).append(R.attr.textColorArray, Integer.valueOf(R.array.content_text_color));
            ThemedUtils.updateViewTree(title);
            addHeaderView(title);
        }
    }

    public void onLoadingStarted() {
        if (isRefreshing()) {
            showLoadingDialog();
            this.mGridView.showFooterEmpty();
        }
    }

    public void onLoadingEnd(Throwable exception) {
        dismissLoadingDialog();
        this.mIsRefreshing = false;
        if (exception == null) {
            if (isRefreshing() && !this.mIsSilentlyRefreshing) {
                ToastUtils.showToast((int) R.string.toast_general_refresh_success);
                this.mIsSilentlyRefreshing = false;
            }
            onLoadCompleted();
            return;
        }
        if (isRefreshing()) {
            if (!this.mIsSilentlyRefreshing) {
                ToastUtils.showToast((int) R.string.toast_general_refresh_failed);
                this.mIsSilentlyRefreshing = false;
            }
            this.mIsRefreshing = false;
            dismissLoadingDialog();
        }
        onLoadFailed(exception);
    }

    protected void onLoadFailed(Throwable e) {
        if (((this.mGridView.getCount() - this.mGridView.getHeaderViewCount()) - this.mGridView.getFooterViewCount()) - 1 <= 0 && (e instanceof DataLoadException)) {
            showLoadErrorPage((DataLoadException) e, new RefreshClickListener() {
                public void onClick() {
                    BaseEndlessGridFragment.this.mGridView.loadData();
                }
            });
        }
        this.mGridView.showFooterText(ExceptionUtils.getHumanReadableMessage(e, (int) R.string.general_load_failed));
        Logger.e(this.TAG, e);
    }

    protected void onLoadCompleted() {
        if (this.mAdapter.isEmpty()) {
            onListLoadEmpty();
        } else {
            this.mGridView.showFooterEmpty();
        }
    }

    protected void notifyDataChanged() {
        App.get().runOnUiThread(new Runnable() {
            public void run() {
                BaseEndlessGridFragment.this.mAdapter.notifyDataSetChanged();
            }
        });
    }

    protected void onListViewCreated(EndlessGridView listView) {
    }

    protected View onCreateEmptyView() {
        return null;
    }
}
