package com.douban.book.reader.adapter;

import android.view.View;
import android.widget.TextView;
import com.douban.book.reader.R;

public class SimpleStringAdapter<T> extends BaseArrayAdapter<T> {
    protected View newView() {
        return View.inflate(getContext(), R.layout.view_simple_list_item, null);
    }

    protected void bindView(View itemView, T item) {
        ((TextView) itemView).setText(strValueOf(item));
    }

    protected String strValueOf(T item) {
        return String.valueOf(item);
    }
}
