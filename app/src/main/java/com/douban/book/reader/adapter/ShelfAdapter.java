package com.douban.book.reader.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.douban.book.reader.app.App;
import com.douban.book.reader.entity.ShelfItem;
import com.douban.book.reader.util.ThemedUtils;
import com.douban.book.reader.view.ShelfItemView;
import com.douban.book.reader.view.ShelfItemView_;

public class ShelfAdapter extends BaseFilterAdapter<ShelfItem> {
    private boolean mIsInMultiChoiceMode;

    public ShelfAdapter(Context context) {
        super(context);
        this.mIsInMultiChoiceMode = false;
    }

    public long getItemId(int position) {
        if (getCount() > position) {
            return (long) ((ShelfItem) getItem(position)).id;
        }
        return -1;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ShelfItemView_.build(App.get());
        }
        ((ShelfItemView) convertView).setInMultiChoiceMode(this.mIsInMultiChoiceMode);
        ((ShelfItemView) convertView).bindData((ShelfItem) getItem(position));
        ThemedUtils.updateViewTreeIfThemeChanged(convertView);
        return convertView;
    }

    public boolean filter(CharSequence constraint, ShelfItem shelfItem) {
        return shelfItem.title.contains(constraint);
    }

    public void setMultiChoiceMode(boolean isInMultiChoiceMode) {
        this.mIsInMultiChoiceMode = isInMultiChoiceMode;
        notifyDataSetInvalidated();
    }
}
