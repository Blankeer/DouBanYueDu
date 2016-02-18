package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.app.App;
import com.douban.book.reader.controller.TaskController;
import com.douban.book.reader.event.ColorThemeChangedEvent;
import com.douban.book.reader.manager.Lister;
import com.douban.book.reader.manager.cache.Identifiable;
import com.douban.book.reader.manager.exception.DataLoadException;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ThemedUtils;
import com.douban.book.reader.util.ToastUtils;
import com.douban.book.reader.util.ViewUtils;
import com.douban.book.reader.view.LoadErrorPageView.RefreshClickListener;
import com.mcxiaoke.next.ui.endless.EndlessListView;
import com.mcxiaoke.next.ui.endless.EndlessListView.OnFooterRefreshListener;
import com.mcxiaoke.next.ui.endless.EndlessListView.RefreshMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import natalya.os.TaskExecutor.TaskCallback;

public abstract class BaseEndlessListFragment<T extends Identifiable> extends BaseRefreshFragment implements Refreshable {
    protected BaseArrayAdapter<T> mAdapter;
    private String mEmptyHint;
    private List<View> mHeaderViews;
    private boolean mIsRefreshing;
    private boolean mIsSlientlyRefreshing;
    private View mListEmptyView;
    protected EndlessListView mListView;
    private Lister<T> mLister;
    private ViewGroup mRootView;
    private String mTitle;

    /* renamed from: com.douban.book.reader.fragment.BaseEndlessListFragment.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ Lister val$lister;

        AnonymousClass2(Lister lister) {
            this.val$lister = lister;
        }

        public void run() {
            TaskController.getInstance().cancelByCaller(BaseEndlessListFragment.this);
            BaseEndlessListFragment.this.mLister = this.val$lister;
            BaseEndlessListFragment.this.mAdapter.clear();
            BaseEndlessListFragment.this.loadData();
        }
    }

    public abstract BaseArrayAdapter<T> onCreateAdapter();

    public abstract Lister<T> onCreateLister();

    public BaseEndlessListFragment() {
        this.mIsRefreshing = false;
        this.mIsSlientlyRefreshing = false;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mLister = onCreateLister();
        this.mAdapter = onCreateAdapter();
    }

    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_base_endless_list, null);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mListView = (EndlessListView) view.findViewById(R.id.list);
        this.mRootView = (ViewGroup) view.findViewById(R.id.root);
        setScrollableChild(this.mListView);
        init();
    }

    public void addHeaderView(View headerView) {
        if (this.mHeaderViews == null) {
            this.mHeaderViews = new ArrayList();
        }
        this.mHeaderViews.add(headerView);
        if (this.mListView != null) {
            this.mListView.addHeaderView(headerView);
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
        this.mListView.setRefreshMode(RefreshMode.NONE);
        if (this.mListEmptyView != null) {
            this.mListEmptyView.setVisibility(0);
            this.mListView.showFooterEmpty();
            return;
        }
        if (StringUtils.isNotEmpty(this.mEmptyHint)) {
            this.mListView.showFooterText(this.mEmptyHint);
        } else {
            this.mListView.showFooterEmpty();
        }
    }

    private void init() {
        onListViewCreated(this.mListView);
        addListTitle();
        this.mListView.setAdapter(this.mAdapter);
        View emptyView = onCreateEmptyView();
        if (emptyView != null) {
            this.mListEmptyView = emptyView;
            this.mListEmptyView.setVisibility(8);
            this.mRootView.addView(this.mListEmptyView);
        }
        int choiceMode = this.mListView.getChoiceMode();
        if (choiceMode != 0) {
            this.mListView.setChoiceMode(choiceMode);
        }
        this.mListView.setOnFooterRefreshListener(new OnFooterRefreshListener() {
            public void onFooterRefresh(EndlessListView listView) {
                if (BaseEndlessListFragment.this.mLister != null && BaseEndlessListFragment.this.mLister.hasMore()) {
                    BaseEndlessListFragment.this.loadMore();
                }
            }

            public void onFooterIdle(EndlessListView listView) {
                if (BaseEndlessListFragment.this.mLister == null || !BaseEndlessListFragment.this.mLister.hasMore()) {
                    BaseEndlessListFragment.this.mListView.setRefreshMode(RefreshMode.NONE);
                }
            }
        });
        if (this.mLister != null) {
            loadData();
        }
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
            replaceLister(onCreateLister());
            this.mListView.setRefreshMode(RefreshMode.AUTO);
            if (this.mListEmptyView != null) {
                this.mListEmptyView.setVisibility(8);
            }
        }
    }

    public void replaceLister(Lister<T> lister) {
        if (lister != null) {
            App.get().runOnUiThread(new AnonymousClass2(lister));
        }
    }

    public void refreshSilently() {
        this.mIsSlientlyRefreshing = true;
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

    private void loadData() {
        if (isRefreshing()) {
            showLoadingDialog();
        } else {
            this.mListView.showFooterRefreshing();
        }
        loadMore();
    }

    private void loadMore() {
        this.mListView.setRefreshMode(RefreshMode.AUTO);
        TaskController.getInstance().execute(new Callable<List<T>>() {
            public List<T> call() throws Exception {
                return BaseEndlessListFragment.this.mLister.loadMore();
            }
        }, new TaskCallback<List<T>>() {
            public void onTaskSuccess(List<T> list, Bundle extras, Object object) {
                if (BaseEndlessListFragment.this.isRefreshing()) {
                    if (!BaseEndlessListFragment.this.mIsSlientlyRefreshing) {
                        ToastUtils.showToast((int) R.string.toast_general_refresh_success);
                        BaseEndlessListFragment.this.mIsSlientlyRefreshing = false;
                    }
                    BaseEndlessListFragment.this.mIsRefreshing = false;
                    BaseEndlessListFragment.this.dismissLoadingDialog();
                }
                BaseEndlessListFragment.this.onLoadCompleted(list);
            }

            public void onTaskFailure(Throwable e, Bundle extras) {
                if (BaseEndlessListFragment.this.isRefreshing()) {
                    if (!BaseEndlessListFragment.this.mIsSlientlyRefreshing) {
                        ToastUtils.showToast((int) R.string.toast_general_refresh_failed);
                        BaseEndlessListFragment.this.mIsSlientlyRefreshing = false;
                    }
                    BaseEndlessListFragment.this.mIsRefreshing = false;
                    BaseEndlessListFragment.this.dismissLoadingDialog();
                }
                BaseEndlessListFragment.this.onLoadFailed(e);
            }
        }, this);
    }

    protected void onLoadFailed(Throwable e) {
        if (((this.mListView.getCount() - this.mListView.getHeaderViewsCount()) - this.mListView.getFooterViewsCount()) - 1 <= 0 && (e instanceof DataLoadException)) {
            showLoadErrorPage((DataLoadException) e, new RefreshClickListener() {
                public void onClick() {
                    BaseEndlessListFragment.this.loadData();
                }
            });
        }
        this.mListView.showFooterText((int) R.string.general_load_failed);
        this.mListView.setRefreshMode(RefreshMode.CLICK);
        Logger.e(this.TAG, e);
    }

    protected void onLoadCompleted(List<T> data) {
        this.mAdapter.addAll(data);
        if (this.mAdapter.isEmpty()) {
            onListLoadEmpty();
        } else {
            this.mListView.showFooterEmpty();
        }
    }

    protected void notifyDataChanged() {
        App.get().runOnUiThread(new Runnable() {
            public void run() {
                BaseEndlessListFragment.this.mAdapter.notifyDataSetChanged();
            }
        });
    }

    protected void onListViewCreated(EndlessListView listView) {
    }

    protected View onCreateEmptyView() {
        return null;
    }
}
