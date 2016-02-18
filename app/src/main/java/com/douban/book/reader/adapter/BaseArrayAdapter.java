package com.douban.book.reader.adapter;

import android.content.Context;
import android.os.Build.VERSION;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.douban.book.reader.app.App;
import com.douban.book.reader.util.ThemedUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class BaseArrayAdapter<T> extends ArrayAdapter<T> {
    protected LayoutInflater mLayoutInflater;

    public BaseArrayAdapter() {
        this(new ArrayList());
    }

    public BaseArrayAdapter(Context context) {
        this(context, 0, new ArrayList());
    }

    public BaseArrayAdapter(List<T> objects) {
        this(0, objects);
    }

    public BaseArrayAdapter(int resId, List<T> itemList) {
        this(App.get(), resId, itemList);
    }

    public BaseArrayAdapter(Context context, int resId, List<T> itemList) {
        super(context, resId, itemList);
        this.mLayoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    public void addAll(Collection<? extends T> collection) {
        if (VERSION.SDK_INT >= 11) {
            super.addAll(collection);
            return;
        }
        for (T item : collection) {
            super.add(item);
        }
    }

    public void replace(Collection<? extends T> collection) {
        clear();
        addAll(collection);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = newView();
        }
        bindView(convertView, getItem(position));
        ThemedUtils.updateViewTreeIfThemeChanged(convertView);
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    protected View newView() {
        return null;
    }

    protected void bindView(View itemView, T t) {
    }
}
