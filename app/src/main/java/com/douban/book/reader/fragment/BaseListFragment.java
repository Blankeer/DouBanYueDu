package com.douban.book.reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.douban.book.reader.R;
import com.douban.book.reader.adapter.BaseArrayAdapter;
import com.douban.book.reader.app.App;
import com.douban.book.reader.controller.TaskController;
import com.douban.book.reader.theme.ThemedAttrs;
import com.douban.book.reader.util.Logger;
import com.douban.book.reader.util.Res;
import com.douban.book.reader.util.StringUtils;
import com.douban.book.reader.util.ViewUtils;
import java.util.List;
import java.util.concurrent.Callable;
import natalya.os.TaskExecutor.TaskCallback;

public abstract class BaseListFragment<T> extends BaseFragment {
    protected BaseArrayAdapter<T> mAdapter;
    private TextView mEmptyView;
    private String mListTitle;
    protected ListView mListView;
    private ViewGroup mRootView;

    public abstract BaseArrayAdapter<T> onCreateAdapter();

    public abstract List<T> onLoadData();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_base_list, null);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mAdapter = onCreateAdapter();
        this.mListView = (ListView) view.findViewById(R.id.list);
        this.mEmptyView = (TextView) view.findViewById(R.id.empty_hint);
        this.mRootView = (ViewGroup) view.findViewById(R.id.root);
        init();
    }

    public void setEmptyHint(int resId) {
        setEmptyHint(Res.getString(resId));
    }

    public void setEmptyHint(String emptyHint) {
        this.mEmptyView.setText(emptyHint);
    }

    public void setListTitle(int resId) {
        this.mListTitle = Res.getString(resId);
    }

    private void addListTitle() {
        if (StringUtils.isNotEmpty(this.mListTitle)) {
            TextView title = new TextView(App.get());
            ThemedAttrs.ofView(title).append(R.attr.textColorArray, Integer.valueOf(R.array.content_text_color)).updateView();
            ViewUtils.setTextAppearance(App.get(), title, R.style.AppWidget_Text_Title_Column);
            title.setText(this.mListTitle);
            ViewUtils.setHorizontalPadding(title, Res.getDimensionPixelSize(R.dimen.page_horizontal_padding));
            ViewUtils.setTopPadding(title, Res.getDimensionPixelSize(R.dimen.general_subview_horizontal_padding_normal));
            title.setText(this.mListTitle);
            this.mListView.addHeaderView(title);
        }
    }

    private void init() {
        onListViewCreated(this.mListView);
        addListTitle();
        this.mListView.setAdapter(this.mAdapter);
        View emptyView = onCreateEmptyView();
        if (emptyView == null) {
            this.mListView.setEmptyView(this.mEmptyView);
        } else {
            this.mRootView.addView(emptyView);
            this.mListView.setEmptyView(emptyView);
        }
        int choiceMode = this.mListView.getChoiceMode();
        if (choiceMode != 0) {
            this.mListView.setChoiceMode(choiceMode);
        }
        loadData();
    }

    private void loadData() {
        TaskController.getInstance().execute(new Callable<List<T>>() {
            public List<T> call() throws Exception {
                BaseListFragment.this.showLoadingDialog();
                return BaseListFragment.this.onLoadData();
            }
        }, new TaskCallback<List<T>>() {
            public void onTaskSuccess(List<T> dataList, Bundle extras, Object object) {
                if (dataList != null) {
                    BaseListFragment.this.mAdapter.clear();
                    BaseListFragment.this.mAdapter.addAll(dataList);
                    BaseListFragment.this.mAdapter.notifyDataSetChanged();
                    BaseListFragment.this.onLoadSucceed();
                }
                BaseListFragment.this.dismissLoadingDialog();
            }

            public void onTaskFailure(Throwable e, Bundle extras) {
                Logger.e(BaseListFragment.this.TAG, e);
                BaseListFragment.this.onLoadFailed(e);
                BaseListFragment.this.dismissLoadingDialog();
            }
        }, this);
    }

    protected void onLoadSucceed() {
    }

    protected void onLoadFailed(Throwable e) {
    }

    protected void onListViewCreated(ListView listView) {
    }

    protected View onCreateEmptyView() {
        return null;
    }
}
