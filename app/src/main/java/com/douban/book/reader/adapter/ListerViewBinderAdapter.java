package com.douban.book.reader.adapter;

import android.os.Bundle;
import com.douban.book.reader.controller.TaskController;
import com.douban.book.reader.manager.Lister;
import com.douban.book.reader.manager.cache.Identifiable;
import java.util.List;
import java.util.concurrent.Callable;
import natalya.os.TaskExecutor.TaskCallback;

public class ListerViewBinderAdapter<T extends Identifiable> extends ViewBinderAdapter<T> implements Pageable {
    private OnLoadStatusChangedListener mListener;
    private Lister<T> mLister;

    public ListerViewBinderAdapter(Lister<T> lister, Class<? extends ViewBinder<T>> type) {
        super(type);
        this.mLister = lister;
    }

    public void refresh() {
        clear();
        this.mLister.reset();
        loadNextPage();
    }

    public void setOnLoadStateChangedListener(OnLoadStatusChangedListener listener) {
        this.mListener = listener;
    }

    public void loadNextPage() {
        if (this.mLister.hasMore()) {
            if (this.mListener != null) {
                this.mListener.onLoadingStarted();
            }
            TaskController.getInstance().execute(new Callable<List<T>>() {
                public List<T> call() throws Exception {
                    return ListerViewBinderAdapter.this.mLister.loadMore();
                }
            }, new TaskCallback<List<T>>() {
                public void onTaskSuccess(List<T> list, Bundle extras, Object object) {
                    ListerViewBinderAdapter.this.addAll(list);
                    if (ListerViewBinderAdapter.this.mListener != null) {
                        ListerViewBinderAdapter.this.mListener.onLoadingEnd(null);
                    }
                }

                public void onTaskFailure(Throwable e, Bundle extras) {
                    if (ListerViewBinderAdapter.this.mListener != null) {
                        ListerViewBinderAdapter.this.mListener.onLoadingEnd(e);
                    }
                }
            }, this);
        }
    }
}
