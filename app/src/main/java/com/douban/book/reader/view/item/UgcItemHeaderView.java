package com.douban.book.reader.view.item;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(2130903169)
public class UgcItemHeaderView extends RelativeLayout {
    @ViewById(2131558462)
    TextView mTitle;

    public UgcItemHeaderView(Context context) {
        super(context);
    }

    public void setTitle(CharSequence title) {
        this.mTitle.setText(title);
    }
}
