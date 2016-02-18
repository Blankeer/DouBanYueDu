package com.douban.book.reader.adapter;

import android.content.Context;
import android.view.View;
import com.douban.book.reader.util.ViewUtils;

public class ViewBinderAdapter<T> extends BaseArrayAdapter<T> {
    private Class<? extends ViewBinder<T>> mType;

    public ViewBinderAdapter(Context context, Class<? extends ViewBinder<T>> type) {
        super(context);
        this.mType = type;
    }

    public ViewBinderAdapter(Class<? extends ViewBinder<T>> type) {
        this.mType = type;
    }

    protected View newView() {
        return ViewUtils.createView(this.mType, getContext());
    }

    protected void bindView(View itemView, T data) {
        ((ViewBinder) itemView).bindData(data);
    }
}
